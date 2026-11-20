
package com.medislot.backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.dao.DataIntegrityViolationException;

import com.medislot.backend.model.Appointment;

@Repository
public class AppointmentRepository {

    private final JpaAppointmentStore store;

    public AppointmentRepository(JpaAppointmentStore store) { this.store = store; }

    public synchronized Appointment save(Appointment appointment) {

        if (existsByPatientIdAndDateAndTime(appointment.getPatientId(), appointment.getAppointmentDate(), appointment.getAppointmentTime())) {
            throw new IllegalStateException("This patient already has an appointment at that time");
        }
        if (existsByDoctorIdAndDateAndTime(appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime())) {
            throw new IllegalStateException(
                    "This doctor slot is already booked"
            );
        }
        try { return store.saveAndFlush(appointment); }
        catch (DataIntegrityViolationException ex) { throw new IllegalStateException("This doctor slot is already booked", ex); }
    }

    public synchronized Appointment update(Appointment appointment) {

        if (appointment == null || appointment.getId() == null) {
            throw new IllegalArgumentException(
                    "Appointment and appointment ID are required."
            );
        }

        return store.save(appointment);
    }

    public List<Appointment> findAll() {

        List<Appointment> result = store.findAll();

        result.sort(Comparator.comparing(Appointment::getAppointmentDate)
                .thenComparing(Appointment::getAppointmentTime));

        return result;
    }

    public Optional<Appointment> findById(Long id) {
        return store.findById(id);
    }

    public List<Appointment> findByPatientId(Long patientId) {

        return store.findByPatient_Id(patientId);
    }

    public List<Appointment> findByDate(LocalDate date) {

        return store.findByAppointmentDate(date);
    }

    public boolean existsByPatientIdAndDateAndTime(
            Long patientId, LocalDate date, LocalTime time) {

        return store.existsByPatient_IdAndAppointmentDateAndAppointmentTimeAndStatusNot(patientId, date, time, "CANCELLED");
    }

    public boolean existsByDoctorIdAndDateAndTime(Long doctorId, LocalDate date, LocalTime time) {
        return store.existsByDoctor_IdAndAppointmentDateAndAppointmentTimeAndStatusNot(doctorId, date, time, "CANCELLED");
    }
}
