package com.medislot.backend.repository;
import com.medislot.backend.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JpaDoctorStore extends JpaRepository<Doctor, Long> {
    java.util.Optional<Doctor> findByEmailIgnoreCase(String email);
}
