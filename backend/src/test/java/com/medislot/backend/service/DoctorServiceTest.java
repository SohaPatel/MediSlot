package com.medislot.backend.service;

import com.medislot.backend.dto.DoctorRegistrationRequest;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.repository.AppointmentRepository;
import com.medislot.backend.repository.DoctorRepository;
import com.medislot.backend.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class DoctorServiceTest {
    @Test void registrationRequiresConfiguredCodeAndHashesPassword() {
        FakeDoctorRepository doctors=new FakeDoctorRepository();
        DoctorService service=new DoctorService(doctors, new AppointmentRepository(null), "onboarding-secret");
        Doctor registered=service.register(new DoctorRegistrationRequest("Dr Example", "Cardiology", "doc@example.test", "a-very-long-secret", "onboarding-secret"));
        assertEquals("doc@example.test", registered.getEmail());
        assertNotEquals("a-very-long-secret", registered.getPasswordHash());
        assertTrue(new BCryptPasswordEncoder().matches("a-very-long-secret", registered.getPasswordHash()));
        assertThrows(ResponseStatusException.class, () -> service.register(new DoctorRegistrationRequest("Other Doctor", "Cardiology", "other@example.test", "a-very-long-secret", "wrong")));
    }

    @Test void loginRejectsInactiveDoctors() {
        FakeDoctorRepository doctors=new FakeDoctorRepository();
        Doctor inactive=new Doctor(9L, "Dr. Inactive", "Cardiology", List.of());
        inactive.setEmail("inactive@example.test");
        inactive.setPasswordHash(new BCryptPasswordEncoder().encode("valid-password"));
        inactive.setActive(false);
        doctors.save(inactive);
        DoctorService service=new DoctorService(doctors, new AppointmentRepository(null), "code");
        assertThrows(ResponseStatusException.class, () -> service.login("inactive@example.test", "valid-password"));
    }

    private static class FakeDoctorRepository extends DoctorRepository {
        private final Map<String, Doctor> records = new HashMap<>();
        FakeDoctorRepository() { super(null); }
        @Override public boolean existsByEmail(String email) { return records.containsKey(email.toLowerCase()); }
        @Override public Optional<Doctor> findByEmail(String email) { return Optional.ofNullable(records.get(email.toLowerCase())); }
        @Override public Doctor save(Doctor doctor) { records.put(doctor.getEmail().toLowerCase(), doctor); return doctor; }
    }
}
