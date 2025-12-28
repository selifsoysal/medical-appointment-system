package com.medical.appointment.service;

import com.medical.appointment.model.Appointment;
import com.medical.appointment.model.AppointmentStatus;
import com.medical.appointment.model.Patient;
import com.medical.appointment.model.TimeSlot;
import com.medical.appointment.repository.AppointmentRepository;
import com.medical.appointment.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, TimeSlotRepository timeSlotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public boolean hasActiveAppointmentInSpecialization(Patient patient, String specialization) {
        return getAppointmentsByPatient(patient.getId()).stream()
                .anyMatch(a -> a.getDoctor().getSpecialization().equals(specialization)
                        && (a.getStatus() == AppointmentStatus.NEW || a.getStatus() == AppointmentStatus.CONFIRMED));
    }

    public Optional<TimeSlot> getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id);
    }

    public void updateAppointmentStatus(Appointment appointment, AppointmentStatus status, String doctorResponse) {
        appointment.setStatus(status);
        appointment.setDoctorResponse(doctorResponse);
        appointmentRepository.save(appointment);
    }
}
