package com.macedo.abacatepay_spring_demo.repository;

import com.macedo.abacatepay_spring_demo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByExternalId(String externalId);
}
