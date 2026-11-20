package com.medislot.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="emergency_requests")
public class EmergencyRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="patient_id",nullable=false) @JsonIgnore private Patient patient;
 @Column(nullable=false,length=100) private String specialization;
 @Column(nullable=false) private LocalDate requestedDate;
 @Column(nullable=false,length=2000) private String reason;
 @Column(nullable=false,length=16) private String status="PENDING";
 @Column(nullable=false) private int clinicalPriority=0;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="reviewed_by_doctor_id") @JsonIgnore private Doctor reviewedBy;
 @Column(length=2000) private String clinicalNotes;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 private LocalDateTime reviewedAt;
 protected EmergencyRequest(){}
 public EmergencyRequest(Patient patient,String specialization,LocalDate date,String reason){this.patient=patient;this.specialization=specialization;this.requestedDate=date;this.reason=reason;}
 public Long getId(){return id;} public Long getPatientId(){return patient.getId();} public String getSpecialization(){return specialization;} public LocalDate getRequestedDate(){return requestedDate;} public String getReason(){return reason;} public String getStatus(){return status;} public int getClinicalPriority(){return clinicalPriority;} public String getClinicalNotes(){return clinicalNotes;} public String getReviewedByName(){return reviewedBy==null?null:reviewedBy.getName();} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getReviewedAt(){return reviewedAt;}
 public void review(Doctor reviewer,String status,int priority,String notes){this.reviewedBy=reviewer;this.status=status;this.clinicalPriority=priority;this.clinicalNotes=notes;this.reviewedAt=LocalDateTime.now();}
}
