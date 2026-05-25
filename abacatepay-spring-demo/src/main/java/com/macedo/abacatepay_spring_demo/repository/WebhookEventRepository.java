package com.macedo.abacatepay_spring_demo.repository;

import com.macedo.abacatepay_spring_demo.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    boolean existsByEventId(String eventId);
}
