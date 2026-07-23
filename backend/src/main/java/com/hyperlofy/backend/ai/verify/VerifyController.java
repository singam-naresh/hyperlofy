package com.hyperlofy.backend.ai.verify;

import com.hyperlofy.backend.ai.verify.dto.VerifyRequest;
import com.hyperlofy.backend.ai.verify.dto.VerifyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerifyController {

    private final VerifyService verifyService;

    @PostMapping
    public ResponseEntity<VerifyResponse> submitVerification(@Valid @RequestBody VerifyRequest request) {
        return ResponseEntity.ok(verifyService.submitVerification(request));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<VerifyResponse>> getOrderVerifications(@PathVariable UUID orderId) {
        return ResponseEntity.ok(verifyService.getVerificationsForOrder(orderId));
    }
}
