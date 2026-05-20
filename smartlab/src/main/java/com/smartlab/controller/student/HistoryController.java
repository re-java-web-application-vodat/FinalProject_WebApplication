package com.smartlab.controller.student;

import com.smartlab.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public String history(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("sessions", historyService.getSessionHistory(username));
        model.addAttribute("borrows", historyService.getBorrowHistory(username));
        return "student/history";
    }
}
