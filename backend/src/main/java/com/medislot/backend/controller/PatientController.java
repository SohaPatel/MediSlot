
package com.medislot.backend.controller;

import com.medislot.backend.dto.PatientLoginRequest;
import com.medislot.backend.dto.PatientRegistrationRequest;
import com.medislot.backend.model.Patient;
import com.medislot.backend.service.PatientService;
import com.medislot.backend.security.JwtService;
import com.medislot.backend.dto.LoginResponse;
import com.medislot.backend.dto.MedicalHistoryRequest;
import com.medislot.backend.dto.PatientProfileUpdateRequest;
import com.medislot.backend.model.MedicalHistoryEntry;
import com.medislot.backend.service.PrescriptionService;
import com.medislot.backend.model.Prescription;
import com.medislot.backend.service.NotificationService;
import com.medislot.backend.model.NotificationItem;
import com.medislot.backend.service.EmergencyReviewService;
import com.medislot.backend.dto.EmergencyRequestDto;
import com.medislot.backend.model.EmergencyRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;
    private final JwtService jwtService;
    private final PrescriptionService prescriptionService;
    private final NotificationService notificationService;
    private final EmergencyReviewService emergencyService;

    public PatientController(PatientService patientService, JwtService jwtService, PrescriptionService prescriptionService,NotificationService notificationService,EmergencyReviewService emergencyService) {
        this.patientService = patientService;
        this.jwtService = jwtService;
        this.prescriptionService = prescriptionService;
        this.notificationService = notificationService;
        this.emergencyService = emergencyService;
    }

    // Register a new patient
    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {

        Patient patient = patientService.registerPatient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    // Login an existing patient
    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(
            @Valid @RequestBody PatientLoginRequest request) {

        Patient patient = patientService.loginPatient(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(new LoginResponse(patient, jwtService.issue(patient.getEmail(), "PATIENT"), "PATIENT"));
    }

    // Get all registered patients
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/me")
    public ResponseEntity<Patient> getProfile(Authentication authentication) {
        return ResponseEntity.ok(patientService.getByEmail(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<Patient> updateProfile(@Valid @RequestBody PatientProfileUpdateRequest request, Authentication authentication) {
        return ResponseEntity.ok(patientService.updateProfile(authentication.getName(), request));
    }

    @GetMapping("/me/medical-history")
    public ResponseEntity<List<MedicalHistoryEntry>> getMedicalHistory(Authentication authentication) {
        return ResponseEntity.ok(patientService.getMedicalHistory(authentication.getName()));
    }

    @PostMapping("/me/medical-history")
    public ResponseEntity<MedicalHistoryEntry> addMedicalHistory(@Valid @RequestBody MedicalHistoryRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.addMedicalHistory(authentication.getName(), request));
    }

    @GetMapping("/me/prescriptions") public List<Prescription> prescriptions(Authentication auth) { return prescriptionService.forPatient(auth.getName()); }
    @GetMapping(value="/me/prescriptions/{id}.pdf",produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> prescriptionPdf(@PathVariable Long id,Authentication auth) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=medislot-prescription-"+id+".pdf").contentType(MediaType.APPLICATION_PDF).body(prescriptionService.pdf(id,auth.getName()));
    }
    @GetMapping("/me/notifications") public List<NotificationItem> notifications(Authentication auth) { return notificationService.forPatient(auth.getName()); }
    @PatchMapping("/me/notifications/{id}/read") public ResponseEntity<Void> markNotificationRead(@PathVariable Long id,Authentication auth) { notificationService.markRead(auth.getName(),id); return ResponseEntity.noContent().build(); }
    @GetMapping("/me/emergency-requests") public List<EmergencyRequest> emergencyRequests(Authentication auth) { return emergencyService.forPatient(auth.getName()); }
    @PostMapping("/me/emergency-requests") public ResponseEntity<EmergencyRequest> submitEmergency(@Valid @RequestBody EmergencyRequestDto request,Authentication auth) { return ResponseEntity.status(201).body(emergencyService.create(auth.getName(),request)); }
}
