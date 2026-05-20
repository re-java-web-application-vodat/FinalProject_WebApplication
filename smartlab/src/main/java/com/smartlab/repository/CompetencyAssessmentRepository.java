package com.smartlab.repository;

import com.smartlab.entity.CompetencyAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetencyAssessmentRepository extends JpaRepository<CompetencyAssessment, Long> {

    /** Tìm đánh giá theo buổi tư vấn (1-1) */
    Optional<CompetencyAssessment> findBySessionId(Long sessionId);

    /** Lấy tất cả đánh giá của một sinh viên, sắp xếp mới nhất trước */
    List<CompetencyAssessment> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
