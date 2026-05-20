package com.smartlab.controller.admin;

import com.smartlab.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/borrows")
public class BorrowAdminController {

    private final BorrowService borrowService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("borrows", borrowService.getAllBorrows());
        return "admin/borrow/list";
    }

    @PostMapping("/{id}/export")
    public String export(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handle(id, redirectAttributes, true);
    }

    @PostMapping("/{id}/return")
    public String returnItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handle(id, redirectAttributes, false);
    }

    private String handle(Long id, RedirectAttributes redirectAttributes, boolean export) {
        try {
            if (export) {
                borrowService.exportBorrow(id);
            } else {
                borrowService.returnBorrow(id);
            }
            redirectAttributes.addFlashAttribute("success", "Cập nhật phiếu mượn thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrows";
    }
}
