package com.medical.appointment.repository;

import com.medical.appointment.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByDoctorIdAndDateOrderByStartTimeAsc(Long doctorId, LocalDate date);

    List<TimeSlot> findByDoctorIdOrderByDateAscStartTimeAsc(Long doctorId);
}
