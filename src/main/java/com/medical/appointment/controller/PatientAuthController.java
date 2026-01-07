package com.medical.appointment.controller;

import com.medical.appointment.model.Patient;
import com.medical.appointment.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientAuthController {

    private final PatientService patientService;

    public PatientAuthController(PatientService patientService) {
        this.patientService = patientService;
    }

@PostMapping("/login")
public String login(@RequestParam String email,
                    @RequestParam String password,
                    HttpSession session,
                    Model model) {

    if (email == null || email.isBlank()) {
        model.addAttribute("patientError", "Please enter a valid email!");
        model.addAttribute("emailValue", email);
        model.addAttribute("showForm", "patient");
        return "home";
    }

    if (!email.contains("@")) {
        model.addAttribute("patientError", "Please enter a valid email!");
        model.addAttribute("emailValue", email);
        model.addAttribute("showForm", "patient");
        return "home";
    }

    if (password == null || password.isBlank()) {
        model.addAttribute("patientError", "Please enter your password!");
        model.addAttribute("emailValue", email);
        model.addAttribute("showForm", "patient");
        return "home";
    }

    Optional<Patient> result = patientService.login(email, password);
    if (result.isPresent()) {
        session.setAttribute("loggedPatient", result.get());
        return "redirect:/patient/dashboard";
    } else {
        model.addAttribute("patientError", "Email or password is incorrect!");
        model.addAttribute("emailValue", email);
        model.addAttribute("showForm", "patient");
        return "home";
    }
}


    @PostMapping("/register")
    public String register(@ModelAttribute Patient patient, Model model) {
        if (patientService.emailExists(patient.getEmail())) {
            model.addAttribute("registerError", "This email is already registered");
            model.addAttribute("showForm", "register");
            return "home";
        }

        patientService.register(patient);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
