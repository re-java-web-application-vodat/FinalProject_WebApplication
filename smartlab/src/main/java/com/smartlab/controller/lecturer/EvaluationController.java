package com.smartlab.controller.lecturer;

import com.smartlab.dto.EvaluationDTO;
import com.smartlab.entity.MentoringSession;
import com.smartlab.repository.MentoringSessionRepository;
import com.smartlab.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý giao diện đánh giá sau buổi tư vấn (Flow 2).
 * Chỉ dành cho vai trò LECTURER.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/lecturer/sessions")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final MentoringSessionRepository sessionRepository;

    /**
     * GET /lecturer/sessions/{id}/evaluate
     * Hiển thị form đánh giá năng lực + chọn thiết bị mượn.
     */
    @GetMapping("/{id}/evaluate")
    public String evaluatePage(
            @PathVariable Long id,
            Model model
    ) {
        MentoringSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi tư vấn."));

        model.addAttribute("session", session);
        model.addAttribute("evaluationDTO", new EvaluationDTO());
        model.addAttribute("equipments", evaluationService.getAvailableEquipment());
        return "lecturer/evaluate";
    }

    /**
     * POST /lecturer/sessions/{id}/evaluate
     * Xử lý submit form đánh giá (Flow 2).
     * Sử dụng try/catch để bắt lỗi và hiển thị thông báo phù hợp.
     */
    @PostMapping("/{id}/evaluate")
    public String submitEvaluation(
            @PathVariable Long id,
            @ModelAttribute EvaluationDTO evaluationDTO,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            evaluationService.submitEvaluation(id, evaluationDTO, authentication.getName());
            redirectAttributes.addFlashAttribute("success",
                    "Đánh giá buổi tư vấn thành công! Phiếu mượn thiết bị đã được tạo.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/lecturer/sessions/" + id + "/evaluate";
        }
        return "redirect:/lecturer/sessions";
    }
}
