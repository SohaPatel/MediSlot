package com.medislot.backend.config;

import com.medislot.backend.model.Doctor;
import com.medislot.backend.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalTime;
import java.util.List;
import com.medislot.backend.model.AdminAccount;
import com.medislot.backend.repository.AdminAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seedDoctors(DoctorRepository doctors) {
        return args -> {
            if (doctors.count() == 0) {
                doctors.save(new Doctor(null, "Dr. Rahul Sharma", "Cardiologist", List.of(LocalTime.of(9,0), LocalTime.of(10,0), LocalTime.of(11,0), LocalTime.of(12,0))));
                doctors.save(new Doctor(null, "Dr. Priya Mehta", "Dermatologist", List.of(LocalTime.of(10,0), LocalTime.of(11,0), LocalTime.of(14,0), LocalTime.of(15,0))));
                doctors.save(new Doctor(null, "Dr. Amit Verma", "General Physician", List.of(LocalTime.of(9,0), LocalTime.of(13,0), LocalTime.of(14,0), LocalTime.of(16,0))));
            }
        };
    }

    @Bean
    CommandLineRunner seedAdmin(AdminAccountRepository admins,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.password:}") String password) {
        return args -> {
            if (!email.isBlank() && !password.isBlank() && admins.findByEmailIgnoreCase(email).isEmpty()) {
                admins.save(new AdminAccount(email.trim().toLowerCase(), new BCryptPasswordEncoder().encode(password)));
            }
        };
    }
}
