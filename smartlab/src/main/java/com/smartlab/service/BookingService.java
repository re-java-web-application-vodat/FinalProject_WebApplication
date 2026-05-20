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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final int MIN_DURATION_MINUTES = 30;

    /** Số giờ tối thiểu trước buổi hẹn để được phép hủy (mặc định 24h) */
    @Value("${smartlab.cancel-before-hours:24}")
    private int cancelBeforeHours;

    private final MentoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;

    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public List<Lecturer> getLecturers() {
        return lecturerRepository.findAllWithDetails();
    }

    @Transactional
    public void book(BookingDTO dto, String username) {
        validateInput(dto);

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản sinh viên."));

        Lecturer lecturer = lecturerRepository.findById(dto.getLecturerId())
                .orElseThrow(() -> new IllegalArgumentException("Giảng viên không tồn tại."));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Khoa không tồn tại."));

        if (!lecturer.getDepartment().getId().equals(department.getId())) {
            throw new IllegalArgumentException("Giảng viên không thuộc khoa đã chọn.");
        }

        LocalDate sessionDate = dto.getSessionDate();
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime = dto.getEndTime();

        validateTimeRange(sessionDate, startTime, endTime);

        if (sessionRepository.existsOverlappingLecturerSession(
                lecturer.getId(), sessionDate, startTime, endTime, SessionStatus.CANCELLED)) {
            throw new IllegalArgumentException("Giảng viên đã có lịch trùng khung giờ này.");
        }

        if (sessionRepository.existsOverlappingStudentSession(
                student.getId(), sessionDate, startTime, endTime, SessionStatus.CANCELLED)) {
            throw new IllegalArgumentException("Bạn đã có buổi mentoring trùng khung giờ này.");
        }

        MentoringSession session = MentoringSession.builder()
                .student(student)
                .lecturer(lecturer)
                .department(department)
                .sessionDate(sessionDate)
                .startTime(startTime)
                .endTime(endTime)
                .status(SessionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        sessionRepository.save(session);
    }

    private void validateInput(BookingDTO dto) {
        if (dto.getDepartmentId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn khoa.");
        }
        if (dto.getLecturerId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn giảng viên.");
        }
        if (dto.getSessionDate() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày.");
        }
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new IllegalArgumentException("Vui lòng chọn giờ bắt đầu và kết thúc.");
        }
    }

    private void validateTimeRange(LocalDate sessionDate, LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu.");
        }

        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < MIN_DURATION_MINUTES) {
            throw new IllegalArgumentException("Buổi mentoring tối thiểu " + MIN_DURATION_MINUTES + " phút.");
        }

        LocalDateTime startDateTime = LocalDateTime.of(sessionDate, startTime);
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Không thể đặt lịch trong quá khứ.");
        }

        LocalDateTime endDateTime = LocalDateTime.of(sessionDate, endTime);
        if (endDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Khung giờ kết thúc đã qua, vui lòng chọn giờ khác.");
        }
    }

    //Hủy lịch tư vấn (Sinh viên)
    /**
     * Quy trình:
     *   1. Kiểm tra buổi tư vấn thuộc về sinh viên
     *   2. Kiểm tra thời gian hủy theo quy định (mặc định >= 24h trước buổi hẹn)
     *   3. Cập nhật trạng thái → CANCELLED
     *   4. Khung giờ tự động được giải phóng (do overlap query loại trừ CANCELLED)
     *
     * @param sessionId ID buổi tư vấn cần hủy
     * @param username  Tên đăng nhập của sinh viên
     * @throws IllegalArgumentException nếu vi phạm quy định hủy
     */
    @Transactional
    public void cancelBooking(Long sessionId, String username) {

        // Bước 1: Tìm buổi tư vấn
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy buổi tư vấn."));

        // Kiểm tra quyền: buổi tư vấn phải thuộc về sinh viên hiện tại
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản sinh viên."));

        if (!session.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền hủy buổi tư vấn này.");
        }

        // Kiểm tra trạng thái: chỉ hủy buổi PENDING hoặc PENDING_PAYMENT
        if (session.getStatus() != SessionStatus.PENDING
                && session.getStatus() != SessionStatus.PENDING_PAYMENT) {
            throw new IllegalArgumentException(
                    "Chỉ có thể hủy buổi tư vấn đang ở trạng thái chờ xử lý.");
        }

        // Bước 2: Kiểm tra quy định thời gian hủy
        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilSession = Duration.between(now, sessionStart).toHours();

        if (hoursUntilSession < cancelBeforeHours) {
            throw new IllegalArgumentException(
                    "Không thể hủy lịch trong vòng " + cancelBeforeHours +
                    " giờ trước buổi hẹn. Vui lòng liên hệ giảng viên.");
        }

        // Bước 3: Cập nhật trạng thái → CANCELLED
        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);

        // Bước 4: Khung giờ tự động được giải phóng
        // (các query kiểm tra overlap đã loại trừ trạng thái CANCELLED)
    }
}
