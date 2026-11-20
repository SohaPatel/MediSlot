package com.medislot.backend.repository;
import com.medislot.backend.model.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest,Long> {
 List<EmergencyRequest> findByPatient_IdOrderByCreatedAtDesc(Long patientId);
 List<EmergencyRequest> findBySpecializationIgnoreCaseAndStatusOrderByCreatedAtAsc(String specialization,String status);
}
