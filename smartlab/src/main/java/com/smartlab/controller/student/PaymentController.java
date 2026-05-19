package com.smartlab.controller.student;

import com.smartlab.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{sessionId}")
    public String payment(@PathVariable Long sessionId){
        String paymentUrl = paymentService.createPayment(sessionId);
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String transactionCode){
        paymentService.paymentSuccess(transactionCode);
        return "redirect:/student/history";
    }
}