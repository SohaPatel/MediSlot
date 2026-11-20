package com.medislot.backend.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Entity @Table(name = "waiting_list")
public class WaitingListEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "patient_id", nullable = false) private Patient patient;
    @Column(nullable = false, length = 100) private String specialization;
    @Column(nullable = false) private LocalDate appointmentDate;
    private LocalTime preferredTime;
    @Column(nullable = false, length = 16) private String status = "WAITING";
    @Column(nullable = false) private int priorityScore = 0;
    @Column(name="reserved_appointment_id") private Long reservedAppointmentId;
    private LocalDateTime offeredAt;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    protected WaitingListEntry() { }
    public WaitingListEntry(Patient patient, String specialization, LocalDate date, LocalTime time) { this.patient=patient; this.specialization=specialization; this.appointmentDate=date; this.preferredTime=time; }
    public Long getId() { return id; }
    public Long getPatientId() { return patient.getId(); }
    public String getSpecialization() { return specialization; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getPreferredTime() { return preferredTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status=status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getPriorityScore() { return priorityScore; }
    public void setPriorityScore(int score) { if(score<0||score>5) throw new IllegalArgumentException("Priority score must be between 0 and 5."); this.priorityScore=score; }
    public Long getReservedAppointmentId() { return reservedAppointmentId; }
    public LocalDateTime getOfferedAt() { return offeredAt; }
    public void offer(Long appointmentId) { this.status="OFFERED"; this.reservedAppointmentId=appointmentId; this.offeredAt=LocalDateTime.now(); }
}
