package com.medical.appointment.repository;

import com.medical.appointment.model.Appointment;
import com.medical.appointment.model.AppointmentStatus;
import com.medical.appointment.model.Patient;
import com.medical.appointment.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByStatus(AppointmentStatus status);

    boolean existsByPatientAndDoctor_SpecializationAndStatus(
            Patient patient,
            String specialization,
            AppointmentStatus status
    );
}
