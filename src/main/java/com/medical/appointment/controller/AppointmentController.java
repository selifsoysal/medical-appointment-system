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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
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

    // ---------------- Departments → Doctors ----------------
    @GetMapping("/doctors")
    public String getDoctors(@RequestParam String specialization, Model model, HttpSession session) {

        prepareModel(model);

        Patient patient = (Patient) session.getAttribute("loggedPatient");

        if (patient != null && appointmentService.hasActiveAppointmentInSpecialization(patient, specialization)) {
            model.addAttribute(
                    "errorMessage",
                    "You already have an active appointment in this department. Please cancel it before booking a new one."
            );
            model.addAttribute("selectedSpecialization", specialization);
            return "patient-dashboard";
        }

        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        model.addAttribute("doctors", doctors);
        model.addAttribute("selectedSpecialization", specialization);

        return "patient-dashboard";
    }

    // ---------------- Doctors → TimeSlots ----------------
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

        // DEĞİŞİKLİK 3: Tarih null ise YARIN değil BUGÜN olsun.
        if (date == null) date = LocalDate.now();

        // DB'den slotları al (Service artık saati geçmişleri filtreliyor)
        List<TimeSlot> slots = slotService.getSlotsByDate(doc, date);

        model.addAttribute("doctors", Collections.singletonList(doc));
        model.addAttribute("selectedSpecialization", specialization);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedDate", date);
        model.addAttribute("timeSlots", slots);

        return "patient-dashboard";
    }

    // ---------------- Create Appointment (show confirmation page) ----------------
    @PostMapping("/appointment")
    public String createAppointment(@RequestParam Long doctorId,
                                    @RequestParam Long timeSlotId,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        Patient patient = (Patient) session.getAttribute("loggedPatient");
        if (patient == null) return "redirect:/auth/patient/login";

        Optional<Doctor> docOpt = doctorService.getDoctorById(doctorId);
        if (docOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Doctor not found!");
            return "redirect:/patient/dashboard";
        }

        Doctor doc = docOpt.get();

        if (appointmentService.hasActiveAppointmentInSpecialization(patient, doc.getSpecialization())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "You already have an active appointment in this department."
            );
            return "redirect:/patient/dashboard";
        }

        // DEĞİŞİKLİK 4: Slotu ID ile buluyoruz (Eski kodda sadece yarına bakıyordu, bugünü bulamıyordu)
        // Tüm slotlardan ID eşleşeni bul (Performans için repository'de findById olması iyidir ama bozmamak için böyle bıraktım)
        Optional<TimeSlot> slotOpt = slotService.getAllSlots().stream()
                .filter(s -> s.getId().equals(timeSlotId))
                .findFirst();

        if (slotOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Time slot not found!");
            return "redirect:/patient/dashboard";
        }

        TimeSlot slot = slotOpt.get();

        // DEĞİŞİKLİK 5: GÜVENLİK KONTROLÜ
        // Eğer seçilen slot BUGÜN ise ve saati GEÇMİŞ ise engelle.
        if (slot.getDate().equals(LocalDate.now()) && slot.getStartTime().isBefore(LocalTime.now())) {
            redirectAttributes.addFlashAttribute("errorMessage", "The selected time slot has expired. Please choose a future time.");
            return "redirect:/patient/dashboard";
        }

        if (!slot.isAvailable()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Time slot not available!");
            return "redirect:/patient/dashboard";
        }

        model.addAttribute("selectedDoctor", doc);
        model.addAttribute("selectedSlot", slot);
        model.addAttribute("patient", patient);

        return "appointment-confirmation";
    }

    // ---------------- Confirm Appointment ----------------
    @PostMapping("/appointment/confirm")
    public String confirmAppointment(@RequestParam Long timeSlotId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {

        Patient patient = (Patient) session.getAttribute("loggedPatient");
        if (patient == null) return "redirect:/auth/patient/login";

        Optional<TimeSlot> slotOpt = slotService.getAllSlots()
                .stream()
                .filter(s -> s.getId().equals(timeSlotId))
                .findFirst();

        if (slotOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Time slot not found!");
            return "redirect:/patient/dashboard";
        }

        TimeSlot slot = slotOpt.get();
        
        // DEĞİŞİKLİK 6: Confirm aşamasında da son bir saat kontrolü (İsteğe bağlı ama güvenlidir)
        if (slot.getDate().equals(LocalDate.now()) && slot.getStartTime().isBefore(LocalTime.now())) {
             redirectAttributes.addFlashAttribute("errorMessage", "Cannot book past time slots.");
             return "redirect:/patient/dashboard";
        }
        
        Doctor doc = slot.getDoctor();

        if (appointmentService.hasActiveAppointmentInSpecialization(
                patient, doc.getSpecialization())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "You already have an active appointment in this department."
            );
            return "redirect:/patient/dashboard";
        }

        // --------- APPOINTMENT OLUŞTUR ---------
        Appointment newApp = new Appointment();
        newApp.setPatient(patient);
        newApp.setDoctor(doc);
        newApp.setTimeSlot(slot);
        newApp.setStatus(AppointmentStatus.NEW);

        // ÇİFT YÖNLÜ İLİŞKİ
        slot.setAppointment(newApp);
        slot.setAvailable(false);

        // önce appointment
        appointmentService.saveAppointment(newApp);

        // sonra slot
        slotService.saveTimeSlot(slot);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your appointment with Dr. " + doc.getFullName() +
                        " on " + slot.getDate() +
                        " at " + slot.getStartTime() +
                        " has been successfully created."
        );

        return "redirect:/patient/dashboard";
    }

    // ---------------- Helper: Prepare Model ----------------
    private void prepareModel(Model model) {
        List<Doctor> allDocs = doctorService.getAllDoctors();
        Set<String> specs = allDocs.stream()
                .map(Doctor::getSpecialization)
                .collect(Collectors.toSet());

        model.addAttribute("specializations", specs);

        // DEĞİŞİKLİK 7: Takvim listesi YARINDAN değil BUGÜNDEN başlasın.
        // plusDays(1) -> now()
        LocalDate min = LocalDate.now(); 
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 30; i++) dates.add(min.plusDays(i));
        model.addAttribute("availableDates", dates);
        
        // Burayı da güncellemek gerekir, varsayılan seçili tarih bugün olsun
        model.addAttribute("selectedDate", min);
    }
}