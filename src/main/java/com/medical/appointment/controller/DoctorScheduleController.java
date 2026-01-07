package com.medical.appointment.controller;

import com.medical.appointment.model.*;
import com.medical.appointment.service.AppointmentService;
import com.medical.appointment.service.TimeSlotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorScheduleController {

    private final TimeSlotService slotService;
    private final AppointmentService appointmentService;

    public DoctorScheduleController(TimeSlotService slotService, AppointmentService appointmentService) {
        this.slotService = slotService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/schedule")
    public String schedulePage(@RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               HttpSession session, Model model) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        // DEĞİŞİKLİK 1: Varsayılan tarih artık BUGÜN.
        if (date == null) date = LocalDate.now();

        // DEĞİŞİKLİK 2: Service katmanındaki yeni mantıkla (bugünse geçmiş saatleri filtreleyen) slotları al.
        List<TimeSlot> slots = slotService.getSlotsByDate(d, date);
        
        // Slotları model'e ekle
        model.addAttribute("timeSlots", slots != null ? slots : Collections.emptyList());
        
        // DEĞİŞİKLİK 3: View tarafındaki takvim filtresinde seçili kalması için tarihi gönderiyoruz.
        model.addAttribute("selectedDate", date);

        return "doctor-schedule";
    }

    @PostMapping("/timeslot/add")
    public String addTimeSlot(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                              HttpSession session, RedirectAttributes redirectAttrs) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        // DEĞİŞİKLİK 4: Geçmişe slot ekleme kontrolü.
        // Eğer tarih bugünse ve saat geçmişse VEYA tarih dünden öncesiyse ekleme yapma.
        if (date.isBefore(LocalDate.now()) || 
           (date.equals(LocalDate.now()) && startTime.isBefore(LocalTime.now()))) {
            redirectAttrs.addFlashAttribute("errorMessage", "You cannot add a time slot to a past date or time.");
            return "redirect:/doctor/schedule?date=" + date;
        }

        TimeSlot slot = new TimeSlot();
        slot.setDoctor(d);
        slot.setDate(date);
        slot.setStartTime(startTime);
        slot.setAvailable(true);

        slotService.saveTimeSlot(slot);
        redirectAttrs.addFlashAttribute("successMessage", "Time slot added successfully.");
        return "redirect:/doctor/schedule?date=" + date;
    }

    @PostMapping("/timeslot/delete")
    public String deleteTimeSlot(@RequestParam Long timeSlotId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 HttpSession session, RedirectAttributes redirectAttrs) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        // Not: Burada slotService.getSlotsByDate kullanmak yerine direkt repository'den ID ile çekmek 
        // daha güvenli olabilir (çünkü getSlotsByDate geçmiş saatleri filtrelerse silemeyebilirsiniz).
        // Ancak kodunuzu bozmamak adına mevcut mantığı koruyarak tüm slotları kontrol ediyoruz.
        Optional<TimeSlot> slotOpt = slotService.getAllSlots().stream()
                .filter(s -> s.getId().equals(timeSlotId))
                .findFirst();

        if (slotOpt.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Time slot not found!");
            return "redirect:/doctor/schedule?date=" + date;
        }

        TimeSlot slot = slotOpt.get();

        if (slot.getAppointment() != null &&
                (slot.getAppointment().getStatus() == AppointmentStatus.NEW ||
                 slot.getAppointment().getStatus() == AppointmentStatus.CONFIRMED)) {

            redirectAttrs.addFlashAttribute("errorMessage", "Cannot delete slot: active appointment exists.");
            return "redirect:/doctor/schedule?date=" + date;
        }

        if (slot.getAppointment() != null) {
            appointmentService.deleteAppointment(slot.getAppointment());
        }

        slotService.deleteTimeSlot(slot);
        redirectAttrs.addFlashAttribute("successMessage", "Time slot deleted successfully.");
        return "redirect:/doctor/schedule?date=" + date;
    }
}