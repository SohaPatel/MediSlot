
package com.medislot.backend.service;

import com.medislot.backend.algorithm.BipartiteMatchingAlgorithm;
import com.medislot.backend.dto.AppointmentRequest;
import com.medislot.backend.dto.RescheduleRequest;
import com.medislot.backend.dto.DoctorRecommendation;
import com.medislot.backend.algorithm.DoctorSearchTrie;
import com.medislot.backend.algorithm.AppointmentBinarySearch;
import com.medislot.backend.repository.DoctorReviewRepository;
import com.medislot.backend.dto.QueueStatus;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.model.Patient;
import com.medislot.backend.repository.AppointmentRepository;
import com.medislot.backend.repository.DoctorRepository;
import com.medislot.backend.repository.PatientRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final WaitingListService waitingListService;
    private final DoctorReviewRepository reviews;
    private final NotificationService notificationService;

    private final BipartiteMatchingAlgorithm matchingAlgorithm =
            new BipartiteMatchingAlgorithm();

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            WaitingListService waitingListService,
            DoctorReviewRepository reviews,
            NotificationService notificationService) {

        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.waitingListService = waitingListService;
        this.reviews = reviews;
        this.notificationService = notificationService;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll().stream().filter(Doctor::isActive).toList();
    }

    public List<DoctorRecommendation> recommendDoctors(String specialization, java.time.LocalDate date, LocalTime time) {
        return doctorRepository.findAll().stream().filter(Doctor::isActive).map(doctor -> {
            boolean specializationMatch = specialization != null && doctor.getSpecialization().equalsIgnoreCase(specialization);
            boolean available = date != null && time != null && doctor.getAvailableSlots().contains(time)
                    && !appointmentRepository.existsByDoctorIdAndDateAndTime(doctor.getId(), date, time);
            Double averageRating = reviews.averageRating(doctor.getId());
            long reviewCount = reviews.countByDoctor_Id(doctor.getId());
            int ratingPoints = averageRating == null ? 0 : (int) Math.round(averageRating / 5.0 * 20.0);
            int score = (specializationMatch ? 50 : 0) + (available ? 30 : 0) + ratingPoints;
            List<String> reasons = new ArrayList<>();
            if (specializationMatch) reasons.add("Matches your selected specialization");
            if (available) reasons.add("Has the requested slot available");
            if (averageRating != null) reasons.add(String.format(java.util.Locale.ROOT, "Rated %.1f/5 from %d reviews", averageRating, reviewCount));
            return new DoctorRecommendation(doctor, score, reasons);
        }).sorted(java.util.Comparator.comparingInt(DoctorRecommendation::score).reversed()).toList();
    }

    public List<String> searchDoctors(String prefix) {
        DoctorSearchTrie trie = new DoctorSearchTrie();
        for (Doctor doctor : getAllDoctors()) {
            trie.add(doctor.getName());
            trie.add(doctor.getSpecialization());
        }
        return trie.suggest(prefix, 10);
    }

    @Transactional
    public Appointment bookAppointment(AppointmentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Appointment request cannot be empty."
            );
        }

        if (request.getPatientId() == null) {
            throw new IllegalArgumentException(
                    "Patient ID is required."
            );
        }

        if (request.getSpecialization() == null ||
                request.getSpecialization().isBlank()) {
            throw new IllegalArgumentException(
                    "Doctor specialization is required."
            );
        }

        if (request.getAppointmentDate() == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (request.getAppointmentDate().isBefore(
                java.time.LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .filter(Patient::isActive)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient not found. Please register first."
                ));

        List<Doctor> matchingDoctors = doctorRepository.findAll()
                .stream()
                .filter(Doctor::isActive)
                .filter(doctor -> doctor.getSpecialization()
                        .equalsIgnoreCase(request.getSpecialization()))
                .filter(doctor -> !doctor.getBlockedDates().contains(request.getAppointmentDate()))
                .toList();

        if (matchingDoctors.isEmpty()) {
            throw new IllegalArgumentException(
                    "No doctor available for this specialization."
            );
        }

        List<Doctor> eligibleDoctors = new ArrayList<>();
        List<List<Integer>> adjacency = new ArrayList<>();
        List<LocalTime> allSlots = new ArrayList<>();

        for (Doctor doctor : matchingDoctors) {

            eligibleDoctors.add(doctor);

            List<Integer> doctorSlots = new ArrayList<>();

            for (LocalTime slot : doctor.getAvailableSlots()) {

                int slotIndex = allSlots.indexOf(slot);

                if (slotIndex == -1) {
                    slotIndex = allSlots.size();
                    allSlots.add(slot);
                }

                if (request.getPreferredTime() == null ||
                        request.getPreferredTime().equals(slot)) {
                    doctorSlots.add(slotIndex);
                }
            }

            adjacency.add(doctorSlots);
        }

        int[] matching = matchingAlgorithm.maximumMatching(
                eligibleDoctors.size(),
                allSlots.size(),
                adjacency
        );

        for (int i = 0; i < matching.length; i++) {

            if (matching[i] == -1) {
                continue;
            }

            Doctor doctor = eligibleDoctors.get(i);
            LocalTime selectedTime = allSlots.get(matching[i]);

            try {
                Appointment appointment = new Appointment(
                        null,
                        request.getPatientId(),
                        patient.getName(),
                        doctor.getId(),
                        doctor.getName(),
                        request.getAppointmentDate(),
                        selectedTime,
                        "CONFIRMED"
                );

                appointment.setPatient(patient);
                appointment.setDoctor(doctor);

                Appointment saved=appointmentRepository.save(appointment);
                notificationService.notifyPatient(patient,"BOOKING","Appointment "+saved.getId()+" with "+doctor.getName()+" is confirmed for "+saved.getAppointmentDate()+" at "+saved.getAppointmentTime()+".","booking-"+saved.getId(),"MediSlot appointment confirmed");
                return saved;

            } catch (IllegalStateException exception) {
                // Try another available doctor if this slot was taken.
            }
        }

        throw new IllegalStateException(
                "No suitable appointment slot is currently available."
        );
    }

    // Cancel an existing appointment
    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {

        if (appointmentId == null) {
            throw new IllegalArgumentException(
                    "Appointment ID is required."
            );
        }

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Appointment not found."
                ));

        if ("CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException(
                    "This appointment has already been cancelled."
            );
        }

        if (!"CONFIRMED".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException(
                    "Only confirmed appointments can be cancelled."
            );
        }

        appointment.setStatus("CANCELLED");

        Appointment cancelled = appointmentRepository.update(appointment);
        notificationService.notifyPatient(appointment.getPatient(),"CANCELLATION","Appointment "+appointment.getId()+" has been cancelled.","cancellation-"+appointment.getId(),"MediSlot appointment cancelled");
        waitingListService.offerNext(appointment.getDoctor().getSpecialization(), appointment.getAppointmentDate(), appointment.getAppointmentTime(), appointment.getDoctor());
        return cancelled;
    }

    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, RescheduleRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        if (!"CONFIRMED".equalsIgnoreCase(appointment.getStatus()))
            throw new IllegalStateException("Only confirmed appointments can be rescheduled.");
        if (!appointment.getDoctor().getAvailableSlots().contains(request.appointmentTime()))
            throw new IllegalArgumentException("The doctor is not available at that time.");
        if (appointment.getDoctor().getBlockedDates().contains(request.appointmentDate()))
            throw new IllegalArgumentException("The doctor is unavailable on that date.");
        if (appointmentRepository.existsByDoctorIdAndDateAndTime(appointment.getDoctorId(), request.appointmentDate(), request.appointmentTime())
                && !(appointment.getAppointmentDate().equals(request.appointmentDate()) && appointment.getAppointmentTime().equals(request.appointmentTime())))
            throw new IllegalStateException("This doctor slot is already booked.");
        if (appointmentRepository.existsByPatientIdAndDateAndTime(appointment.getPatientId(), request.appointmentDate(), request.appointmentTime())
                && !(appointment.getAppointmentDate().equals(request.appointmentDate()) && appointment.getAppointmentTime().equals(request.appointmentTime())))
            throw new IllegalStateException("You already have an appointment at that time.");
        java.time.LocalDate oldDate=appointment.getAppointmentDate();
        LocalTime oldTime=appointment.getAppointmentTime();
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setAppointmentTime(request.appointmentTime());
        Appointment updated=appointmentRepository.update(appointment);
        notificationService.notifyPatient(appointment.getPatient(),"RESCHEDULE","Appointment "+appointment.getId()+" is rescheduled to "+updated.getAppointmentDate()+" at "+updated.getAppointmentTime()+".","reschedule-"+appointment.getId()+"-"+updated.getAppointmentDate()+"-"+updated.getAppointmentTime(),"MediSlot appointment rescheduled");
        if(!oldDate.equals(updated.getAppointmentDate())||!oldTime.equals(updated.getAppointmentTime())) waitingListService.offerNext(appointment.getDoctor().getSpecialization(),oldDate,oldTime,appointment.getDoctor());
        return updated;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> searchPatientAppointmentsByDate(String email, java.time.LocalDate date) {
        Patient patient = patientRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        return AppointmentBinarySearch.onDate(appointmentRepository.findByPatientId(patient.getId()), date);
    }

    public List<QueueStatus> queueForPatient(String email) {
        Patient patient=patientRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        java.time.LocalDate today=java.time.LocalDate.now();
        List<Appointment> own=appointmentRepository.findByPatientId(patient.getId()).stream()
                .filter(a -> today.equals(a.getAppointmentDate()))
                .filter(a -> !List.of("CANCELLED","COMPLETED").contains(a.getStatus())).toList();
        List<QueueStatus> result=new ArrayList<>();
        for(Appointment appointment:own) {
            List<Appointment> sameDoctor=appointmentRepository.findByDate(today).stream()
                    .filter(a -> appointment.getDoctorId().equals(a.getDoctorId()))
                    .filter(a -> !List.of("CANCELLED","COMPLETED").contains(a.getStatus()))
                    .sorted(java.util.Comparator.comparing(Appointment::getAppointmentTime)).toList();
            int index=0;
            for(int i=0;i<sameDoctor.size();i++) if(sameDoctor.get(i).getId().equals(appointment.getId())) {index=i;break;}
            int minutes=index*appointment.getDoctor().getConsultationDurationMinutes();
            result.add(new QueueStatus(appointment.getId(),index+1,index,minutes,appointment.getStatus(),true));
        }
        return result;
    }

    public boolean patientOwnsAccount(Long patientId, String email) {
        return patientRepository.findById(patientId).map(p -> p.isActive() && p.getEmail().equalsIgnoreCase(email)).orElse(false);
    }

    public boolean appointmentOwnedBy(Long appointmentId, String email) {
        return appointmentRepository.findById(appointmentId)
                .map(a -> a.getPatient().isActive() && a.getPatient().getEmail().equalsIgnoreCase(email)).orElse(false);
    }
}
