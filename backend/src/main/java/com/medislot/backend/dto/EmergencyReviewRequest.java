package com.medislot.backend.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record EmergencyReviewRequest(@NotBlank String decision,@Min(1) @Max(5) int clinicalPriority,@Size(max=2000) String clinicalNotes) { }
