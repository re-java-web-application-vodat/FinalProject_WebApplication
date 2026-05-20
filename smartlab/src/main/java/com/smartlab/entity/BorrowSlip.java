package com.smartlab.entity;

import com.smartlab.enums.BorrowSlipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Phiếu mượn thiết bị do giảng viên tạo sau buổi tư vấn (Flow 2).
 * Mỗi phiếu có thể chứa nhiều dòng thiết bị (BorrowSlipItem).
 * Admin sẽ duyệt và xuất kho theo phiếu này (Flow 4).
 */
@Entity
@Table(name = "borrow_slips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Trạng thái phiếu: PENDING → RELEASED → RETURNED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowSlipStatus status;

    /** Ghi chú của giảng viên */
    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDateTime createdAt;

    /** Buổi tư vấn liên kết */
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private MentoringSession session;

    /** Sinh viên mượn thiết bị */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** Giảng viên tạo phiếu */
    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    /** Danh sách chi tiết thiết bị trong phiếu */
    @OneToMany(mappedBy = "borrowSlip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowSlipItem> items = new ArrayList<>();
}
