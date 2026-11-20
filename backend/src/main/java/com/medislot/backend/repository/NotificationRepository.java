package com.medislot.backend.repository;
import com.medislot.backend.model.NotificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NotificationRepository extends JpaRepository<NotificationItem,Long> {
 boolean existsByDedupeKey(String key);
 List<NotificationItem> findByPatient_IdOrderByCreatedAtDesc(Long patientId);
 java.util.Optional<NotificationItem> findByIdAndPatient_Id(Long id,Long patientId);
}
