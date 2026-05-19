package com.smartlab.service;

import com.smartlab.entity.MentoringSession;
import com.smartlab.entity.PaymentTransaction;

import com.smartlab.enums.SessionStatus;

import com.smartlab.repository.MentoringSessionRepository;
import com.smartlab.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final MentoringSessionRepository sessionRepository;

    @Transactional
    public String createPayment(Long sessionId){

        MentoringSession session = sessionRepository.findById(sessionId).orElseThrow();

        String code = UUID.randomUUID().toString();

        PaymentTransaction payment = PaymentTransaction.builder()

                        .transactionCode(code)

                        .amount(100000.0)

                        .paymentMethod("VNPAY")

                        .paymentStatus("PENDING")

                        .createdAt(LocalDateTime.now())

                        .session(session)

                        .build();

        paymentRepository.save(payment);

        session.setStatus(SessionStatus.PENDING_PAYMENT);

        sessionRepository.save(session);

        // sandbox url
        return "https://sandbox.vnpayment.vn/payment/" + code;
    }

    @Transactional
    public void paymentSuccess(String transactionCode){

        PaymentTransaction payment = paymentRepository.findByTransactionCode(transactionCode);

        payment.setPaymentStatus("SUCCESS");

        MentoringSession session = payment.getSession();

        session.setStatus(SessionStatus.CONFIRMED);

        paymentRepository.save(payment);

        sessionRepository.save(session);
    }
}
