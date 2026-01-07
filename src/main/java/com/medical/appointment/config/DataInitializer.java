package com.medical.appointment.config;

import com.medical.appointment.model.Doctor;
import com.medical.appointment.repository.DoctorRepository;
import com.medical.appointment.service.TimeSlotService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DoctorRepository doctorRepository;
    private final TimeSlotService timeSlotService;

    @PostConstruct
    public void init() {
        System.out.println("🔥 SLOT GENERATION STARTED");

        try {
            List<Doctor> doctors = doctorRepository.findAll();

            if (doctors == null || doctors.isEmpty()) {
                System.out.println("⚠️ No doctors found in the database, skipping slot generation.");
            } else {
                for (Doctor doctor : doctors) {
                    if (doctor != null) {
                        try {
                            timeSlotService.generateMonthlySlots(doctor);
                        } catch (Exception e) {
                            System.err.println("❌ Failed to generate slots for doctor ID " + doctor.getId() 
                                               + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Slot generation failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✅ SLOT GENERATION FINISHED");
    }
}
