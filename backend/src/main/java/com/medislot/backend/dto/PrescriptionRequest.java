package com.medislot.backend.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record PrescriptionRequest(@NotBlank @Size(max=160) String medicineName,@NotBlank @Size(max=160) String dosage,@NotBlank @Size(max=160) String frequency,@NotBlank @Size(max=120) String duration,@Size(max=2000) String instructions) { }
