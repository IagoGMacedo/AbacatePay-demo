package com.macedo.abacatepay_spring_demo.client;

import com.macedo.abacatepay_spring_demo.config.FeignConfig;
import com.macedo.abacatepay_spring_demo.dto.PixQrCodeRequest;
import com.macedo.abacatepay_spring_demo.dto.PixQrCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "abacateClient", url = "${abacatepay.api.url}", configuration = FeignConfig.class)
public interface AbacateFeignClient {

    @PostMapping("/pixQrCode/create")
    PixQrCodeResponse createPixQrCode(@RequestBody PixQrCodeRequest request);
}
