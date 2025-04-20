package com.example.shop.config.exceptionhandler;

import com.example.shop.model.dto.ErrorResponse;
import com.example.shop.model.exception.ApiClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            ErrorResponse errorResponse = objectMapper.readValue(httpResponse.getBody(), ErrorResponse.class);
            throw new ApiClientException(errorResponse);
        }
    }
}