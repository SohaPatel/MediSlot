package com.medislot.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> home() {

        Map<String, String> response = new HashMap<>();

        response.put("project", "MediSlot");
        response.put("message",
                "Smart Appointment Scheduling System");
        response.put("status",
                "Backend is running successfully");

        return response;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {

        Map<String, String> response = new HashMap<>();

        response.put("status", "UP");
        response.put("application", "MediSlot");

        return response;
    }
}
