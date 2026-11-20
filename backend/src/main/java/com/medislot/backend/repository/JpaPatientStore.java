package com.medislot.backend.repository;
import com.medislot.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface JpaPatientStore extends JpaRepository<Patient, Long> { Optional<Patient> findByEmailIgnoreCase(String email); }
