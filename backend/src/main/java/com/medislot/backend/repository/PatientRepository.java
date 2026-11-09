
package com.medislot.backend.repository;

import com.medislot.backend.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PatientRepository {

    private final ConcurrentHashMap<Long, Patient> patients =
            new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    public Patient save(Patient patient) {
        Long id = idGenerator.getAndIncrement();
        patient.setId(id);
        patients.put(id, patient);
        return patient;
    }

    public Optional<Patient> findById(Long id) {
        return Optional.ofNullable(patients.get(id));
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.values()
                .stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }
}
