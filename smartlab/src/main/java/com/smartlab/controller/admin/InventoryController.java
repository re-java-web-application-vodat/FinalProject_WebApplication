package com.smartlab.controller.admin;

import com.smartlab.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xử lý duyệt xuất kho phiếu mượn (Flow 4).
 * Chỉ dành cho vai trò ADMIN.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/borrow-slips")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * GET /admin/borrow-slips
     * Hiển thị danh sách tất cả phiếu mượn thiết bị.
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("borrowSlips", inventoryService.getAllBorrowSlips());
        return "admin/borrow-slip/list";
    }

    /**
     * POST /admin/borrow-slips/{id}/release
     * Duyệt xuất kho cho phiếu mượn (Flow 4).
     * Sử dụng try/catch để bắt lỗi thiếu tồn kho.
     */
    @PostMapping("/{id}/release")
    public String release(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inventoryService.releaseInventory(id);
            redirectAttributes.addFlashAttribute("success",
                    "Xuất kho thành công! Thiết bị đã được cấp phát.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/borrow-slips";
    }
}
