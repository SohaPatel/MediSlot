package com.medislot.backend.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record AdminDoctorRequest(@NotBlank @Size(min=2,max=100) String name, @NotBlank @Size(max=100) String specialization, @Email @Size(max=254) String email) { }
