package com.medislot.backend.dto;
import com.medislot.backend.model.Patient;
public record LoginResponse(Patient patient, String token, String role) { }
