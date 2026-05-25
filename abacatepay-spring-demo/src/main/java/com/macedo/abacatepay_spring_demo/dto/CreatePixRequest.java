package com.macedo.abacatepay_spring_demo.dto;

public record CreatePixRequest(
    String name,
    String email,
    String phone,
    String cpf,
    int amount
) {

}
