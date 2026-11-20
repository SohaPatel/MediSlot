
package com.medislot.backend.model;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "appointments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Long patientId;

    @Transient
    private String patientName;

    @Transient
    private Long doctorId;

    @Transient
    private String doctorName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonIgnore
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonIgnore
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private LocalTime appointmentTime;

    @Column(nullable = false, length = 24)
    private String status;

    public Appointment() {
    }

    public Appointment(Long id, Long patientId, String patientName,
                       Long doctorId, String doctorName,
                       LocalDate appointmentDate,
                       LocalTime appointmentTime, String status) {

        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patient != null ? patient.getId() : patientId;
    }

    public String getPatientName() {
        return patient != null ? patient.getName() : patientName;
    }

    public Long getDoctorId() {
        return doctor != null ? doctor.getId() : doctorId;
    }

    public String getDoctorName() {
        return doctor != null ? doctor.getName() : doctorName;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
