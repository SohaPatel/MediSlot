
package com.medislot.backend.controller;

import com.medislot.backend.dto.AppointmentRequest;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.service.AppointmentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getDoctors() {
        return ResponseEntity.ok(
                appointmentService.getAllDoctors()
        );
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        try {
            Appointment appointment =
                    appointmentService.bookAppointment(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(appointment);

        } catch (IllegalArgumentException |
                 IllegalStateException exception) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(
                appointmentService.getAllAppointments()
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByPatient(patientId)
        );
    }
}
