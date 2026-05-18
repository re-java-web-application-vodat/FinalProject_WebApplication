package com.smartlab.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingDTO {

    private Long lecturerId;

    private Long departmentId;

    private LocalDate sessionDate;

    private LocalTime startTime;

    private LocalTime endTime;
}