package com.macedo.abacatepay_spring_demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class HmacSignatureFilter extends OncePerRequestFilter {

    private static final String SIGNATURE_HEADER = "X-Webhook-Signature";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${abacatepay.hmac.key}")
    private String hmacKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String signatureHeader = request.getHeader(SIGNATURE_HEADER);

        if (signatureHeader == null || signatureHeader.isBlank()) {
            log.warn("Webhook rejected: missing {} header", SIGNATURE_HEADER);
            reject(response);
            return;
        }

        byte[] rawBody = request.getInputStream().readAllBytes();

        if (!verifySignature(rawBody, signatureHeader)) {
            log.warn("Webhook rejected: HMAC signature mismatch");
            reject(response);
            return;
        }

        log.debug("Webhook signature verified successfully");
        chain.doFilter(new CachedBodyHttpServletRequest(request, rawBody), response);
    }

    private boolean verifySignature(byte[] rawBody, String signatureFromHeader) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                    hmacKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);

            String expectedSignature = Base64.getEncoder().encodeToString(mac.doFinal(rawBody));

            byte[] a = expectedSignature.getBytes(StandardCharsets.UTF_8);
            byte[] b = signatureFromHeader.getBytes(StandardCharsets.UTF_8);

            // Timing-safe comparison — prevents timing attacks
            return MessageDigest.isEqual(a, b);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("HMAC verification failed due to configuration error: {}", e.getMessage());
            return false;
        }
    }

    private void reject(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"invalid webhook signature\"}");
    }
}
