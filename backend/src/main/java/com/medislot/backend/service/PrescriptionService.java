package com.medislot.backend.service;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.medislot.backend.dto.PrescriptionRequest;
import com.medislot.backend.model.*;
import com.medislot.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.io.ByteArrayOutputStream;
import java.util.List;
@Service public class PrescriptionService {
    private final PrescriptionRepository prescriptions; private final AppointmentRepository appointments; private final DoctorRepository doctors; private final PatientRepository patients;
    private final NotificationService notificationService;
    public PrescriptionService(PrescriptionRepository prescriptions,AppointmentRepository appointments,DoctorRepository doctors,PatientRepository patients,NotificationService notificationService){this.prescriptions=prescriptions;this.appointments=appointments;this.doctors=doctors;this.patients=patients;this.notificationService=notificationService;}
    @Transactional public Prescription create(Long appointmentId,String email,PrescriptionRequest request){
        Doctor doctor=doctors.findByEmail(email).filter(Doctor::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Appointment appt=appointments.findById(appointmentId).filter(a->doctor.getId().equals(a.getDoctorId())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!List.of("IN_CONSULTATION","COMPLETED").contains(appt.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT,"A prescription can be created after a consultation starts.");
        Prescription saved=prescriptions.save(new Prescription(appt.getPatient(),doctor,appt,request.medicineName().trim(),request.dosage().trim(),request.frequency().trim(),request.duration().trim(),request.instructions()==null?null:request.instructions().trim()));
        notificationService.notifyPatient(appt.getPatient(),"PRESCRIPTION","A prescription is available for your appointment with "+doctor.getName()+".","prescription-"+saved.getId(),"MediSlot prescription available");
        return saved;
    }
    public List<Prescription> forPatient(String email){Patient patient=patients.findByEmail(email).filter(Patient::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));return prescriptions.findByPatient_IdOrderByPrescriptionDateDesc(patient.getId());}
    public byte[] pdf(Long id,String email){
        Prescription p=prescriptions.findById(id).filter(item->item.getPatientId().equals(patients.findByEmail(email).map(Patient::getId).orElse(-1L))).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        try(ByteArrayOutputStream output=new ByteArrayOutputStream()) { Document doc=new Document();PdfWriter.getInstance(doc,output);doc.open();doc.add(new Paragraph("MediSlot | Digital Prescription"));doc.add(new Paragraph("Patient: "+p.getPatientName()));doc.add(new Paragraph("Doctor: "+p.getDoctorName()));doc.add(new Paragraph("Date: "+p.getPrescriptionDate()));doc.add(new Paragraph("Medicine: "+p.getMedicineName()));doc.add(new Paragraph("Dosage: "+p.getDosage()));doc.add(new Paragraph("Frequency: "+p.getFrequency()));doc.add(new Paragraph("Duration: "+p.getDuration()));doc.add(new Paragraph("Instructions: "+(p.getInstructions()==null?"":p.getInstructions())));doc.close();return output.toByteArray();}
        catch(Exception ex){throw new IllegalStateException("Unable to generate prescription PDF.",ex);}
    }
}
