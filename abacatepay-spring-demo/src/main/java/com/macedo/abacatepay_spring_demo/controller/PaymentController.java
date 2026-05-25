package com.macedo.abacatepay_spring_demo.controller;

import com.macedo.abacatepay_spring_demo.dto.CreatePixRequest;
import com.macedo.abacatepay_spring_demo.dto.CreatePixResponse;
import com.macedo.abacatepay_spring_demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.macedo.abacatepay_spring_demo.dto.PaymentStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pix")
    public ResponseEntity<CreatePixResponse> createPix(@RequestBody CreatePixRequest request) {
        CreatePixResponse response = paymentService.createPix(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pix/{externalId}/status")
    public ResponseEntity<PaymentStatusResponse> getStatus(@PathVariable String externalId) {
        return ResponseEntity.ok(paymentService.getStatus(externalId));
    }
}
