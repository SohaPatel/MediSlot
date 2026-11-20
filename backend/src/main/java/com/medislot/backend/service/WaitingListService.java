package com.medislot.backend.service;
import com.medislot.backend.dto.WaitingListRequest;
import com.medislot.backend.model.Patient;
import com.medislot.backend.model.WaitingListEntry;
import com.medislot.backend.repository.PatientRepository;
import com.medislot.backend.repository.WaitingListRepository;
import com.medislot.backend.repository.AppointmentRepository;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.algorithm.WaitingListPrioritizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.List;

@Service public class WaitingListService {
    private final WaitingListRepository entries;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final NotificationService notificationService;
    public WaitingListService(WaitingListRepository entries, PatientRepository patients, AppointmentRepository appointments,NotificationService notificationService) { this.entries=entries; this.patients=patients; this.appointments=appointments; this.notificationService=notificationService; }
    @Transactional public WaitingListEntry join(String email, WaitingListRequest request) {
        Patient patient=patients.findByEmail(email).filter(Patient::isActive).orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        if (entries.findByPatient_IdOrderByCreatedAtDesc(patient.getId()).stream().anyMatch(e -> e.getSpecialization().equalsIgnoreCase(request.specialization()) && e.getAppointmentDate().equals(request.appointmentDate()) && List.of("WAITING","OFFERED").contains(e.getStatus())))
            throw new IllegalStateException("You are already on this waiting list.");
        return entries.save(new WaitingListEntry(patient, request.specialization().trim(), request.appointmentDate(), request.preferredTime()));
    }
    public List<WaitingListEntry> forPatient(String email) {
        Patient patient=patients.findByEmail(email).filter(Patient::isActive).orElseThrow(() -> new IllegalArgumentException("Patient not found."));
        return entries.findByPatient_IdOrderByCreatedAtDesc(patient.getId());
    }
    @Transactional public void offerNext(String specialization, java.time.LocalDate date, java.time.LocalTime time, Doctor doctor) {
        PriorityQueue<WaitingListEntry> queue;
        List<WaitingListEntry> eligible=entries.findBySpecializationIgnoreCaseAndStatus(specialization, "WAITING").stream()
                .filter(entry -> entry.getAppointmentDate().equals(date))
                .filter(entry -> entry.getPreferredTime() == null || entry.getPreferredTime().equals(time))
                .toList();
        queue=WaitingListPrioritizer.queue(eligible);
        while(!queue.isEmpty()) {
            WaitingListEntry next=queue.poll();
            Appointment reservation=new Appointment(null,next.getPatientId(),patients.findById(next.getPatientId()).orElseThrow().getName(),doctor.getId(),doctor.getName(),date,time,"OFFERED");
            reservation.setPatient(patients.findById(next.getPatientId()).orElseThrow()); reservation.setDoctor(doctor);
            try {
                Appointment saved=appointments.save(reservation);
                next.offer(saved.getId());
                Patient p=patients.findById(next.getPatientId()).orElseThrow();
                notificationService.notifyPatient(p,"WAITLIST_OFFER","A slot is reserved for you: "+date+" at "+time+" with "+doctor.getName()+". Accept or decline it in MediSlot.","waitlist-offer-"+saved.getId(),"MediSlot waiting-list slot available");
                return;
            } catch(IllegalStateException conflict) { /* A concurrent booking won the slot; try the next eligible patient. */ }
        }
    }
    @Transactional public void acceptOffer(Long appointmentId,String email) {
        Appointment appt=appointments.findById(appointmentId).orElseThrow(()->new IllegalArgumentException("Offer not found."));
        if(!appt.getPatient().isActive() || !appt.getPatient().getEmail().equalsIgnoreCase(email) || !"OFFERED".equals(appt.getStatus())) throw new IllegalArgumentException("Offer is not available to this account.");
        appt.setStatus("CONFIRMED"); appointments.update(appt);
        entries.findByReservedAppointmentId(appointmentId).ifPresent(entry->entry.setStatus("BOOKED"));
        notificationService.notifyPatient(appt.getPatient(),"WAITLIST_BOOKED","You accepted the reserved appointment slot.","waitlist-accepted-"+appointmentId,"MediSlot waiting-list offer accepted");
    }
    @Transactional public void declineOffer(Long appointmentId,String email) {
        Appointment appt=appointments.findById(appointmentId).orElseThrow(()->new IllegalArgumentException("Offer not found."));
        if(!appt.getPatient().isActive() || !appt.getPatient().getEmail().equalsIgnoreCase(email) || !"OFFERED".equals(appt.getStatus())) throw new IllegalArgumentException("Offer is not available to this account.");
        appt.setStatus("CANCELLED"); appointments.update(appt);
        entries.findByReservedAppointmentId(appointmentId).ifPresent(entry->entry.setStatus("DECLINED"));
        offerNext(appt.getDoctor().getSpecialization(),appt.getAppointmentDate(),appt.getAppointmentTime(),appt.getDoctor());
    }
    @Transactional public void expireOffers() {
        java.time.LocalDateTime expiry=java.time.LocalDateTime.now().minusMinutes(15);
        for(WaitingListEntry entry:entries.findByStatus("OFFERED")) {
            if(entry.getOfferedAt()!=null && entry.getOfferedAt().isBefore(expiry)) {
                appointments.findById(entry.getReservedAppointmentId()).ifPresent(appt->{appt.setStatus("CANCELLED");appointments.update(appt);offerNext(appt.getDoctor().getSpecialization(),appt.getAppointmentDate(),appt.getAppointmentTime(),appt.getDoctor());});
                entry.setStatus("EXPIRED");
            }
        }
    }
    @Transactional public void applyClinicalPriority(Long patientId,String specialization,java.time.LocalDate date,int priority) {
        entries.findByPatient_IdOrderByCreatedAtDesc(patientId).stream()
                .filter(entry -> entry.getStatus().equals("WAITING") && entry.getSpecialization().equalsIgnoreCase(specialization) && entry.getAppointmentDate().equals(date))
                .forEach(entry -> entry.setPriorityScore(priority));
    }
}
