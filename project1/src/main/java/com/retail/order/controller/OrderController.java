package com.retail.order.controller;

import com.retail.order.dto.CreateOrderRequest;
import com.retail.order.dto.OrderResponse;
import com.retail.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderNumber}")
    public OrderResponse getOrder(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    @GetMapping
    public Page<OrderResponse> getOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }
}
