package com.macedo.abacatepay_spring_demo.dto;

public record PixQrCodeRequest(
        int amount,
        String description,
        long expiresIn,
        Customer customer
) {
    public record Customer(
            String name,
            String cellphone,
            String email,
            String taxId
    ) {}
}
