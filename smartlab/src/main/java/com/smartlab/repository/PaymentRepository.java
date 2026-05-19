package com.smartlab.repository;

import com.smartlab.entity.PaymentTransaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, Long> {

    PaymentTransaction findByTransactionCode(String code);
}
