package com.macedo.abacatepay_spring_demo.dto;

public record WebhookRequest(
        String event,
        boolean devMode,
        BillingData data
) {
    public record BillingData(
            PixQrCode pixQrCode,
            Payment payment
    ) {}

    public record PixQrCode(
            String id,
            Integer amount,
            String kind,
            String status
    ) {}

    public record Payment(
            Integer amount,
            Integer fee,
            String method
    ) {}
}
