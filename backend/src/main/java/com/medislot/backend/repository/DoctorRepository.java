
package com.medislot.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.medislot.backend.model.Doctor;


@Repository
public class DoctorRepository {

    private final JpaDoctorStore store;

    public DoctorRepository(JpaDoctorStore store) { this.store = store; }

    public List<Doctor> findAll() {
        return store.findAll();
    }

    public Optional<Doctor> findById(Long id) {
        return store.findById(id);
    }

    public Doctor save(Doctor doctor) {
        return store.save(doctor);
    }

    public long count() { return store.count(); }
    public Optional<Doctor> findByEmail(String email) { return store.findByEmailIgnoreCase(email); }
    public boolean existsByEmail(String email) { return store.findByEmailIgnoreCase(email).isPresent(); }
}
