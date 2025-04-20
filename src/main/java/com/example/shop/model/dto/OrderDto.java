package com.example.shop.model.dto;

import com.example.shop.model.enums.OrderStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class OrderDto {
    private UUID id;
    private List<ProductDto> productDtoList;
    private String statusComment;
    private OrderStatus orderStatus = OrderStatus.FORMING;
}
