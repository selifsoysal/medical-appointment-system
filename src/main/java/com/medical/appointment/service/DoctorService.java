package com.medical.appointment.service;

import com.medical.appointment.model.Doctor;
import com.medical.appointment.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<String> getAllSpecializations() {
        return doctorRepository.findAllSpecializations();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> login(String email, String password) {
        Doctor d = doctorRepository.findByEmailAndPassword(email, password);
        return Optional.ofNullable(d);
    }
}
