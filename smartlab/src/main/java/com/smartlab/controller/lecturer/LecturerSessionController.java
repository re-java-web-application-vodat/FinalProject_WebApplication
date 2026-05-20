package com.smartlab.controller.lecturer;

import com.smartlab.enums.SessionStatus;
import com.smartlab.service.LecturerSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecturer/sessions")
public class LecturerSessionController {

    private final LecturerSessionService lecturerSessionService;

    @GetMapping
    public String sessions(Authentication authentication, Model model) {
        model.addAttribute("sessions", lecturerSessionService.getSessions(authentication.getName()));
        return "lecturer/sessions";
    }

    @PostMapping("/{id}/complete")
    public String complete(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        return updateStatus(id, authentication, SessionStatus.COMPLETED, redirectAttributes);
    }

    @PostMapping("/{id}/cancel")
    public String cancel(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        return updateStatus(id, authentication, SessionStatus.CANCELLED, redirectAttributes);
    }

    private String updateStatus(
            Long id,
            Authentication authentication,
            SessionStatus status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            lecturerSessionService.updateStatus(id, authentication.getName(), status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lecturer/sessions";
    }
}
