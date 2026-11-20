package com.medislot.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service public class AppointmentPdfService {
    private final AppointmentRepository appointments;
    public AppointmentPdfService(AppointmentRepository appointments) { this.appointments=appointments; }
    public byte[] create(Long id, String patientEmail) {
        Appointment appointment = appointments.findById(id).filter(a -> a.getPatient().getEmail().equalsIgnoreCase(patientEmail))
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("MediSlot | Appointment Confirmation"));
            document.add(new Paragraph("Appointment ID: " + appointment.getId()));
            document.add(new Paragraph("Patient: " + appointment.getPatientName()));
            document.add(new Paragraph("Doctor: " + appointment.getDoctorName()));
            document.add(new Paragraph("Specialization: " + appointment.getDoctor().getSpecialization()));
            document.add(new Paragraph("Date: " + appointment.getAppointmentDate()));
            document.add(new Paragraph("Time: " + appointment.getAppointmentTime()));
            document.add(new Paragraph("Status: " + appointment.getStatus()));
            document.close();
            return output.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to generate appointment confirmation.", exception);
        }
    }
}
