package com.example.shop.kafka;

import com.example.shop.dto.OrderDto;
import com.example.shop.repository.OrderRepository;
import com.example.shop.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderConsumer {

    private final OrderService orderService;

    public KafkaOrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "store-house-order-requests")
    public void consumeOrder(OrderDto orderDto) {
        orderService.updateFromOrderDto(orderDto);
    }
}
