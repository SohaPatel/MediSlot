
package com.medislot.backend.service;

import com.medislot.backend.algorithm.BipartiteMatchingAlgorithm;
import com.medislot.backend.dto.AppointmentRequest;
import com.medislot.backend.model.Appointment;
import com.medislot.backend.model.Doctor;
import com.medislot.backend.model.Patient;
import com.medislot.backend.repository.AppointmentRepository;
import com.medislot.backend.repository.DoctorRepository;
import com.medislot.backend.repository.PatientRepository;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private final BipartiteMatchingAlgorithm matchingAlgorithm =
            new BipartiteMatchingAlgorithm();

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {

        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Appointment bookAppointment(AppointmentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Appointment request cannot be empty."
            );
        }

        if (request.getPatientId() == null) {
            throw new IllegalArgumentException(
                    "Patient ID is required."
            );
        }

        if (request.getSpecialization() == null ||
                request.getSpecialization().isBlank()) {
            throw new IllegalArgumentException(
                    "Doctor specialization is required."
            );
        }

        if (request.getAppointmentDate() == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (request.getAppointmentDate().isBefore(
                java.time.LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        Patient patient = patientRepository
                .findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient not found. Please register first."
                ));

        List<Doctor> matchingDoctors = doctorRepository.findAll()
                .stream()
                .filter(doctor -> doctor.getSpecialization()
                        .equalsIgnoreCase(request.getSpecialization()))
                .toList();

        if (matchingDoctors.isEmpty()) {
            throw new IllegalArgumentException(
                    "No doctor available for this specialization."
            );
        }

        List<Doctor> eligibleDoctors = new ArrayList<>();
        List<List<Integer>> adjacency = new ArrayList<>();
        List<LocalTime> allSlots = new ArrayList<>();

        for (Doctor doctor : matchingDoctors) {

            eligibleDoctors.add(doctor);

            List<Integer> doctorSlots = new ArrayList<>();

            for (LocalTime slot : doctor.getAvailableSlots()) {

                int slotIndex = allSlots.indexOf(slot);

                if (slotIndex == -1) {
                    slotIndex = allSlots.size();
                    allSlots.add(slot);
                }

                if (request.getPreferredTime() == null ||
                        request.getPreferredTime().equals(slot)) {
                    doctorSlots.add(slotIndex);
                }
            }

            adjacency.add(doctorSlots);
        }

        int[] matching = matchingAlgorithm.maximumMatching(
                eligibleDoctors.size(),
                allSlots.size(),
                adjacency
        );

        for (int i = 0; i < matching.length; i++) {

            if (matching[i] == -1) {
                continue;
            }

            Doctor doctor = eligibleDoctors.get(i);
            LocalTime selectedTime = allSlots.get(matching[i]);

            try {
                Appointment appointment = new Appointment(
                        null,
                        request.getPatientId(),
                        patient.getName(),
                        doctor.getId(),
                        doctor.getName(),
                        request.getAppointmentDate(),
                        selectedTime,
                        "CONFIRMED"
                );

                return appointmentRepository.save(appointment);

            } catch (IllegalStateException exception) {
                // Try another available doctor if this slot was taken.
            }
        }

        throw new IllegalStateException(
                "No suitable appointment slot is currently available."
        );
    }

    // Cancel an existing appointment
    public Appointment cancelAppointment(Long appointmentId) {

        if (appointmentId == null) {
            throw new IllegalArgumentException(
                    "Appointment ID is required."
            );
        }

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Appointment not found."
                ));

        if ("CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException(
                    "This appointment has already been cancelled."
            );
        }

        if (!"CONFIRMED".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException(
                    "Only confirmed appointments can be cancelled."
            );
        }

        appointment.setStatus("CANCELLED");

        return appointmentRepository.update(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}
