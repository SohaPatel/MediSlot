package com.medislot.backend.service;
import com.medislot.backend.dto.DoctorReviewRequest;
import com.medislot.backend.model.*;
import com.medislot.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service public class DoctorReviewService {
    private final DoctorReviewRepository reviews; private final AppointmentRepository appointments; private final PatientRepository patients;
    public DoctorReviewService(DoctorReviewRepository reviews,AppointmentRepository appointments,PatientRepository patients){this.reviews=reviews;this.appointments=appointments;this.patients=patients;}
    @Transactional public DoctorReview submit(Long appointmentId,String email,DoctorReviewRequest request){
        Patient patient=patients.findByEmail(email).filter(Patient::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Appointment appt=appointments.findById(appointmentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!patient.getId().equals(appt.getPatientId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(!"COMPLETED".equals(appt.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Reviews are available after a completed appointment.");
        if(reviews.existsByAppointment_Id(appointmentId)) throw new ResponseStatusException(HttpStatus.CONFLICT,"This appointment already has a review.");
        return reviews.save(new DoctorReview(patient,appt.getDoctor(),appt,request.rating(),request.comment()==null?null:request.comment().trim()));
    }
    public List<DoctorReview> forDoctor(Long id){return reviews.findByDoctor_IdOrderByCreatedAtDesc(id);}
}
