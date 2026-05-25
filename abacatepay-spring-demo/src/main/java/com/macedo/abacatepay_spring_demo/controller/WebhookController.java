package com.macedo.abacatepay_spring_demo.controller;

import com.macedo.abacatepay_spring_demo.dto.WebhookRequest;
import com.macedo.abacatepay_spring_demo.service.WebhookProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookProcessingService webhookProcessingService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receive(@RequestBody WebhookRequest request) {
        webhookProcessingService.process(request);
        return ResponseEntity.ok().build();
    }
}
