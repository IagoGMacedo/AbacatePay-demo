package com.macedo.abacatepay_spring_demo.dto;

public record CreatePixResponse(
        String id,
        String status,
        String qrCode,
        String expiresAt
) {}
