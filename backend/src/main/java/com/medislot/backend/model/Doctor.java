
package com.medislot.backend.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(unique = true, length = 254)
    private String email;

    @JsonIgnore
    @Column(length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_available_slots", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "slot_time", nullable = false)
    private List<LocalTime> availableSlots;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_blocked_dates", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "blocked_date", nullable = false)
    private Set<LocalDate> blockedDates = new HashSet<>();

    @Column(nullable = false)
    private int consultationDurationMinutes = 30;

    public Doctor() {
        this.availableSlots = new ArrayList<>();
    }

    public Doctor(Long id, String name, String specialization,
                  List<LocalTime> availableSlots) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.availableSlots = availableSlots == null
                ? new ArrayList<>()
                : new ArrayList<>(availableSlots);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getEmail() { return email; }
    @JsonIgnore public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return active; }
    public Set<LocalDate> getBlockedDates() { return blockedDates; }
    public int getConsultationDurationMinutes() { return consultationDurationMinutes; }

    public List<LocalTime> getAvailableSlots() {
        return availableSlots;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setEmail(String email) { this.email = email; }
    @JsonIgnore public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setActive(boolean active) { this.active = active; }
    public void setBlockedDates(Set<LocalDate> blockedDates) { this.blockedDates = blockedDates == null ? new HashSet<>() : new HashSet<>(blockedDates); }
    public void setConsultationDurationMinutes(int minutes) { this.consultationDurationMinutes = minutes; }

    public void setAvailableSlots(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots == null
                ? new ArrayList<>()
                : new ArrayList<>(availableSlots);
    }
}
