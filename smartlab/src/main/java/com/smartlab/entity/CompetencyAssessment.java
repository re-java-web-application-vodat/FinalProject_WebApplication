package com.smartlab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Đánh giá năng lực sinh viên sau buổi tư vấn (Flow 2).
 * Quan hệ 1-1 với MentoringSession: mỗi buổi tư vấn chỉ có một đánh giá.
 */
@Entity
@Table(name = "competency_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetencyAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nội dung đánh giá chính */
    @Column(columnDefinition = "TEXT")
    private String assessmentContent;

    /** Điểm năng lực (0.0 – 10.0) */
    private Double score;

    /** Điểm mạnh của sinh viên */
    @Column(columnDefinition = "TEXT")
    private String strengths;

    /** Điểm cần cải thiện */
    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    /** Đề xuất / khuyến nghị của giảng viên */
    @Column(columnDefinition = "TEXT")
    private String recommendations;

    private LocalDateTime createdAt;

    /** Buổi tư vấn liên kết (1-1) */
    @OneToOne
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private MentoringSession session;

    /** Sinh viên được đánh giá */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** Giảng viên thực hiện đánh giá */
    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;
}
