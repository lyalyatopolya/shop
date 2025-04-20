package com.example.shop.service;

import com.example.shop.config.exceptionhandler.RestTemplateResponseErrorHandler;
import com.example.shop.model.dto.ErrorResponse;
import com.example.shop.model.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ProductRestClient {

    @Value("${storehouse.api.url}")
    private String storeHouseApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ProductRestClient() {
        this.restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
    }


    public List<ProductDto> getAllProducts() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-login", "some login LYALYATOPOLYA");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ProductDto[]> response = restTemplate.exchange(
                storeHouseApiUrl + "products",
                HttpMethod.GET,
                requestEntity,
                ProductDto[].class
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            return Arrays.asList(response.getBody());
        }
        return Collections.emptyList();
    }

    public String createProduct(ProductDto productDto) {
        String url = storeHouseApiUrl + "product/create";

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-login", "some login LYALYATOPOLYA");

        String failedCreateInfo = null;
        ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(productDto, headers),
                new ParameterizedTypeReference<>() {
                }
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            ErrorResponse errorResponse = new ObjectMapper().convertValue(response.getBody(), ErrorResponse.class);
            failedCreateInfo = "Произошла ошибка с api склада: "
                    + "code: " + errorResponse.code() + " message: " + errorResponse.message();
        }
        return failedCreateInfo;
    }

    public String updateProduct(ProductDto productDto) {
        String url = storeHouseApiUrl + "product/update";

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-login", "some login LYALYATOPOLYA");

        String failedCreateInfo = null;
        ResponseEntity<ProductDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(productDto, headers),
                ProductDto.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            ErrorResponse errorResponse = new ObjectMapper().convertValue(response.getBody(), ErrorResponse.class);
            failedCreateInfo = "Произошла ошибка с api склада: "
                    + "code: " + errorResponse.code() + " message: " + errorResponse.message();
        }
        return failedCreateInfo;
    }

    public void deleteProduct(String productId) {
        String url = storeHouseApiUrl + "product/delete/" + productId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("user-login", "some login LYALYATOPOLYA");

        restTemplate.delete(url, headers);
        System.out.println("Product deleted successfully");
    }
}
