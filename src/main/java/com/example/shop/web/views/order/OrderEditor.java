package com.example.shop.web.views.order;

import com.example.shop.kafka.KafkaOrderProducerService;
import com.example.shop.model.entity.Order;
import com.example.shop.model.entity.OrderProduct;
import com.example.shop.model.entity.Person;
import com.example.shop.model.dto.OrderDto;
import com.example.shop.model.dto.ProductDto;
import com.example.shop.model.dto.ProductListResult;
import com.example.shop.repository.OrderProductRepository;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.PersonRepository;
import com.example.shop.service.ProductRestClient;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringComponent
@RouteScope
public class OrderEditor extends Dialog {

    private final PersonRepository personRepository;
    private final ProductRestClient productRestClient;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final KafkaOrderProducerService kafkaOrderProducerService;

    private final Binder<Order> binder = new BeanValidationBinder<>(Order.class, false);
    private final ComboBox<Person> personComboBoxField = new ComboBox<>("Заказчик");
    private final ComboBox<ProductDto> productDtoField = new ComboBox<>("Добавить продукт к заказу");
    private final Grid<OrderProduct> orderProductGrid;
    private final List<OrderProduct> orderProductData = new ArrayList<>();

    private final List<OrderProduct> orderProducts = new ArrayList<>();

    private Order order;
    @Setter
    private Runnable afterSaveFunc;

    public OrderEditor(PersonRepository personRepository,
                       ProductRestClient productRestClient,
                       OrderRepository orderRepository,
                       OrderProductRepository orderProductRepository,
                       KafkaOrderProducerService kafkaOrderProducerService) {
        this.personRepository = personRepository;
        this.productRestClient = productRestClient;
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.kafkaOrderProducerService = kafkaOrderProducerService;
        orderProductGrid = createOrderProductGrid();
        initDialog();
    }

    public void openEditor() {
        orderProducts.clear();
        order = new Order();
        binder.removeBean();
        setupData();
        binder.setBean(order);
        open();
    }

    private Grid<OrderProduct> createOrderProductGrid() {
        Grid<OrderProduct> orderProductGrid = new Grid<>(OrderProduct.class, false);
        orderProductGrid.addColumn("id").setHeader("Id");
        orderProductGrid.addColumn(OrderProduct::getProductName).setHeader("Название продукта");
        orderProductGrid.addComponentColumn(this::generateCountField).setHeader("Кол-во продукта");
        orderProductGrid.addColumn(e -> e.getCountInStoreHouse() == 0 ? "Нет в наличии" : e.getCountInStoreHouse()).setHeader("Кол-во продуктов на складе");
        return orderProductGrid;
    }

    private IntegerField generateCountField(OrderProduct orderProduct) {
        IntegerField countField = new IntegerField();
        countField.setValue(orderProduct.getCount());
        countField.setStepButtonsVisible(true);
        countField.setMin(1);
        countField.addValueChangeListener(e -> {
            orderProduct.setCount(e.getValue());
        });
        return countField;
    }

    private void initDialog() {
        binder.forField(personComboBoxField).bind("person");
        FormLayout fieldForm = new FormLayout(personComboBoxField, productDtoField);
        add(fieldForm, orderProductGrid);
        initProductDtoField();

        Button closeButton = new Button("Закрыть", e -> {
            close();
            afterSaveFunc.run();
        });
        Button saveButton = new Button("Сохранить", e -> validateSaveAndSendKafkaMessage());
        getFooter().add(saveButton, closeButton);

        setHeaderTitle("Создать");
        setCloseOnOutsideClick(false);
        setSizeFull();
    }

    private void initProductDtoField() {
        productDtoField.addValueChangeListener(e -> {
            if (Objects.isNull(e.getValue())) {
                return;
            }
            ProductDto productDto = e.getValue();
            productDtoField.setValue(null);
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setCount(1);
            orderProduct.setProductId(productDto.getId());
            orderProduct.setProductName(productDto.getName());
            orderProduct.setCountInStoreHouse(productDto.getCount());
            if (!orderProducts.stream().map(OrderProduct::getProductId).collect(Collectors.toSet()).contains(e.getValue().getId())) {
                orderProductData.add(orderProduct);
            }
            orderProductGrid.setItems(orderProductData);
        });
    }

    private void setupData() {
        orderProductData.clear();
        orderProductGrid.setItems(orderProductData);
        personComboBoxField.setItems(personRepository.findAll());
        personComboBoxField.setItemLabelGenerator(Person::getName);

        productDtoField.setItems(productRestClient.getAllProducts());
        productDtoField.setItemLabelGenerator(ProductDto::getName);
    }

    private void validateSaveAndSendKafkaMessage() {
        if(orderProductData.isEmpty()){
            Notification notification = new Notification("Продукты для заказа отсутствуют");
            notification.setDuration(5000);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            notification.open();
            return;
        }
        if (binder.validate().isOk()) {
            Order savedOrder = orderRepository.save(order);
            List<OrderProduct> savedOrderProducts = orderProductRepository.saveAll(orderProducts);
            close();
            OrderDto orderDto = new OrderDto();
            orderDto.setId(savedOrder.getId());

            List<ProductDto> productDtoList = new ArrayList<>();
            for (OrderProduct orderProduct : savedOrderProducts) {
                ProductDto productDto = new ProductDto();
                productDto.setId(orderProduct.getProductId());
                productDto.setName(orderProduct.getProductName());
                productDto.setCount(orderProduct.getCount());
                productDtoList.add(productDto);
            }
            orderDto.setProductDtoList(productDtoList);
            kafkaOrderProducerService.sendMessage("shop-order-requests", orderDto);
        }
    }

}
