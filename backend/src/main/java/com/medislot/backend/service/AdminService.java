package com.medislot.backend.service;

import com.medislot.backend.dto.AdminDoctorRequest;
import com.medislot.backend.model.*;
import com.medislot.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service public class AdminService {
    private final AdminAccountRepository admins;
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;
    private final NotificationService notifications;
    private final WaitingListService waitingList;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    public AdminService(AdminAccountRepository admins, PatientRepository patients, DoctorRepository doctors, AppointmentRepository appointments,NotificationService notifications,WaitingListService waitingList) {
        this.admins=admins; this.patients=patients; this.doctors=doctors; this.appointments=appointments;this.notifications=notifications;this.waitingList=waitingList;
    }
    public AdminAccount login(String email, String password) {
        AdminAccount admin=admins.findByEmailIgnoreCase(email.trim()).filter(AdminAccount::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
        if (!encoder.matches(password, admin.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        return admin;
    }
    public Map<String,Object> dashboard() {
        List<Appointment> all=appointments.findAll(); LocalDate today=LocalDate.now();
        return Map.of("totalPatients",patients.findAll().size(),"totalDoctors",doctors.findAll().size(),"totalAppointments",all.size(),
                "completedAppointments",count(all,"COMPLETED"),"cancelledAppointments",count(all,"CANCELLED"),
                "pendingAppointments",count(all,"CONFIRMED"),"todayAppointments",all.stream().filter(a -> today.equals(a.getAppointmentDate())).count());
    }
    private long count(List<Appointment> list, String status) { return list.stream().filter(a -> status.equals(a.getStatus())).count(); }
    public List<Patient> patients() { return patients.findAll(); }
    public List<Doctor> doctors() { return doctors.findAll(); }
    public List<Appointment> appointments(LocalDate date, String status) {
        return appointments.findAll().stream().filter(a -> date==null || date.equals(a.getAppointmentDate()))
                .filter(a -> status==null || status.isBlank() || status.equalsIgnoreCase(a.getStatus())).toList();
    }
    @Transactional public Patient patientActive(Long id, boolean active) {
        Patient p=patients.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        p.setActive(active); return patients.save(p);
    }
    @Transactional public Doctor doctorActive(Long id, boolean active) {
        Doctor d=doctors.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        d.setActive(active); return doctors.save(d);
    }
    @Transactional public Doctor addDoctor(AdminDoctorRequest request) {
        if (request.email()!=null && doctors.existsByEmail(request.email().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Doctor email already exists.");
        Doctor d=new Doctor(null,request.name().trim(),request.specialization().trim(),List.of());
        if(request.email()!=null) d.setEmail(request.email().trim().toLowerCase());
        d.setActive(false); // Doctor must claim the provisioned profile using the configured onboarding code.
        return doctors.save(d);
    }
    @Transactional public Doctor updateDoctor(Long id, AdminDoctorRequest request) {
        Doctor d=doctors.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        d.setName(request.name().trim()); d.setSpecialization(request.specialization().trim());
        if(request.email()!=null) d.setEmail(request.email().trim().toLowerCase());
        return doctors.save(d);
    }
    @Transactional public Appointment updateAppointmentStatus(Long id, String status) {
        if(status==null || !List.of("CONFIRMED","CHECKED_IN","IN_CONSULTATION","COMPLETED","CANCELLED").contains(status))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unsupported appointment status.");
        Appointment a=appointments.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        a.setStatus(status); Appointment updated=appointments.update(a);
        notifications.notifyPatient(a.getPatient(),"ADMIN_STATUS","Your appointment "+id+" status is now "+status+".","admin-status-"+id+"-"+status,"MediSlot appointment update");
        if("CANCELLED".equals(status)) waitingList.offerNext(a.getDoctor().getSpecialization(),a.getAppointmentDate(),a.getAppointmentTime(),a.getDoctor());
        return updated;
    }
    public Map<String,Object> analytics(LocalDate from, LocalDate to) {
        List<Appointment> all=appointments.findAll().stream().filter(a -> from==null || !a.getAppointmentDate().isBefore(from))
                .filter(a -> to==null || !a.getAppointmentDate().isAfter(to)).toList();
        Map<LocalDate,Long> daily=all.stream().collect(Collectors.groupingBy(Appointment::getAppointmentDate,TreeMap::new,Collectors.counting()));
        Map<java.time.YearMonth,Long> monthly=all.stream().collect(Collectors.groupingBy(a->java.time.YearMonth.from(a.getAppointmentDate()),TreeMap::new,Collectors.counting()));
        Map<String,Long> doctorsByCount=all.stream().collect(Collectors.groupingBy(Appointment::getDoctorName,TreeMap::new,Collectors.counting()));
        Map<String,Long> specializations=all.stream().collect(Collectors.groupingBy(a -> doctors.findById(a.getDoctorId()).map(Doctor::getSpecialization).orElse("Unknown"),TreeMap::new,Collectors.counting()));
        Map<String,Long> peakHours=all.stream().collect(Collectors.groupingBy(a -> a.getAppointmentTime().getHour()+":00",TreeMap::new,Collectors.counting()));
        double cancellationRate=all.isEmpty()?0.0:100.0*count(all,"CANCELLED")/all.size();
        return Map.of("periodAppointments",all.size(),"cancellationRatePercent",cancellationRate,"dailyBookings",daily,"monthlyBookings",monthly,"doctorAppointments",doctorsByCount,"popularSpecializations",specializations,"historicalPeakHours",peakHours);
    }
}
