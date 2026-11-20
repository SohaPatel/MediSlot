package com.medislot.backend.service;
import com.medislot.backend.dto.EmergencyRequestDto;
import com.medislot.backend.dto.EmergencyReviewRequest;
import com.medislot.backend.model.*;
import com.medislot.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.medislot.backend.dto.WaitingListRequest;
import java.util.List;
@Service public class EmergencyReviewService {
 private final EmergencyRequestRepository requests;private final PatientRepository patients;private final DoctorRepository doctors;private final WaitingListService waiting;private final NotificationService notifications;
 public EmergencyReviewService(EmergencyRequestRepository requests,PatientRepository patients,DoctorRepository doctors,WaitingListService waiting,NotificationService notifications){this.requests=requests;this.patients=patients;this.doctors=doctors;this.waiting=waiting;this.notifications=notifications;}
 @Transactional public EmergencyRequest create(String email,EmergencyRequestDto dto){Patient p=patients.findByEmail(email).filter(Patient::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));EmergencyRequest saved=requests.save(new EmergencyRequest(p,dto.specialization().trim(),dto.requestedDate(),dto.reason().trim()));try{waiting.join(email,new WaitingListRequest(dto.specialization(),dto.requestedDate(),null));}catch(IllegalStateException alreadyWaiting){}return saved;}
 public List<EmergencyRequest> forPatient(String email){Patient p=patients.findByEmail(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));return requests.findByPatient_IdOrderByCreatedAtDesc(p.getId());}
 public List<EmergencyRequest> pendingForDoctor(String email){Doctor d=doctors.findByEmail(email).filter(Doctor::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));return requests.findBySpecializationIgnoreCaseAndStatusOrderByCreatedAtAsc(d.getSpecialization(),"PENDING");}
 @Transactional public EmergencyRequest review(String email,Long id,EmergencyReviewRequest decision){
  Doctor d=doctors.findByEmail(email).filter(Doctor::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  EmergencyRequest request=requests.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!d.getSpecialization().equalsIgnoreCase(request.getSpecialization()))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only a clinician in the requested specialty can review this request.");
  if(!"PENDING".equals(request.getStatus()))throw new ResponseStatusException(HttpStatus.CONFLICT,"This request has already been reviewed.");
  String status=decision.decision().equalsIgnoreCase("APPROVE")?"APPROVED":decision.decision().equalsIgnoreCase("DENY")?"DENIED":null;
  if(status==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Decision must be APPROVE or DENY.");
  int priority="APPROVED".equals(status)?decision.clinicalPriority():0;
  request.review(d,status,priority,decision.clinicalNotes());
  if(priority>0)waiting.applyClinicalPriority(request.getPatientId(),request.getSpecialization(),request.getRequestedDate(),priority);
  notifications.notifyPatient(patients.findById(request.getPatientId()).orElseThrow(),"CLINICAL_REVIEW","Your clinical priority request was reviewed: "+status+".","emergency-review-"+id,"MediSlot clinical review update");
  return request;
 }
}
