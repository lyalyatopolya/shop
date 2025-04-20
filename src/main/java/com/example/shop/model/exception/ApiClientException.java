package com.example.shop.model.exception;

import com.example.shop.model.dto.ErrorResponse;
import lombok.Getter;

@Getter
public class ApiClientException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ApiClientException(ErrorResponse errorResponse) {
        super(errorResponse.message());
        this.errorResponse = errorResponse;
    }
}
