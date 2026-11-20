package com.medislot.backend.repository;
import com.medislot.backend.model.DoctorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface DoctorReviewRepository extends JpaRepository<DoctorReview,Long> {
    boolean existsByAppointment_Id(Long appointmentId);
    List<DoctorReview> findByDoctor_IdOrderByCreatedAtDesc(Long doctorId);
    @Query("select avg(r.rating) from DoctorReview r where r.doctor.id = :doctorId") Double averageRating(@Param("doctorId") Long doctorId);
    long countByDoctor_Id(Long doctorId);
}
