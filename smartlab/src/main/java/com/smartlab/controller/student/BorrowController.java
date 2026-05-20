package com.smartlab.controller.student;

import com.smartlab.dto.BorrowDTO;
import com.smartlab.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @GetMapping
    public String borrowPage(Model model) {
        model.addAttribute("equipments", borrowService.getAvailableEquipment());
        model.addAttribute("borrowDTO", new BorrowDTO());
        return "student/borrow";
    }

    @PostMapping
    public String borrow(
            @ModelAttribute BorrowDTO borrowDTO,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            borrowService.requestBorrow(borrowDTO, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Đã gửi yêu cầu mượn thiết bị");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/borrow";
    }
}
