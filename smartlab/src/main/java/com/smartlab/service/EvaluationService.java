package com.smartlab.service;

import com.smartlab.dto.BorrowSlipItemDTO;
import com.smartlab.dto.EvaluationDTO;
import com.smartlab.entity.*;
import com.smartlab.enums.BorrowSlipStatus;
import com.smartlab.enums.SessionStatus;
import com.smartlab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nhập đánh giá sau buổi tư vấn (Giảng viên).
 *
 * Sử dụng @Transactional để đảm bảo ACID:
 *   1. Cập nhật trạng thái buổi tư vấn → COMPLETED
 *   2. Lưu đánh giá năng lực (CompetencyAssessment)
 *   3. Lưu phiếu mượn thiết bị (BorrowSlip + BorrowSlipItem)
 *
 * Nếu bất kỳ bước nào lỗi → Spring tự động ROLLBACK toàn bộ.
 * Nếu tất cả thành công → Spring tự động COMMIT.
 */
@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final MentoringSessionRepository sessionRepository;
    private final LecturerRepository lecturerRepository;
    private final CompetencyAssessmentRepository assessmentRepository;
    private final BorrowSlipRepository borrowSlipRepository;
    private final EquipmentRepository equipmentRepository;

    /**
     * Lấy danh sách thiết bị khả dụng để giảng viên chọn cho sinh viên mượn.
     */
    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findAll().stream()
                .filter(e -> e.getAvailableQuantity() != null && e.getAvailableQuantity() > 0)
                .toList();
    }

    /**
     * Xử lý đánh giá sau buổi tư vấn.
     * Toàn bộ logic nằm trong một transaction duy nhất.
     *
     * @param sessionId ID buổi tư vấn
     * @param dto       Dữ liệu đánh giá từ giảng viên
     * @param username  Tên đăng nhập của giảng viên
     * @throws RuntimeException nếu có lỗi → tự động rollback
     */
    @Transactional
    public void submitEvaluation(Long sessionId, EvaluationDTO dto, String username) {

        // Xác thực giảng viên
        Lecturer lecturer = lecturerRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản chưa liên kết giảng viên."));

        // Xác thực buổi tư vấn
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi tư vấn."));

        // Kiểm tra quyền: chỉ giảng viên phụ trách mới được đánh giá
        if (!session.getLecturer().getId().equals(lecturer.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh giá buổi tư vấn này.");
        }

        // Kiểm tra trạng thái: chỉ đánh giá buổi đã xác nhận (CONFIRMED)
        if (session.getStatus() != SessionStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ có thể đánh giá buổi tư vấn đã được xác nhận.");
        }

        // Kiểm tra đánh giá đã tồn tại chưa
        if (assessmentRepository.findBySessionId(sessionId).isPresent()) {
            throw new RuntimeException("Buổi tư vấn này đã được đánh giá trước đó.");
        }

        // TRANSACTION: 3 thao tác ghi liên tiếp
        // Cập nhật trạng thái buổi tư vấn → COMPLETED
        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        // Lưu đánh giá năng lực
        CompetencyAssessment assessment = CompetencyAssessment.builder()
                .session(session)
                .student(session.getStudent())
                .lecturer(lecturer)
                .assessmentContent(dto.getAssessmentContent())
                .score(dto.getScore())
                .strengths(dto.getStrengths())
                .weaknesses(dto.getWeaknesses())
                .recommendations(dto.getRecommendations())
                .createdAt(LocalDateTime.now())
                .build();
        assessmentRepository.save(assessment);

        //Lưu phiếu mượn thiết bị (nếu có)
        if (dto.getBorrowItems() != null && !dto.getBorrowItems().isEmpty()) {

            // Tạo phiếu mượn
            BorrowSlip slip = BorrowSlip.builder()
                    .session(session)
                    .student(session.getStudent())
                    .lecturer(lecturer)
                    .status(BorrowSlipStatus.PENDING)
                    .note(dto.getNote())
                    .createdAt(LocalDateTime.now())
                    .build();

            // Thêm từng dòng thiết bị vào phiếu
            for (BorrowSlipItemDTO itemDTO : dto.getBorrowItems()) {
                // Bỏ qua dòng trống hoặc số lượng <= 0
                if (itemDTO.getEquipmentId() == null || itemDTO.getQuantity() == null
                        || itemDTO.getQuantity() <= 0) {
                    continue;
                }

                Equipment equipment = equipmentRepository.findById(itemDTO.getEquipmentId())
                        .orElseThrow(() -> new RuntimeException(
                                "Không tìm thấy thiết bị ID: " + itemDTO.getEquipmentId()));

                BorrowSlipItem item = BorrowSlipItem.builder()
                        .borrowSlip(slip)
                        .equipment(equipment)
                        .quantity(itemDTO.getQuantity())
                        .build();
                slip.getItems().add(item);
            }

            // Lưu phiếu mượn cùng các dòng thiết bị (CascadeType.ALL)
            if (!slip.getItems().isEmpty()) {
                borrowSlipRepository.save(slip);
            }
        }

        // Nếu code chạy đến đây mà không có exception → Spring tự COMMIT
        // Nếu bất kỳ dòng nào throw exception → Spring tự ROLLBACK toàn bộ
    }
}
