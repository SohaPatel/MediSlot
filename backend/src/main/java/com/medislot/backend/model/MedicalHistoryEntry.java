package com.medislot.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_history")
public class MedicalHistoryEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, length = 4000)
    private String details;
    @Column(length = 2000)
    private String allergies;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected MedicalHistoryEntry() { }
    public MedicalHistoryEntry(Patient patient, String title, String details, String allergies) {
        this.patient = patient; this.title = title; this.details = details; this.allergies = allergies;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public String getAllergies() { return allergies; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
