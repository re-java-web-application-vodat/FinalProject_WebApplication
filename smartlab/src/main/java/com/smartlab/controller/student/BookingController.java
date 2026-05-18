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

@Controller
@RequiredArgsConstructor

@RequestMapping("/student/book")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public String bookingPage(Model model){
        model.addAttribute("bookingDTO", new BookingDTO());

        return "student/booking";
    }

    @PostMapping
    public String book(@ModelAttribute BookingDTO bookingDTO, Authentication authentication){

        bookingService.book(bookingDTO, authentication.getName());

        return "redirect:/student/book";
    }
}
