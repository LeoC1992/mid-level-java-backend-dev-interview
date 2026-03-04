package com.retail.order.service;

import com.retail.order.dto.CreateOrderRequest;
import com.retail.order.dto.OrderResponse;
import com.retail.order.entity.Order;
import com.retail.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Processing creation for order: {}", request.getOrderNumber());
        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setCustomerName(request.getCustomerName());
        order.setAmount(request.getAmount());
        order.setStatus(request.getStatus());
        
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).get();
        return mapToResponse(order);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
