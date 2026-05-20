package com.smartlab.controller.student;

import com.smartlab.dto.BookingDTO;
import com.smartlab.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/book")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public String bookingPage(Model model) {
        model.addAttribute("bookingDTO", new BookingDTO());
        model.addAttribute("departments", bookingService.getDepartments());
        model.addAttribute("lecturers", bookingService.getLecturers());
        model.addAttribute("minDate", LocalDate.now());
        return "student/booking";
    }

    @PostMapping
    public String book(
            @ModelAttribute BookingDTO bookingDTO,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            bookingService.book(bookingDTO, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Đặt lịch thành công! Vui lòng thanh toán tại trang Lịch sử.");
            return "redirect:/student/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/student/book";
        }
    }
}
