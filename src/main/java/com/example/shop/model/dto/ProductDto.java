package com.example.shop.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDto {
    private UUID id;
    private String name;
    private int count = 0;
    private String comment;
}
