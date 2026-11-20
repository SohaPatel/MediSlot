package com.medislot.backend.repository;
import com.medislot.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public interface JpaAppointmentStore extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient_Id(Long patientId);
    List<Appointment> findByAppointmentDate(LocalDate date);
    boolean existsByPatient_IdAndAppointmentDateAndAppointmentTimeAndStatusNot(Long patientId, LocalDate date, LocalTime time, String status);
    boolean existsByDoctor_IdAndAppointmentDateAndAppointmentTimeAndStatusNot(Long doctorId, LocalDate date, LocalTime time, String status);
}
