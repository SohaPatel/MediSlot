package com.medislot.backend.controller;

import com.medislot.backend.dto.DoctorAuthResponse;
import com.medislot.backend.dto.DoctorProfileUpdateRequest;
import com.medislot.backend.dto.DoctorRegistrationRequest;
import com.medislot.backend.dto.PatientLoginRequest;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.security.JwtService;
import com.medislot.backend.service.DoctorService;
import com.medislot.backend.repository.DoctorReviewRepository;
import com.medislot.backend.model.DoctorReview;
import com.medislot.backend.service.PrescriptionService;
import com.medislot.backend.dto.PrescriptionRequest;
import com.medislot.backend.model.Prescription;
import com.medislot.backend.service.EmergencyReviewService;
import com.medislot.backend.dto.EmergencyReviewRequest;
import com.medislot.backend.model.EmergencyRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {
    private final DoctorService doctorService;
    private final JwtService jwtService;
    private final DoctorReviewRepository reviewRepository;
    private final PrescriptionService prescriptionService;
    private final EmergencyReviewService emergencyService;
    public DoctorController(DoctorService doctorService, JwtService jwtService, DoctorReviewRepository reviewRepository,PrescriptionService prescriptionService,EmergencyReviewService emergencyService) { this.doctorService=doctorService; this.jwtService=jwtService; this.reviewRepository=reviewRepository; this.prescriptionService=prescriptionService; this.emergencyService=emergencyService; }

    @PostMapping("/register")
    public ResponseEntity<Doctor> register(@Valid @RequestBody DoctorRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<DoctorAuthResponse> login(@Valid @RequestBody PatientLoginRequest request) {
        Doctor doctor=doctorService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new DoctorAuthResponse(doctor, jwtService.issue(doctor.getEmail(), "DOCTOR"), "DOCTOR"));
    }
    @GetMapping("/me") @PreAuthorize("hasRole('DOCTOR')")
    public Doctor getProfile(Authentication auth) { return doctorService.profile(auth.getName()); }
    @GetMapping("/{doctorId}/reviews")
    public List<DoctorReview> reviews(@PathVariable Long doctorId) { return reviewRepository.findByDoctor_IdOrderByCreatedAtDesc(doctorId); }
    @PutMapping("/me") @PreAuthorize("hasRole('DOCTOR')")
    public Doctor updateProfile(@Valid @RequestBody DoctorProfileUpdateRequest request, Authentication auth) { return doctorService.updateProfile(auth.getName(), request); }
    @PutMapping("/me/availability") @PreAuthorize("hasRole('DOCTOR')")
    public Doctor updateAvailability(@RequestBody List<LocalTime> slots, Authentication auth) { return doctorService.setAvailability(auth.getName(), slots); }
    @PutMapping("/me/blocked-dates") @PreAuthorize("hasRole('DOCTOR')")
    public Doctor updateBlockedDates(@RequestBody List<LocalDate> dates, Authentication auth) { return doctorService.setBlockedDates(auth.getName(), dates); }
    @GetMapping("/me/appointments") @PreAuthorize("hasRole('DOCTOR')")
    public List<Appointment> appointments(Authentication auth, @RequestParam(required=false) String range) {
        List<Appointment> all=doctorService.appointments(auth.getName());
        LocalDate today=LocalDate.now();
        if ("today".equalsIgnoreCase(range)) return all.stream().filter(a -> today.equals(a.getAppointmentDate())).toList();
        if ("upcoming".equalsIgnoreCase(range)) return all.stream().filter(a -> !a.getAppointmentDate().isBefore(today) && !"CANCELLED".equals(a.getStatus())).toList();
        if ("completed".equalsIgnoreCase(range)) return all.stream().filter(a -> "COMPLETED".equals(a.getStatus())).toList();
        if ("cancelled".equalsIgnoreCase(range)) return all.stream().filter(a -> "CANCELLED".equals(a.getStatus())).toList();
        return all;
    }
    @PatchMapping("/me/appointments/{id}/status") @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String,String> request, Authentication auth) {
        try { return ResponseEntity.ok(doctorService.updateAppointmentStatus(auth.getName(), id, request.get("status"))); }
        catch (org.springframework.web.server.ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getReason()==null ? "Request failed" : ex.getReason())); }
    }
    @PostMapping("/me/appointments/{id}/prescriptions") @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Prescription> createPrescription(@PathVariable Long id,@Valid @RequestBody PrescriptionRequest request,Authentication auth) {
        return ResponseEntity.status(201).body(prescriptionService.create(id,auth.getName(),request));
    }
    @GetMapping("/me/emergency-requests") @PreAuthorize("hasRole('DOCTOR')") public List<EmergencyRequest> pendingEmergencyRequests(Authentication auth) { return emergencyService.pendingForDoctor(auth.getName()); }
    @PatchMapping("/me/emergency-requests/{id}/review") @PreAuthorize("hasRole('DOCTOR')") public EmergencyRequest reviewEmergency(@PathVariable Long id,@Valid @RequestBody EmergencyReviewRequest request,Authentication auth) { return emergencyService.review(auth.getName(),id,request); }
}
