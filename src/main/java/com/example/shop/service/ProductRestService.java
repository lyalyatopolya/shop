package com.example.shop.service;

import com.example.shop.dto.ProductDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProductRestService {

    private static final String API_URL = "http://localhost:8081/api/";
    private final RestTemplate restTemplate = new RestTemplate();


    public List<ProductDto> getAllProducts() {
        ProductDto[] products = restTemplate.getForObject(API_URL + "products", ProductDto[].class);
        return Arrays.asList(products);
    }

    public void createProduct(ProductDto productDto) {
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

    public void updateProduct(ProductDto productDto) {
        String url = API_URL + "product/update";
        try {
            ResponseEntity<ProductDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(productDto),
                    ProductDto.class
            );
            System.out.println("Product updated: " + response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }
    }

    public void deleteProduct(String productId) {
        String url = API_URL + "product/delete/" + productId;
        try {
            restTemplate.delete(url);
            System.out.println("Product deleted successfully");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getResponseBodyAsString());
        }
    }
}
