package com.example.shop.service;

import com.example.shop.model.dto.OrderDto;
import com.example.shop.model.entity.Order;
import com.example.shop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void updateFromOrderDto(OrderDto orderDto){
        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(()->new RuntimeException(String.format("Order with id: %s not found", orderDto.getId())));
        order.setOrderStatus(orderDto.getOrderStatus());
        order.setStatusComment(orderDto.getStatusComment());
        orderRepository.save(order);
    }
}
