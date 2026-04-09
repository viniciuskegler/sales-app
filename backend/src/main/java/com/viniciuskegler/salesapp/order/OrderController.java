package com.viniciuskegler.salesapp.order;

import com.viniciuskegler.salesapp.order.dto.OrderDTO;
import com.viniciuskegler.salesapp.order.dto.PlaceOrderRequestDTO;
import com.viniciuskegler.salesapp.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO placeOrder(@RequestBody @Valid PlaceOrderRequestDTO request,
                               @AuthenticationPrincipal User currentUser) {
        return orderService.placeOrder(request, currentUser);
    }

    @GetMapping
    public List<OrderDTO> getMyOrders(@AuthenticationPrincipal User currentUser) {
        return orderService.getMyOrders(currentUser);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id,
                                 @AuthenticationPrincipal User currentUser) {
        return orderService.getOrderById(id, currentUser);
    }
}
