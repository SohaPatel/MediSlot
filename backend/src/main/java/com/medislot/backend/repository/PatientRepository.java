
package com.medislot.backend.repository;

import com.medislot.backend.model.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepository {

    private final JpaPatientStore store;

    public PatientRepository(JpaPatientStore store) { this.store = store; }

    public Patient save(Patient patient) {
        return store.save(patient);
    }

    public Optional<Patient> findById(Long id) {
        return store.findById(id);
    }

    public Optional<Patient> findByEmail(String email) {
        return store.findByEmailIgnoreCase(email);
    }

    public List<Patient> findAll() {
        return store.findAll();
    }
}
