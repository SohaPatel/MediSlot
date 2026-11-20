package com.medislot.backend.repository;
import com.medislot.backend.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PrescriptionRepository extends JpaRepository<Prescription,Long> { List<Prescription> findByPatient_IdOrderByPrescriptionDateDesc(Long patientId); }
