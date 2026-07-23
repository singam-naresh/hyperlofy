package com.hyperlofy.backend.ai.merchantselection;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/merchant-selection")
@RequiredArgsConstructor
public class MerchantSelectionController {

    private final MerchantSelectionService merchantSelectionService;

    @PostMapping("/plan")
    public ResponseEntity<MerchantSelectionResponse> selectMerchants(@Valid @RequestBody MerchantSelectionRequest request) {
        return ResponseEntity.ok(merchantSelectionService.select(request));
    }
}
