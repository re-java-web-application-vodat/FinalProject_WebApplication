package com.smartlab.repository;

import com.smartlab.entity.MentoringSession;
import com.smartlab.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    @Query("""
            SELECT COUNT(s) > 0 FROM MentoringSession s
            WHERE s.lecturer.id = :lecturerId
            AND s.sessionDate = :sessionDate
            AND s.status <> :cancelled
            AND s.startTime < :endTime
            AND s.endTime > :startTime
            """)
    boolean existsOverlappingLecturerSession(
            @Param("lecturerId") Long lecturerId,
            @Param("sessionDate") LocalDate sessionDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("cancelled") SessionStatus cancelled
    );

    @Query("""
            SELECT COUNT(s) > 0 FROM MentoringSession s
            WHERE s.student.id = :studentId
            AND s.sessionDate = :sessionDate
            AND s.status <> :cancelled
            AND s.startTime < :endTime
            AND s.endTime > :startTime
            """)
    boolean existsOverlappingStudentSession(
            @Param("studentId") Long studentId,
            @Param("sessionDate") LocalDate sessionDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("cancelled") SessionStatus cancelled
    );

    List<MentoringSession> findByStudent_UsernameOrderByCreatedAtDesc(String username);

    List<MentoringSession> findByLecturer_IdOrderBySessionDateDesc(Long lecturerId);
}
