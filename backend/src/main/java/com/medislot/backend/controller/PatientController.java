package com.medislot.backend.controller;

import com.medislot.backend.dto.PatientRegistrationRequest;
import com.medislot.backend.model.Patient;
import com.medislot.backend.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Register a new patient
    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {

        Patient patient = patientService.registerPatient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    // Get all registered patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {

        return ResponseEntity.ok(patientService.getAllPatients());
    }
}