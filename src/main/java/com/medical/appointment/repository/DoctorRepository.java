package com.medical.appointment.repository;

import com.medical.appointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByEmailAndPassword(String email, String password);

    List<Doctor> findBySpecialization(String specialization);

    @Query("select distinct d.specialization from Doctor d")
    List<String> findAllSpecializations();
}
