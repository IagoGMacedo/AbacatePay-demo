package com.macedo.abacatepay_spring_demo.service;

import com.macedo.abacatepay_spring_demo.client.AbacateFeignClient;
import com.macedo.abacatepay_spring_demo.dto.CreatePixRequest;
import com.macedo.abacatepay_spring_demo.dto.CreatePixResponse;
import com.macedo.abacatepay_spring_demo.dto.PaymentStatusResponse;
import com.macedo.abacatepay_spring_demo.dto.PixQrCodeRequest;
import com.macedo.abacatepay_spring_demo.dto.PixQrCodeResponse;
import com.macedo.abacatepay_spring_demo.model.Payment;
import com.macedo.abacatepay_spring_demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AbacateFeignClient abacateFeignClient;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentStatusResponse getStatus(String externalId) {
        return paymentRepository.findByExternalId(externalId)
                .map(payment -> new PaymentStatusResponse(payment.getStatus()))
                .orElse(new PaymentStatusResponse("NOT_FOUND"));
    }

    @Override
    public CreatePixResponse createPix(CreatePixRequest request) {
        var pixRequest = new PixQrCodeRequest(
                request.amount(),
                "AbacatePay Pix",
                2000,
                new PixQrCodeRequest.Customer(
                        request.name(),
                        request.phone(),
                        request.email(),
                        request.cpf()
                )
        );

        PixQrCodeResponse response = abacateFeignClient.createPixQrCode(pixRequest);

        paymentRepository.save(Payment.builder()
                .externalId(response.data().id())
                .status(response.data().status())
                .qrCode(response.data().brCode())
                .customerName(request.name())
                .customerEmail(request.email())
                .createdAt(LocalDateTime.now())
                .build());

        return new CreatePixResponse(
                response.data().id(),
                response.data().status(),
                response.data().brCode(),
                response.data().expiresAt()
        );
    }

}