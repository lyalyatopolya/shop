package com.example.shop;

import com.example.shop.dto.OrderDto;
import com.example.shop.service.KafkaPersonProducerService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView(KafkaPersonProducerService kafkaPersonProducerService) {

        TextField textField = new TextField("Your name");

        Button create = new Button("Отправить в кафку", e -> {
            OrderDto orderDto = new OrderDto();
            orderDto.setPersonId("123");
            kafkaPersonProducerService.sendMessage("order-request", orderDto);
        });

        addClassName("centered-content");

        add(textField, create);
    }
}
