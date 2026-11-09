
package com.medislot.backend.repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.medislot.backend.model.Doctor;

import jakarta.annotation.PostConstruct;

@Repository
public class DoctorRepository {

    private final ConcurrentHashMap<Long, Doctor> doctors =
            new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeDoctors() {

        doctors.put(1L, new Doctor(
                1L,
                "Dr. Rahul Sharma",
                "Cardiologist",
                List.of(
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        LocalTime.of(12, 0)
                )
        ));

        doctors.put(2L, new Doctor(
                2L,
                "Dr. Priya Mehta",
                "Dermatologist",
                List.of(
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        LocalTime.of(14, 0),
                        LocalTime.of(15, 0)
                )
        ));

        doctors.put(3L, new Doctor(
                3L,
                "Dr. Amit Verma",
                "General Physician",
                List.of(
                        LocalTime.of(9, 0),
                        LocalTime.of(13, 0),
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 0)
                )
        ));
    }

    public List<Doctor> findAll() {
        return new ArrayList<>(doctors.values());
    }

    public Optional<Doctor> findById(Long id) {
        return Optional.ofNullable(doctors.get(id));
    }

    public void save(Doctor doctor) {
        doctors.put(doctor.getId(), doctor);
    }
}
