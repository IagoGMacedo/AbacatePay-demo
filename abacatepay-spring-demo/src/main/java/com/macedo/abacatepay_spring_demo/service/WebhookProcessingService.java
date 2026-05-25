package com.macedo.abacatepay_spring_demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macedo.abacatepay_spring_demo.dto.WebhookRequest;
import com.macedo.abacatepay_spring_demo.model.WebhookEvent;
import com.macedo.abacatepay_spring_demo.repository.PaymentRepository;
import com.macedo.abacatepay_spring_demo.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookProcessingService {

    private final WebhookEventRepository webhookEventRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(WebhookRequest request) {
        String eventId = request.data().pixQrCode().id();

        if (webhookEventRepository.existsByEventId(eventId)) {
            log.info("Duplicate webhook ignored: eventId={}", eventId);
            return;
        }

        switch (request.event()) {
            case "billing.paid"          -> handleBillingPaid(request.data());
            case "billing.failed"        -> handleBillingFailed(request.data());
            case "billing.refunded"      -> handleBillingRefunded(request.data());
            case "billing.created"       -> log.info("billing.created registered: id={}", eventId);
            case "subscription.created"  -> log.info("subscription.created registered: id={}", eventId);
            case "subscription.canceled" -> log.info("subscription.canceled registered: id={}", eventId);
            default -> log.warn("Unknown webhook event type '{}': id={}", request.event(), eventId);
        }

        webhookEventRepository.save(WebhookEvent.builder()
                .eventId(eventId)
                .eventType(request.event())
                .payload(serialize(request))
                .processedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build());
    }

    private void handleBillingPaid(WebhookRequest.BillingData data) {
        String pixId = data.pixQrCode().id();
        paymentRepository.findByExternalId(pixId).ifPresentOrElse(
                payment -> {
                    payment.updateStatus("PAID");
                    log.info("Payment marked as PAID: externalId={}", pixId);
                },
                () -> log.warn("billing.paid: payment not found for externalId={}", pixId)
        );
    }

    private void handleBillingFailed(WebhookRequest.BillingData data) {
        String pixId = data.pixQrCode().id();
        paymentRepository.findByExternalId(pixId).ifPresentOrElse(
                payment -> {
                    payment.updateStatus("FAILED");
                    log.info("Payment marked as FAILED: externalId={}", pixId);
                },
                () -> log.warn("billing.failed: payment not found for externalId={}", pixId)
        );
    }

    private void handleBillingRefunded(WebhookRequest.BillingData data) {
        String pixId = data.pixQrCode().id();
        paymentRepository.findByExternalId(pixId).ifPresentOrElse(
                payment -> {
                    payment.updateStatus("REFUNDED");
                    log.info("Payment marked as REFUNDED: externalId={}", pixId);
                },
                () -> log.warn("billing.refunded: payment not found for externalId={}", pixId)
        );
    }

    private String serialize(WebhookRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize webhook payload: {}", e.getMessage());
            return "{}";
        }
    }
}
