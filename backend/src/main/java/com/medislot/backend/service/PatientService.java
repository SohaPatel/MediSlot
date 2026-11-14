
package com.medislot.backend.service;

import com.medislot.backend.algorithm.BloomFilter;
import com.medislot.backend.dto.PatientRegistrationRequest;
import com.medislot.backend.model.Patient;
import com.medislot.backend.repository.PatientRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // Bloom Filter for quick email membership checks
    private final BloomFilter emailBloomFilter =
            new BloomFilter(10000, 5);

    public PatientService(PatientRepository patientRepository) {

        this.patientRepository = patientRepository;

        // Load already registered emails into the Bloom Filter
        List<Patient> existingPatients = patientRepository.findAll();

        for (Patient patient : existingPatients) {
            emailBloomFilter.add(
                    patient.getEmail().trim().toLowerCase()
            );
        }
    }

    public synchronized Patient registerPatient(
            PatientRegistrationRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        // Check Bloom Filter before accessing the repository
        if (emailBloomFilter.mightContain(email)) {

            // Exact lookup prevents false-positive errors
            if (patientRepository.findByEmail(email).isPresent()) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "A patient with this email is already registered"
                );
            }
        }

        Patient patient = new Patient(
                null,
                request.getName().trim(),
                email,
                request.getPhone().trim(),
                request.getAge()
        );

        // Hash password before saving
        patient.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        Patient savedPatient = patientRepository.save(patient);

        // Add email only after successful registration
        emailBloomFilter.add(email);

        return savedPatient;
    }

    public Patient loginPatient(String email, String password) {

        Patient patient = patientRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (patient.getPasswordHash() == null ||
                !passwordEncoder.matches(
                        password,
                        patient.getPasswordHash()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        return patient;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}
