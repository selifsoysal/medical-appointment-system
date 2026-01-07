package com.medical.appointment.service;

import com.medical.appointment.model.Doctor;
import com.medical.appointment.model.TimeSlot;
import com.medical.appointment.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public TimeSlotService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public List<TimeSlot> getSlotsByDate(Doctor doctor, LocalDate date) {
        LocalDate today = LocalDate.now();

        if (date.isBefore(today) || date.isAfter(today.plusMonths(1))) {
            return List.of();
        }

        List<TimeSlot> slots = timeSlotRepository.findByDoctorIdAndDateOrderByStartTimeAsc(doctor.getId(), date);

        if (date.equals(today)) {
            LocalTime currentTime = LocalTime.now();
            
            return slots.stream()
                    .filter(slot -> slot.getStartTime().isAfter(currentTime)) 
                    .toList();
        }

        return slots;
    }

    public List<TimeSlot> saveAll(List<TimeSlot> slots) {
        return timeSlotRepository.saveAll(slots);
    }

    public TimeSlot saveTimeSlot(TimeSlot slot) {
        return timeSlotRepository.save(slot);
    }

    public void deleteTimeSlot(TimeSlot slot) {
        timeSlotRepository.delete(slot);
    }

    public List<TimeSlot> getAllSlots() {
        return timeSlotRepository.findAll();
    }

    // ---------------- Otomatik 1 aylık slot oluştur ----------------
    public void generateMonthlySlots(Doctor doctor) {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusMonths(1);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            List<TimeSlot> existing = getSlotsByDate(doctor, date);
            if (!existing.isEmpty()) continue;

            List<TimeSlot> slots = new ArrayList<>();
            LocalTime time = LocalTime.of(9, 0);
            while (time.isBefore(LocalTime.of(17, 0))) {
                TimeSlot slot = new TimeSlot();
                slot.setDoctor(doctor);
                slot.setDate(date);
                slot.setStartTime(time);
                slot.setAvailable(true);
                slots.add(slot);
                time = time.plusMinutes(30);
            }
            saveAll(slots);
        }
    }
}
