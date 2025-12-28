package com.medical.appointment.controller;

import com.medical.appointment.model.*;
import com.medical.appointment.service.AppointmentService;
import com.medical.appointment.service.DoctorService;
import com.medical.appointment.service.TimeSlotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final TimeSlotService slotService;

    public AppointmentController(AppointmentService appointmentService,
                                 DoctorService doctorService,
                                 TimeSlotService slotService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.slotService = slotService;
    }

    @GetMapping("/doctors")
    public String getDoctors(@RequestParam String specialization, Model model, HttpSession session) {

        prepareModel(model);

        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);

        model.addAttribute("doctors", doctors);
        model.addAttribute("selectedSpecialization", specialization);

        Patient patient = (Patient) session.getAttribute("loggedPatient");
        boolean hasApp = patient != null && appointmentService.hasActiveAppointmentInSpecialization(patient, specialization);

        model.addAttribute("hasAppointment", hasApp);
        return "patient-dashboard";
    }

    @GetMapping("/timeslots")
    public String getTimeSlots(@RequestParam Long doctorId,
                               @RequestParam String specialization,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {

        prepareModel(model);

        Optional<Doctor> docOpt = doctorService.getDoctorById(doctorId);
        if (docOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Doctor not found!");
            return "patient-dashboard";
        }
        Doctor doc = docOpt.get();

        if (date == null) {
            date = LocalDate.now().plusDays(1);
        }

        List<TimeSlot> slots = slotService.getOrCreateDailySlots(doc, date);

        model.addAttribute("doctors", Arrays.asList(doc)); 
        model.addAttribute("selectedSpecialization", specialization);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedDate", date);
        model.addAttribute("timeSlots", slots);

        return "patient-dashboard";
    }

    @PostMapping("/appointment")
    public String createAppointment(@RequestParam Long doctorId, @RequestParam Long timeSlotId,
                                    HttpSession session, Model model) {

        Patient patient = (Patient) session.getAttribute("loggedPatient");
        if (patient == null) return "redirect:/auth/patient/login";

        Optional<Doctor> docOpt = doctorService.getDoctorById(doctorId);
        if (docOpt.isEmpty()) return "redirect:/patient/doctors?specialization=";

        Doctor doc = docOpt.get();

        if (appointmentService.hasActiveAppointmentInSpecialization(patient, doc.getSpecialization())) {
            model.addAttribute("errorMessage", "You already have an appointment in this department!");
            return getDoctors(doc.getSpecialization(), model, session);
        }

        Optional<TimeSlot> slotOpt = appointmentService.getTimeSlotById(timeSlotId);
        if (slotOpt.isEmpty() || !slotOpt.get().isAvailable()) {
            model.addAttribute("errorMessage", "Time slot not available!");
            return getDoctors(doc.getSpecialization(), model, session);
        }

        TimeSlot slot = slotOpt.get();

        model.addAttribute("selectedDoctor", doc);
        model.addAttribute("selectedSlot", slot);
        model.addAttribute("patient", patient);

        return "appointment-confirmation";
    }

    @PostMapping("/appointment/confirm")
    public String confirmAppointment(@RequestParam Long timeSlotId, HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("loggedPatient");
        if (patient == null) return "redirect:/auth/patient/login";

        Optional<TimeSlot> slotOpt = appointmentService.getTimeSlotById(timeSlotId);
        if (slotOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Time slot not found!");
            return "patient-dashboard";
        }

        TimeSlot slot = slotOpt.get();
        Doctor doc = slot.getDoctor();

        if (appointmentService.hasActiveAppointmentInSpecialization(patient, doc.getSpecialization())) {
            model.addAttribute("errorMessage", "Error: Existing appointment found.");
            return "patient-dashboard";
        }

        Appointment newApp = new Appointment();
        newApp.setPatient(patient);
        newApp.setDoctor(doc);
        newApp.setTimeSlot(slot);
        newApp.setStatus(AppointmentStatus.NEW);

        slot.setAvailable(false);

        appointmentService.saveAppointment(newApp);
        slotService.saveTimeSlot(slot);

        return "redirect:/patient/dashboard";
    }

    private void prepareModel(Model model) {
        List<Doctor> allDocs = doctorService.getAllDoctors();
        Set<String> specs = allDocs.stream()
                .map(Doctor::getSpecialization)
                .collect(Collectors.toSet());

        model.addAttribute("specializations", specs);

        LocalDate min = LocalDate.now().plusDays(1);
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            dates.add(min.plusDays(i));
        }
        model.addAttribute("availableDates", dates);
        model.addAttribute("selectedDate", min);
    }
}
