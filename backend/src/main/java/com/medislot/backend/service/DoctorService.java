package com.medislot.backend.service;

import com.medislot.backend.dto.DoctorProfileUpdateRequest;
import com.medislot.backend.dto.DoctorRegistrationRequest;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.repository.AppointmentRepository;
import com.medislot.backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service public class DoctorService {
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;
    private final String registrationCode;
    private final NotificationService notifications;
    private final WaitingListService waitingList;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public DoctorService(DoctorRepository doctors, AppointmentRepository appointments,
            @Value("${app.doctor-registration-code:}") String registrationCode) {
        this(doctors,appointments,registrationCode,null,null);
    }
    @Autowired
    public DoctorService(DoctorRepository doctors,AppointmentRepository appointments,
            @Value("${app.doctor-registration-code:}") String registrationCode,NotificationService notifications,WaitingListService waitingList) {
        this.doctors=doctors; this.appointments=appointments; this.registrationCode=registrationCode;this.notifications=notifications;this.waitingList=waitingList;
    }
    @Transactional public Doctor register(DoctorRegistrationRequest request) {
        if (registrationCode.isBlank() || !registrationCode.equals(request.registrationCode()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Doctor registration is not enabled or the code is invalid.");
        Doctor existing = doctors.findByEmail(request.email().trim().toLowerCase()).orElse(null);
        if (existing != null && existing.getPasswordHash() != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
        if (existing != null) {
            existing.setPasswordHash(encoder.encode(request.password()));
            existing.setActive(true);
            return doctors.save(existing);
        }
        Doctor doctor=new Doctor(null, request.name().trim(), request.specialization().trim(), List.of());
        doctor.setEmail(request.email().trim().toLowerCase());
        doctor.setPasswordHash(encoder.encode(request.password()));
        return doctors.save(doctor);
    }
    public Doctor login(String email, String password) {
        Doctor doctor=doctors.findByEmail(email.trim().toLowerCase()).filter(Doctor::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (doctor.getPasswordHash()==null || !encoder.matches(password, doctor.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        return doctor;
    }
    public Doctor profile(String email) { return doctors.findByEmail(email).filter(Doctor::isActive).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); }
    @Transactional public Doctor updateProfile(String email, DoctorProfileUpdateRequest request) {
        Doctor doctor=profile(email); doctor.setName(request.name().trim()); doctor.setSpecialization(request.specialization().trim()); doctor.setConsultationDurationMinutes(request.consultationDurationMinutes()); return doctors.save(doctor);
    }
    @Transactional public Doctor setBlockedDates(String email, List<LocalDate> dates) {
        if (dates == null || dates.stream().anyMatch(java.util.Objects::isNull))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blocked dates are invalid.");
        Doctor doctor=profile(email);
        for (Appointment appointment : appointments.findAll()) {
            if (appointment.getDoctorId().equals(doctor.getId()) && dates.contains(appointment.getAppointmentDate())
                    && !"CANCELLED".equals(appointment.getStatus()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A date with an active appointment cannot be blocked.");
        }
        doctor.setBlockedDates(new java.util.HashSet<>(dates)); return doctors.save(doctor);
    }
    @Transactional public Doctor setAvailability(String email, List<LocalTime> slots) {
        if (slots==null || slots.stream().anyMatch(java.util.Objects::isNull) || slots.stream().distinct().count()!=slots.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Availability slots must be valid and unique.");
        Doctor doctor=profile(email);
        for (Appointment appointment : appointments.findAll()) {
            if (appointment.getDoctorId().equals(doctor.getId()) && !"CANCELLED".equals(appointment.getStatus())
                    && !slots.contains(appointment.getAppointmentTime()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Availability cannot remove a slot with an active appointment.");
        }
        doctor.setAvailableSlots(slots.stream().sorted().toList()); return doctors.save(doctor);
    }
    public List<Appointment> appointments(String email) {
        Doctor doctor=profile(email);
        return appointments.findAll().stream().filter(a -> doctor.getId().equals(a.getDoctorId())).toList();
    }
    @Transactional public Appointment updateAppointmentStatus(String email, Long id, String status) {
        Doctor doctor=profile(email);
        Appointment appointment=appointments.findById(id).filter(a -> doctor.getId().equals(a.getDoctorId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (status == null || !List.of("CHECKED_IN", "IN_CONSULTATION", "COMPLETED", "CANCELLED").contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported consultation status.");
        boolean validTransition=switch(appointment.getStatus()) {
            case "CONFIRMED" -> List.of("CHECKED_IN","CANCELLED").contains(status);
            case "CHECKED_IN" -> List.of("IN_CONSULTATION","CANCELLED").contains(status);
            case "IN_CONSULTATION" -> List.of("COMPLETED","CANCELLED").contains(status);
            default -> false;
        };
        if(!validTransition) throw new ResponseStatusException(HttpStatus.CONFLICT,"Appointment status transition is not allowed.");
        appointment.setStatus(status); Appointment updated=appointments.update(appointment);
        if(notifications!=null) notifications.notifyPatient(appointment.getPatient(),"DOCTOR_STATUS","Your appointment "+id+" status is now "+status+".","doctor-status-"+id+"-"+status,"MediSlot appointment update");
        if("CANCELLED".equals(status)&&waitingList!=null) waitingList.offerNext(doctor.getSpecialization(),appointment.getAppointmentDate(),appointment.getAppointmentTime(),doctor);
        return updated;
    }
}
