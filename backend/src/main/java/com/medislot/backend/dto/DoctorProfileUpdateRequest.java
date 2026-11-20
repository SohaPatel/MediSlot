package com.medislot.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
public record DoctorProfileUpdateRequest(@NotBlank @Size(min=2,max=100) String name, @NotBlank @Size(max=100) String specialization,
        @Min(10) @Max(180) int consultationDurationMinutes) { }
