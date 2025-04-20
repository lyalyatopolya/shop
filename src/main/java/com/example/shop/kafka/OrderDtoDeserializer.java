package com.example.shop.kafka;

import com.example.shop.model.dto.OrderDto;
import com.example.shop.model.enums.OrderStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class OrderDtoDeserializer implements Deserializer<OrderDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrderDto deserialize(String topic, byte[] data) {
        try {
            String json = new String(data, UTF_8);
            JsonNode rootNode = objectMapper.readTree(json);
            OrderDto orderDto = new OrderDto();
            orderDto.setId(UUID.fromString(rootNode.get("id").asText()));
            orderDto.setOrderStatus(OrderStatus.valueOf(rootNode.get("orderStatus").asText()));
            orderDto.setStatusComment(rootNode.get("statusComment").asText());
            return orderDto;
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize OrderDto", e);
        }
    }
}