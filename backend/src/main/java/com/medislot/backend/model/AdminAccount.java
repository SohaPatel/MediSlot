package com.medislot.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
@Entity @Table(name="admin_accounts")
public class AdminAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=254) private String email;
    @JsonIgnore @Column(nullable=false, length=255) private String passwordHash;
    @Column(nullable=false) private boolean active=true;
    protected AdminAccount() { }
    public AdminAccount(String email, String passwordHash) { this.email=email; this.passwordHash=passwordHash; }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    @JsonIgnore public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active=active; }
}
