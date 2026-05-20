package com.smartlab.service;

import com.smartlab.entity.*;
import com.smartlab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Xem lịch sử học thuật & thiết bị mượn (Sinh viên).
 * Quy trình:
 *   1. Truy vấn danh sách buổi tư vấn tổng quan
 *   2. Truy vấn dữ liệu liên kết: Giảng viên, Đánh giá năng lực, Phiếu mượn thiết bị
 *   3. Tổng hợp và trả về cho Controller hiển thị
 */
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MentoringSessionRepository sessionRepository;
    private final EquipmentBorrowRepository borrowRepository;
    private final CompetencyAssessmentRepository assessmentRepository;
    private final BorrowSlipRepository borrowSlipRepository;
    private final UserRepository userRepository;

    /**lấy danh sách buổi tư vấn của sinh viên.
     * Dữ liệu đã bao gồm Giảng viên và Khoa (qua quan hệ JPA).
     */
    @Transactional(readOnly = true)
    public List<MentoringSession> getSessionHistory(String username) {
        return sessionRepository.findByStudent_UsernameOrderByCreatedAtDesc(username);
    }

    /**Lấy lịch sử mượn thiết bị đơn lẻ (do sinh viên tự tạo).
     */
    @Transactional(readOnly = true)
    public List<EquipmentBorrow> getBorrowHistory(String username) {
        return borrowRepository.findByStudent_UsernameOrderByCreatedAtDesc(username);
    }

    /** Lấy đánh giá năng lực theo buổi tư vấn.
     * Trả về Optional vì không phải buổi nào cũng đã được đánh giá.
     */
    @Transactional(readOnly = true)
    public Optional<CompetencyAssessment> getAssessmentBySessionId(Long sessionId) {
        return assessmentRepository.findBySessionId(sessionId);
    }

    /**
     * Lấy tất cả đánh giá năng lực của sinh viên.
     */
    @Transactional(readOnly = true)
    public List<CompetencyAssessment> getAssessmentsByStudent(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        return assessmentRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
    }

    /**lấy mapping sessionId → CompetencyAssessment
     * để hiển thị đánh giá cạnh mỗi buổi tư vấn trên giao diện.
     */
    @Transactional(readOnly = true)
    public Map<Long, CompetencyAssessment> getAssessmentMapByStudent(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        List<CompetencyAssessment> assessments =
                assessmentRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        return assessments.stream()
                .collect(Collectors.toMap(
                        a -> a.getSession().getId(),
                        a -> a
                ));
    }

    /**Lấy danh sách phiếu mượn thiết bị (do giảng viên tạo).
     */
    @Transactional(readOnly = true)
    public List<BorrowSlip> getBorrowSlipsByStudent(String username) {
        User student = userRepository.findByUsername(username).orElseThrow();
        return borrowSlipRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
    }
}
