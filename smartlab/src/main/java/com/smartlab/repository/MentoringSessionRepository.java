package com.smartlab.repository;

import com.smartlab.entity.MentoringSession;
import com.smartlab.enums.SessionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    boolean existsByLecturerIdAndSessionDateAndStartTimeAndStatusNot(

            Long lecturerId,

            LocalDate sessionDate,

            LocalTime startTime,

            SessionStatus status
    );

    List<MentoringSession>
    findByStudentUsername(String username);
}