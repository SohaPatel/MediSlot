
package com.medislot.backend.controller;

import com.medislot.backend.dto.AppointmentRequest;
import com.medislot.backend.dto.RescheduleRequest;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.service.AppointmentService;
import com.medislot.backend.service.WaitingListService;
import com.medislot.backend.dto.WaitingListRequest;
import com.medislot.backend.model.WaitingListEntry;
import com.medislot.backend.service.AppointmentPdfService;
import com.medislot.backend.service.DoctorReviewService;
import com.medislot.backend.dto.DoctorReviewRequest;
import com.medislot.backend.model.DoctorReview;
import com.medislot.backend.dto.QueueStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final WaitingListService waitingListService;
    private final AppointmentPdfService pdfService;
    private final DoctorReviewService reviewService;

    public AppointmentController(
            AppointmentService appointmentService, WaitingListService waitingListService, AppointmentPdfService pdfService, DoctorReviewService reviewService) {
        this.appointmentService = appointmentService;
        this.waitingListService = waitingListService;
        this.pdfService = pdfService;
        this.reviewService = reviewService;
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getDoctors() {
        return ResponseEntity.ok(
                appointmentService.getAllDoctors()
        );
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> recommendations(@RequestParam(required = false) String specialization,
            @RequestParam(required = false) java.time.LocalDate date,
            @RequestParam(required = false) java.time.LocalTime time) {
        return ResponseEntity.ok(appointmentService.recommendDoctors(specialization, date, time));
    }

    @GetMapping("/search-suggestions")
    public ResponseEntity<?> searchSuggestions(@RequestParam String prefix) {
        return ResponseEntity.ok(appointmentService.searchDoctors(prefix));
    }

    @PostMapping("/waiting-list")
    public ResponseEntity<?> joinWaitingList(@Valid @RequestBody WaitingListRequest request, Authentication authentication) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(waitingListService.join(authentication.getName(), request)); }
        catch (IllegalArgumentException | IllegalStateException ex) { return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage())); }
    }

    @PatchMapping("/{appointmentId}/accept-offer")
    public ResponseEntity<?> acceptOffer(@PathVariable Long appointmentId,Authentication auth) {
        try { waitingListService.acceptOffer(appointmentId,auth.getName()); return ResponseEntity.ok(Map.of("status","CONFIRMED")); }
        catch(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage())); }
    }
    @PatchMapping("/{appointmentId}/decline-offer")
    public ResponseEntity<?> declineOffer(@PathVariable Long appointmentId,Authentication auth) {
        try { waitingListService.declineOffer(appointmentId,auth.getName()); return ResponseEntity.ok(Map.of("status","DECLINED")); }
        catch(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage())); }
    }

    @GetMapping("/waiting-list")
    public ResponseEntity<List<WaitingListEntry>> getWaitingList(Authentication authentication) {
        return ResponseEntity.ok(waitingListService.forPatient(authentication.getName()));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @Valid @RequestBody AppointmentRequest request, Authentication authentication) {

        if (!appointmentService.patientOwnsAccount(request.getPatientId(), authentication.getName())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only book appointments for your own account."));
        }

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(
                appointmentService.getAllAppointments()
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
            @PathVariable Long patientId, Authentication authentication) {

        if (!appointmentService.patientOwnsAccount(patientId, authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByPatient(patientId)
        );
    }

    @GetMapping("/search/date")
    public ResponseEntity<?> searchPatientAppointmentsByDate(@RequestParam java.time.LocalDate date, Authentication authentication) {
        return ResponseEntity.ok(appointmentService.searchPatientAppointmentsByDate(authentication.getName(), date));
    }

    @GetMapping("/patient/{patientId}/queue")
    public ResponseEntity<?> patientQueue(@PathVariable Long patientId,Authentication auth) {
        if (!appointmentService.patientOwnsAccount(patientId,auth.getName())) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(appointmentService.queueForPatient(auth.getName()));
    }

    // Cancel an appointment
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId, Authentication authentication) {

        if (!appointmentService.appointmentOwnedBy(appointmentId, authentication.getName())) {
            return ResponseEntity.status(403).body(Map.of("error", "You can only cancel your own appointments."));
        }

        try {
            Appointment appointment =
                    appointmentService.cancelAppointment(appointmentId);

            return ResponseEntity.ok(appointment);

        } catch (IllegalArgumentException |
                 IllegalStateException exception) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @PatchMapping("/{appointmentId}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long appointmentId,
            @Valid @RequestBody RescheduleRequest request, Authentication authentication) {
        if (!appointmentService.appointmentOwnedBy(appointmentId, authentication.getName()))
            return ResponseEntity.status(403).body(Map.of("error", "You can only reschedule your own appointments."));
        try {
            return ResponseEntity.ok(appointmentService.rescheduleAppointment(appointmentId, request));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
        }
    }

    @GetMapping(value = "/{appointmentId}/confirmation.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadConfirmation(@PathVariable Long appointmentId, Authentication authentication) {
        try {
            byte[] pdf = pdfService.create(appointmentId, authentication.getName());
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=medislot-appointment-" + appointmentId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF).body(pdf);
        } catch (IllegalArgumentException exception) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping("/{appointmentId}/review")
    public ResponseEntity<DoctorReview> submitReview(@PathVariable Long appointmentId,@Valid @RequestBody DoctorReviewRequest request,Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.submit(appointmentId,authentication.getName(),request));
    }
}
