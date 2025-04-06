package com.example.shop.config;

import com.example.shop.dto.OrderDto;
import com.example.shop.kafka.OrderDtoDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, OrderDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, OrderDto> kafkaPersonTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, OrderDto> orderDtoConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "shop-group");

        ErrorHandlingDeserializer<OrderDto> valueDeserializer =
                new ErrorHandlingDeserializer<>(new OrderDtoDeserializer());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderDtoConsumerFactory());

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> {
                    System.err.printf("""
                                    Skipping message due to error:
                                    Topic: %s
                                    Partition: %d
                                    Offset: %d
                                    Error: %s%n""",
                            record.topic(), record.partition(),
                            record.offset(), exception.getMessage()
                    );
                },
                new FixedBackOff(0L, 0L)
        );

        errorHandler.addNotRetryableExceptions(
                SerializationException.class,
                RecordDeserializationException.class
        );

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
