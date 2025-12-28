package com.medical.appointment.controller;

import com.medical.appointment.service.DoctorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PatientDashboardController {

    private final DoctorService doctorService;

    public PatientDashboardController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/patient/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("loggedPatient") == null) {
            return "redirect:/patient/login";
        }

        model.addAttribute("specializations", doctorService.getAllSpecializations());
        return "patient-dashboard";
    }
}
