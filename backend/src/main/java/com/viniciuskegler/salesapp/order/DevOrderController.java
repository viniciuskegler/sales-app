package com.viniciuskegler.salesapp.order;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@Profile("!prod")
public class DevOrderController {

    private final OrderStatusAdvancerService advancerService;

    public DevOrderController(OrderStatusAdvancerService advancerService) {
        this.advancerService = advancerService;
    }

    @PostMapping("/advance-statuses")
    public ResponseEntity<Void> advanceStatuses() {
        advancerService.advanceStatuses();
        return ResponseEntity.ok().build();
    }
}
