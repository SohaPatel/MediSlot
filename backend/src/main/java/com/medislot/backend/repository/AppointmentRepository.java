
package com.medislot.backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.medislot.backend.model.Appointment;

@Repository
public class AppointmentRepository {

    private final ConcurrentHashMap<Long, Appointment> appointments =
            new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    public synchronized Appointment save(Appointment appointment) {

        boolean slotAlreadyBooked = appointments.values()
                .stream()
                .anyMatch(existing ->
                        existing.getDoctorId().equals(appointment.getDoctorId())
                        && existing.getAppointmentDate().equals(
                                appointment.getAppointmentDate())
                        && existing.getAppointmentTime().equals(
                                appointment.getAppointmentTime())
                        && !"CANCELLED".equals(existing.getStatus())
                );

        if (slotAlreadyBooked) {
            throw new IllegalStateException(
                    "This doctor slot is already booked"
            );
        }

        appointment.setId(idGenerator.getAndIncrement());
        appointments.put(appointment.getId(), appointment);

        return appointment;
    }

    public synchronized Appointment update(Appointment appointment) {

        if (appointment == null || appointment.getId() == null) {
            throw new IllegalArgumentException(
                    "Appointment and appointment ID are required."
            );
        }

        if (!appointments.containsKey(appointment.getId())) {
            throw new IllegalArgumentException(
                    "Appointment not found."
            );
        }

        appointments.put(appointment.getId(), appointment);

        return appointment;
    }

    public List<Appointment> findAll() {

        List<Appointment> result =
                new ArrayList<>(appointments.values());

        result.sort(Comparator.comparing(Appointment::getAppointmentDate)
                .thenComparing(Appointment::getAppointmentTime));

        return result;
    }

    public Optional<Appointment> findById(Long id) {
        return Optional.ofNullable(appointments.get(id));
    }

    public List<Appointment> findByPatientId(Long patientId) {

        return appointments.values()
                .stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .toList();
    }

    public List<Appointment> findByDate(LocalDate date) {

        return appointments.values()
                .stream()
                .filter(a -> a.getAppointmentDate().equals(date))
                .toList();
    }

    public boolean existsByPatientIdAndDateAndTime(
            Long patientId, LocalDate date, LocalTime time) {

        return appointments.values().stream().anyMatch(a ->
                a.getPatientId().equals(patientId)
                && a.getAppointmentDate().equals(date)
                && a.getAppointmentTime().equals(time)
                && !"CANCELLED".equals(a.getStatus())
        );
    }
}
