
package com.medislot.backend.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "patients", uniqueConstraints = @UniqueConstraint(name = "uk_patients_email", columnNames = "email"))
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column
    private Integer age;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active = true;

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
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

    @JsonIgnore
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
