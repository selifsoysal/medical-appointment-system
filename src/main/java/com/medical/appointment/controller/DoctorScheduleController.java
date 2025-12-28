package com.medical.appointment.controller;

import com.medical.appointment.model.Doctor;
import com.medical.appointment.model.TimeSlot;
import com.medical.appointment.service.TimeSlotService;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorScheduleController {

    private final TimeSlotService slotService;

    public DoctorScheduleController(TimeSlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping("/schedule")
    public String schedulePage(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               HttpSession session,
                               Model model) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        List<TimeSlot> liste;
        if (date != null) {
            liste = slotService.getOrCreateDailySlots(d, date);
            model.addAttribute("selectedDate", date);
        } else {
            liste = slotService.getOrCreateDailySlots(d, LocalDate.now().plusDays(1));
        }

        model.addAttribute("timeSlots", liste);
        return "doctor-schedule";
    }

    @PostMapping("/timeslot/add")
    public String addTimeSlot(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                              HttpSession session) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        TimeSlot slot = new TimeSlot();
        slot.setDoctor(d);
        slot.setDate(date);
        slot.setStartTime(startTime);
        slot.setAvailable(true);

        slotService.saveTimeSlot(slot);

        return "redirect:/doctor/schedule?date=" + date;
    }

    @PostMapping("/timeslot/delete")
    public String deleteTimeSlot(@RequestParam Long timeSlotId,
                                 @RequestParam(required = false) String date,
                                 HttpSession session) {

        Doctor d = (Doctor) session.getAttribute("loggedDoctor");
        if (d == null) return "redirect:/doctor/login";

        slotService.getOrCreateDailySlots(d, LocalDate.now()).stream()
                .filter(slot -> slot.getId().equals(timeSlotId))
                .findFirst()
                .ifPresent(slotService::deleteTimeSlot);

        if (date != null && !date.isEmpty() && !date.equals("null")) {
            return "redirect:/doctor/schedule?date=" + date;
        }

        return "redirect:/doctor/schedule";
    }
}
