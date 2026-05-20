package com.smartlab.controller.student;

import com.smartlab.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller xử lý Flow 3: Xem lịch sử học thuật & thiết bị mượn (Sinh viên).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/student/history")
public class HistoryController {

    private final HistoryService historyService;

    /**
     * GET /student/history
     * Tổng hợp và hiển thị toàn bộ hồ sơ học thuật của sinh viên (Flow 3).
     *
     * Bước 2: Truy vấn danh sách buổi tư vấn tổng quan
     * Bước 3: Truy vấn dữ liệu liên kết (giảng viên, đánh giá năng lực, phiếu mượn)
     * Bước 5: Tổng hợp và hiển thị
     */
    @GetMapping
    public String history(Authentication authentication, Model model) {
        String username = authentication.getName();

        // Bước 2: Danh sách buổi tư vấn
        model.addAttribute("sessions", historyService.getSessionHistory(username));

        // Bước 3: Phiếu mượn thiết bị đơn lẻ (do sinh viên tự tạo)
        model.addAttribute("borrows", historyService.getBorrowHistory(username));

        // Bước 3: Mapping sessionId → đánh giá năng lực (để hiển thị cạnh mỗi buổi)
        model.addAttribute("assessmentMap", historyService.getAssessmentMapByStudent(username));

        // Bước 3: Phiếu mượn thiết bị (do giảng viên tạo)
        model.addAttribute("borrowSlips", historyService.getBorrowSlipsByStudent(username));

        return "student/history";
    }
}
