package com.smartlab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionCode;
    private Double amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "session_id")
    private MentoringSession session;
}