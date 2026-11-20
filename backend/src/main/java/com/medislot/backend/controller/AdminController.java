package com.medislot.backend.controller;

import com.medislot.backend.dto.AdminAuthResponse;
import com.medislot.backend.dto.AdminDoctorRequest;
import com.medislot.backend.dto.PatientLoginRequest;
import com.medislot.backend.model.*;
import com.medislot.backend.security.JwtService;
import com.medislot.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final JwtService jwtService;
    public AdminController(AdminService adminService, JwtService jwtService) { this.adminService=adminService; this.jwtService=jwtService; }
    @PostMapping("/login") public ResponseEntity<AdminAuthResponse> login(@Valid @RequestBody PatientLoginRequest request) {
        AdminAccount admin=adminService.login(request.getEmail(),request.getPassword());
        return ResponseEntity.ok(new AdminAuthResponse(admin.getEmail(),jwtService.issue(admin.getEmail(),"ADMIN"),"ADMIN"));
    }
    @GetMapping("/dashboard") @PreAuthorize("hasRole('ADMIN')") public Map<String,Object> dashboard() { return adminService.dashboard(); }
    @GetMapping("/patients") @PreAuthorize("hasRole('ADMIN')") public List<Patient> patients() { return adminService.patients(); }
    @PatchMapping("/patients/{id}/active") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> patientActive(@PathVariable Long id,@RequestBody Map<String,Boolean> body) {
        if(body.get("active")==null) return ResponseEntity.badRequest().body(Map.of("error","active is required"));
        return ResponseEntity.ok(adminService.patientActive(id,body.get("active")));
    }
    @GetMapping("/doctors") @PreAuthorize("hasRole('ADMIN')") public List<Doctor> doctors() { return adminService.doctors(); }
    @PostMapping("/doctors") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Doctor> addDoctor(@Valid @RequestBody AdminDoctorRequest request) { return ResponseEntity.status(201).body(adminService.addDoctor(request)); }
    @PutMapping("/doctors/{id}") @PreAuthorize("hasRole('ADMIN')") public Doctor updateDoctor(@PathVariable Long id,@Valid @RequestBody AdminDoctorRequest request) { return adminService.updateDoctor(id,request); }
    @PatchMapping("/doctors/{id}/active") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> doctorActive(@PathVariable Long id,@RequestBody Map<String,Boolean> body) {
        if(body.get("active")==null) return ResponseEntity.badRequest().body(Map.of("error","active is required"));
        return ResponseEntity.ok(adminService.doctorActive(id,body.get("active")));
    }
    @GetMapping("/appointments") @PreAuthorize("hasRole('ADMIN')")
    public List<Appointment> appointments(@RequestParam(required=false) LocalDate date,@RequestParam(required=false) String status) { return adminService.appointments(date,status); }
    @PatchMapping("/appointments/{id}/status") @PreAuthorize("hasRole('ADMIN')")
    public Appointment updateAppointment(@PathVariable Long id,@RequestBody Map<String,String> body) { return adminService.updateAppointmentStatus(id,body.get("status")); }
    @GetMapping("/analytics") @PreAuthorize("hasRole('ADMIN')")
    public Map<String,Object> analytics(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to) { return adminService.analytics(from,to); }
}
