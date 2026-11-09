
package com.medislot.backend.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Doctor {

    private Long id;
    private String name;
    private String specialization;
    private List<LocalTime> availableSlots;

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

    public void setAvailableSlots(List<LocalTime> availableSlots) {
        this.availableSlots = availableSlots == null
                ? new ArrayList<>()
                : new ArrayList<>(availableSlots);
    }
}
