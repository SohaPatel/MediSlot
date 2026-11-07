package com.medislot.backend.model;

import java.time.LocalDateTime;

public class Patient {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer age;
    private LocalDateTime registeredAt;

    public Patient() {
    }

    public Patient(Long id, String name, String email,
                   String phone, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getAge() {
        return age;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}