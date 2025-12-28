package com.medical.appointment.controller;


import com.medical.appointment.service.DoctorService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor")
public class DoctorAuthController {

    private final DoctorService doctorService;

    public DoctorAuthController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        return doctorService.login(email, password)
                .map(d -> {
                    session.setAttribute("loggedDoctor", d);
                    return "redirect:/doctor/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("doctorError", "Email or password is incorrect!");
                    model.addAttribute("showForm", "doctor");
                    return "home";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedDoctor");
        return "redirect:/";
    }
}
