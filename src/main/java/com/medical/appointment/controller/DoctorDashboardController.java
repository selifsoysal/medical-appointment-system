package com.medical.appointment.controller;

import com.medical.appointment.model.*;
import com.medical.appointment.service.AppointmentService;
import com.medical.appointment.service.TimeSlotService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor")
public class DoctorDashboardController {

    private final AppointmentService appointmentService;
    private final TimeSlotService slotService;

    public DoctorDashboardController(AppointmentService appointmentService,
                                     TimeSlotService slotService) {
        this.appointmentService = appointmentService;
        this.slotService = slotService;
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

            // 1️⃣ Sadece kendi randevusuna müdahale edebilir
            if (!randevu.getDoctor().getId().equals(d.getId())) return;

            // 2️⃣ Hasta iptal etmişse doktor hiçbir değişiklik yapamaz
            if (randevu.getStatus() == AppointmentStatus.CANCELLED) {
                // opsiyonel: bir mesaj göstermek istersen model attribute ekleyebilirsin
                return;
            }

            // 3️⃣ Status güncelle
            AppointmentStatus newStatus = AppointmentStatus.valueOf(status);
            appointmentService.updateAppointmentStatus(randevu, newStatus, doctorResponse);

            // 4️⃣ Slot güncelleme
            TimeSlot slot = randevu.getTimeSlot();
            if (slot != null) {
                if (newStatus == AppointmentStatus.REJECTED || newStatus == AppointmentStatus.CANCELLED) {
                    slot.setAvailable(true);   // slot tekrar açılır
                } else if (newStatus == AppointmentStatus.CONFIRMED) {
                    slot.setAvailable(false);  // slot dolu
                }
                slotService.saveTimeSlot(slot);
            }
        });

        return "redirect:/doctor/dashboard";
    }
}
