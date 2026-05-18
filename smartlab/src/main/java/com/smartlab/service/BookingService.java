package com.smartlab.service;

import com.smartlab.dto.BookingDTO;

import com.smartlab.entity.Department;
import com.smartlab.entity.Lecturer;
import com.smartlab.entity.MentoringSession;
import com.smartlab.entity.User;

import com.smartlab.enums.SessionStatus;

import com.smartlab.repository.DepartmentRepository;
import com.smartlab.repository.LecturerRepository;
import com.smartlab.repository.MentoringSessionRepository;
import com.smartlab.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final MentoringSessionRepository repository;

    private final UserRepository userRepository;

    private final LecturerRepository lecturerRepository;

    private final DepartmentRepository departmentRepository;

    public void book(BookingDTO dto, String username){

        LocalDateTime bookingTime = LocalDateTime.of(dto.getSessionDate(), dto.getStartTime());

        // CHẶN ĐẶT QUÁ KHỨ
        if(bookingTime.isBefore(LocalDateTime.now())){
            throw new RuntimeException("Không thể đặt chỗ sau giờ quy định.");
        }

        // CHỐNG XUNG ĐỘT
        boolean exists = repository.existsByLecturerIdAndSessionDateAndStartTimeAndStatusNot(dto.getLecturerId(), dto.getSessionDate(), dto.getStartTime(), SessionStatus.CANCELLED);

        if(exists){
            throw new RuntimeException("Giáo viên đã có lịch hẹn!");
        }

        User student = userRepository.findByUsername(username).orElseThrow();

        Lecturer lecturer = lecturerRepository.findById(dto.getLecturerId()).orElseThrow();

        Department department = departmentRepository.findById(dto.getDepartmentId()).orElseThrow();

        MentoringSession session = MentoringSession.builder()

                        .student(student)

                        .lecturer(lecturer)

                        .department(department)

                        .sessionDate(dto.getSessionDate())

                        .startTime(dto.getStartTime())

                        .endTime(dto.getEndTime())

                        .status(SessionStatus.PENDING)

                        .createdAt(LocalDateTime.now())

                        .build();

        repository.save(session);
    }
}
