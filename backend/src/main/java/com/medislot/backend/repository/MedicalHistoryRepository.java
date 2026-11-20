package com.medislot.backend.repository;
import com.medislot.backend.model.MedicalHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistoryEntry, Long> {
    List<MedicalHistoryEntry> findByPatient_IdOrderByCreatedAtDesc(Long patientId);
}
