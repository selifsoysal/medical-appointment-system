package com.medical.appointment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String specialization;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;

}
