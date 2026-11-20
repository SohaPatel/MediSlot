package com.medislot.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="prescriptions")
public class Prescription {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="patient_id",nullable=false) @JsonIgnore private Patient patient;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="doctor_id",nullable=false) @JsonIgnore private Doctor doctor;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="appointment_id",nullable=false) @JsonIgnore private Appointment appointment;
    @Column(nullable=false,length=160) private String medicineName;
    @Column(nullable=false,length=160) private String dosage;
    @Column(nullable=false,length=160) private String frequency;
    @Column(nullable=false,length=120) private String duration;
    @Column(length=2000) private String instructions;
    @Column(nullable=false) private LocalDate prescriptionDate=LocalDate.now();
    protected Prescription() { }
    public Prescription(Patient p,Doctor d,Appointment a,String medicine,String dosage,String frequency,String duration,String instructions){this.patient=p;this.doctor=d;this.appointment=a;this.medicineName=medicine;this.dosage=dosage;this.frequency=frequency;this.duration=duration;this.instructions=instructions;}
    public Long getId(){return id;} public Long getPatientId(){return patient.getId();} public String getPatientName(){return patient.getName();}
    public Long getDoctorId(){return doctor.getId();} public String getDoctorName(){return doctor.getName();}
    public String getMedicineName(){return medicineName;} public String getDosage(){return dosage;} public String getFrequency(){return frequency;} public String getDuration(){return duration;} public String getInstructions(){return instructions;} public LocalDate getPrescriptionDate(){return prescriptionDate;}
}
