package com.medislot.backend.dto;
import com.medislot.backend.model.Doctor;
public record DoctorAuthResponse(Doctor doctor, String token, String role) { }
