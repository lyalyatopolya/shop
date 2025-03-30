package com.example.shop.service;

import com.example.shop.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductRestService {

    private static final String API_URL = "http://localhost:8081/api/";
    private final RestTemplate restTemplate = new RestTemplate();


    public List<ProductDto> getAllProducts() {
        ProductDto[] products = restTemplate.getForObject(API_URL + "products", ProductDto[].class);
        return Arrays.asList(products);
    }

    public void createProduct( ProductDto productDto) {
        String url = API_URL + "product/create";
        try {
            ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                    url,
                    productDto,
                    ProductDto.class
            );
            System.out.println("Product created: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }
    }
}
