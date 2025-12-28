package com.medical.appointment.controller;

import com.medical.appointment.model.Appointment;
import com.medical.appointment.model.AppointmentStatus;
import com.medical.appointment.model.Patient;
import com.medical.appointment.model.TimeSlot;
import com.medical.appointment.service.AppointmentService;
import com.medical.appointment.service.TimeSlotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientProfileController {

    private final AppointmentService appointmentService;
    private final TimeSlotService timeSlotService;

    public PatientProfileController(AppointmentService appointmentService,
                                    TimeSlotService timeSlotService) {
        this.appointmentService = appointmentService;
        this.timeSlotService = timeSlotService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Patient p = (Patient) session.getAttribute("loggedPatient");
        if (p == null) return "redirect:/";

        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(p.getId());
        model.addAttribute("appointments", appointments);

        return "patient-profile";
    }

    @PostMapping("/appointment/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        Patient p = (Patient) session.getAttribute("loggedPatient");
        if (p == null) return "redirect:/";

        appointmentService.getAppointmentById(id).ifPresent(appointment -> {
            if (appointment.getPatient().getId().equals(p.getId()) &&
                    (appointment.getStatus() == AppointmentStatus.NEW || appointment.getStatus() == AppointmentStatus.CONFIRMED)) {

                TimeSlot slot = appointment.getTimeSlot();
                if (slot != null) {
                    slot.setAvailable(true);
                    timeSlotService.saveTimeSlot(slot);
                }

                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointmentService.saveAppointment(appointment);
            }
        });

        return "redirect:/patient/profile";
    }
}
