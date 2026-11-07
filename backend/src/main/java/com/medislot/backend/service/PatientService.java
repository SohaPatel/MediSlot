package com.medislot.backend.service;

import com.medislot.backend.dto.PatientRegistrationRequest;
import com.medislot.backend.model.Patient;
import com.medislot.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(PatientRegistrationRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (patientRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A patient with this email is already registered"
            );
        }

        Patient patient = new Patient(
                null,
                request.getName().trim(),
                email,
                request.getPhone().trim(),
                request.getAge()
        );

        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}