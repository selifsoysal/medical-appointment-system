package com.medical.appointment.service;

import com.medical.appointment.model.Patient;
import com.medical.appointment.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public boolean emailExists(String email) {
        return patientRepository.findByEmail(email).isPresent();
    }

    public Patient register(Patient patient) {
        return patientRepository.save(patient);
    }

    public Optional<Patient> login(String email, String password) {
        return patientRepository.findByEmailAndPassword(email, password);
    }


    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
