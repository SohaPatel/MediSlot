package com.medislot.backend.dto;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
public record RescheduleRequest(@NotNull @FutureOrPresent LocalDate appointmentDate, @NotNull LocalTime appointmentTime) { }
