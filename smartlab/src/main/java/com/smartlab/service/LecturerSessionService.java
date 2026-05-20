package com.smartlab.service;

import com.smartlab.entity.Lecturer;
import com.smartlab.entity.MentoringSession;
import com.smartlab.enums.SessionStatus;
import com.smartlab.repository.LecturerRepository;
import com.smartlab.repository.MentoringSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerSessionService {

    private final MentoringSessionRepository sessionRepository;
    private final LecturerRepository lecturerRepository;

    @Transactional(readOnly = true)
    public List<MentoringSession> getSessions(String username) {
        Lecturer lecturer = lecturerRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản chưa liên kết giảng viên"));
        return sessionRepository.findByLecturer_IdOrderBySessionDateDesc(lecturer.getId());
    }

    @Transactional
    public void updateStatus(Long sessionId, String username, SessionStatus newStatus) {
        Lecturer lecturer = lecturerRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản chưa liên kết giảng viên"));

        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi mentoring"));

        if (!session.getLecturer().getId().equals(lecturer.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật buổi này");
        }

        if (newStatus == SessionStatus.CANCELLED
                && session.getStatus() != SessionStatus.PENDING
                && session.getStatus() != SessionStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Chỉ có thể hủy buổi đang chờ xử lý");
        }

        if (newStatus == SessionStatus.COMPLETED && session.getStatus() != SessionStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ hoàn thành buổi đã xác nhận");
        }

        session.setStatus(newStatus);
        sessionRepository.save(session);
    }
}
