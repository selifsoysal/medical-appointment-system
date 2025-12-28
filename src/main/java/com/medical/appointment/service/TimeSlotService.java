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

    public List<TimeSlot> getOrCreateDailySlots(Doctor doctor, LocalDate date) {

        if (date.isBefore(LocalDate.now().plusDays(1)) || date.isAfter(LocalDate.now().plusMonths(1))) {
            return List.of();
        }

        List<TimeSlot> existing = timeSlotRepository.findByDoctorIdAndDateOrderByStartTimeAsc(doctor.getId(), date);
        if (!existing.isEmpty()) return existing;

        List<TimeSlot> generated = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        while (start.isBefore(end)) {
            TimeSlot slot = new TimeSlot();
            slot.setDoctor(doctor);
            slot.setDate(date);
            slot.setStartTime(start);
            slot.setAvailable(true);
            generated.add(slot);
            start = start.plusMinutes(30);
        }

        return timeSlotRepository.saveAll(generated);
    }

    public TimeSlot saveTimeSlot(TimeSlot slot) {
        return timeSlotRepository.save(slot);
    }

    public void deleteTimeSlot(TimeSlot slot) {
        timeSlotRepository.delete(slot);
    }
}
