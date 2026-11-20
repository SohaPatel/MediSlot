package com.medislot.backend.repository;
import com.medislot.backend.model.WaitingListEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface WaitingListRepository extends JpaRepository<WaitingListEntry, Long> {
    List<WaitingListEntry> findBySpecializationIgnoreCaseAndStatus(String specialization, String status);
    List<WaitingListEntry> findByPatient_IdOrderByCreatedAtDesc(Long patientId);
    java.util.Optional<WaitingListEntry> findByReservedAppointmentId(Long appointmentId);
    List<WaitingListEntry> findByStatus(String status);
    boolean existsByPatient_IdAndSpecializationIgnoreCaseAndAppointmentDateAndStatus(Long patientId, String specialization, java.time.LocalDate date, String status);
}
