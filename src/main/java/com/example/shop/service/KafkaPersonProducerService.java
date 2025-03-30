package com.example.shop.service;

import com.example.shop.dto.OrderDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaPersonProducerService {

    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public KafkaPersonProducerService(KafkaTemplate<String, OrderDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, OrderDto orderDto) {
        kafkaTemplate.send(topic, orderDto);
        System.out.println("Sent message: " + orderDto.toString());
    }
}
