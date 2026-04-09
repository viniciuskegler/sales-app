package com.viniciuskegler.salesapp.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
public class InternalOrderController {

    @Value("${INTERNAL_API_SECRET}")
    private String internalApiSecret;

    private final OrderStatusAdvancerService advancerService;

    public InternalOrderController(OrderStatusAdvancerService advancerService) {
        this.advancerService = advancerService;
    }

    @PostMapping("/advance-statuses")
    public ResponseEntity<Void> advanceStatuses(
            @RequestHeader("X-Internal-Secret") String secret) {
        if (!internalApiSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        advancerService.advanceStatuses();
        return ResponseEntity.ok().build();
    }
}
