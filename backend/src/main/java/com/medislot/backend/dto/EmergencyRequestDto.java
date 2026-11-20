package com.medislot.backend.dto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
public record EmergencyRequestDto(@NotBlank @Size(max=100) String specialization,@NotNull @FutureOrPresent LocalDate requestedDate,@NotBlank @Size(max=2000) String reason) { }
