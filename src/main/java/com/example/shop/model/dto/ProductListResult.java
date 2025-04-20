package com.example.shop.model.dto;

import java.util.List;

public record ProductListResult(List<ProductDto> products, String error) {

    public boolean hasError() {
        return error != null;
    }
}
