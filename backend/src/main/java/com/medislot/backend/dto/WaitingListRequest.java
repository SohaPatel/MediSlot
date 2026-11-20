package com.medislot.backend.dto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
public record WaitingListRequest(@NotBlank @Size(max=100) String specialization, @NotNull @FutureOrPresent LocalDate appointmentDate, LocalTime preferredTime) { }
