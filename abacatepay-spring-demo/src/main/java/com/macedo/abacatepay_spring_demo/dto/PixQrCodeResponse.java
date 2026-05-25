package com.macedo.abacatepay_spring_demo.dto;

public record PixQrCodeResponse(
        Object error,
        PixData data
) {
    public record PixData(
            String id,
            String status,
            String brCode,
            String brCodeBase64,
            String expiresAt
    ) {}
}
