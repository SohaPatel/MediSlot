package com.medislot.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="doctor_reviews", uniqueConstraints=@UniqueConstraint(name="uk_review_appointment",columnNames="appointment_id"))
public class DoctorReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="patient_id",nullable=false) @JsonIgnore private Patient patient;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="doctor_id",nullable=false) private Doctor doctor;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="appointment_id",nullable=false,unique=true) @JsonIgnore private Appointment appointment;
    @Column(nullable=false) private int rating;
    @Column(length=2000) private String comment;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    protected DoctorReview() { }
    public DoctorReview(Patient patient,Doctor doctor,Appointment appointment,int rating,String comment) { this.patient=patient;this.doctor=doctor;this.appointment=appointment;this.rating=rating;this.comment=comment; }
    public Long getId(){return id;} public Long getDoctorId(){return doctor.getId();} public String getDoctorName(){return doctor.getName();}
    public int getRating(){return rating;} public String getComment(){return comment;} public LocalDateTime getCreatedAt(){return createdAt;}
}
