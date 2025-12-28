package com.medical.appointment.controller;

import com.medical.appointment.model.*;
import com.medical.appointment.service.AppointmentService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    private final AppointmentService appointmentService;

    public DoctorDashboardController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(d.getId()));
        return "doctor-dashboard";
    }

    @PostMapping("/appointment/update")
    public String updateAppointment(@RequestParam Long appointmentId,
                                    @RequestParam String status,
                                    @RequestParam(required = false) String doctorResponse,
                                    HttpSession session) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        appointmentService.getAppointmentById(appointmentId).ifPresent(randevu -> {
            if (randevu.getDoctor().getId().equals(d.getId())) {
                appointmentService.updateAppointmentStatus(randevu,
                        AppointmentStatus.valueOf(status),
                        doctorResponse);
            }
        });

        return "redirect:/doctor/dashboard";
    }
}
