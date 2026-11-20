package com.medislot.backend.dto;
import com.medislot.backend.model.Doctor;
import java.util.List;
public record DoctorRecommendation(Doctor doctor, int score, List<String> reasons) { }
