package com.medislot.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record MedicalHistoryRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 4000) String details,
        @Size(max = 2000) String allergies) { }
