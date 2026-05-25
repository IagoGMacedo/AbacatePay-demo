package com.macedo.abacatepay_spring_demo.service;

import com.macedo.abacatepay_spring_demo.dto.CreatePixRequest;
import com.macedo.abacatepay_spring_demo.dto.CreatePixResponse;
import com.macedo.abacatepay_spring_demo.dto.PaymentStatusResponse;

public interface PaymentService {
    CreatePixResponse createPix(CreatePixRequest request);
    PaymentStatusResponse getStatus(String externalId);
}
