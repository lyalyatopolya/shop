package com.example.shop.views.order;

import com.example.shop.dto.OrderDto;
import com.example.shop.dto.ProductDto;
import com.example.shop.model.Order;
import com.example.shop.model.OrderProduct;
import com.example.shop.model.Person;
import com.example.shop.repository.OrderProductRepository;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.PersonRepository;
import com.example.shop.kafka.KafkaOrderProducerService;
import com.example.shop.service.ProductRestService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringComponent
@RouteScope
public class OrderEditor extends Dialog {

    private final PersonRepository personRepository;
    private final ProductRestService productRestService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final KafkaOrderProducerService kafkaOrderProducerService;

    private final Binder<Order> binder = new BeanValidationBinder<>(Order.class, false);
    private final ComboBox<Person> personComboBoxField = new ComboBox<>("Заказчик");
    private final ComboBox<ProductDto> productDtoField = new ComboBox<>("Добавить продукт к заказу");
    private final Grid<OrderProduct> orderProductGrid;

    private final List<OrderProduct> orderProducts = new ArrayList<>();

    private Order order;

    private Runnable afterSaveFunc;

    public OrderEditor(PersonRepository personRepository,
                       ProductRestService productRestService,
                       OrderRepository orderRepository,
                       OrderProductRepository orderProductRepository,
                       KafkaOrderProducerService kafkaOrderProducerService) {
        this.personRepository = personRepository;
        this.productRestService = productRestService;
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

    public void setAfterSaveFunc(Runnable func) {
        afterSaveFunc = func;
    }

    private Grid<OrderProduct> createOrderProductGrid() {
        Grid<OrderProduct> orderProductGrid = new Grid<>(OrderProduct.class, false);
        orderProductGrid.addColumn("id").setHeader("Id");
        orderProductGrid.addColumn(OrderProduct::getProductName).setHeader("Название продукта");
        orderProductGrid.addComponentColumn(this::generateCountField).setHeader("Кол-во продукта");
        orderProductGrid.addColumn(OrderProduct::getCountInStoreHouse).setHeader("Кол-во продуктов на складе");
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
        this.add(fieldForm, orderProductGrid);
        initProductDtoField();

        Button closeButton = new Button("Закрыть", e -> {
            this.close();
            afterSaveFunc.run();
        });
        Button saveButton = new Button("Сохранить", e -> validateSaveAndSendKafkaMessage());
        getFooter().add(saveButton, closeButton);

        setHeaderTitle("Создать");
        setCloseOnOutsideClick(false);
        setSizeFull();
    }

    private void initProductDtoField(){
        productDtoField.addValueChangeListener(e -> {
            if (Objects.isNull(e.getValue())) {
                return;
            }
            ProductDto productDto = e.getValue();;
            productDtoField.setValue(null);
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setCount(1);
            orderProduct.setProductId(productDto.getId());
            orderProduct.setProductName(productDto.getName());
            orderProduct.setCountInStoreHouse(productDto.getCount());
            if (!orderProducts.stream().map(OrderProduct::getProductId).collect(Collectors.toSet()).contains(e.getValue().getId())) {
                orderProducts.add(orderProduct);
            }
            orderProductGrid.setItems(orderProducts);
        });
    }

    private void setupData() {
        orderProductGrid.setItems(Collections.emptyList());
        personComboBoxField.setItems(personRepository.findAll());
        personComboBoxField.setItemLabelGenerator(Person::getName);

        productDtoField.setItems(productRestService.getAllProducts());
        productDtoField.setItemLabelGenerator(ProductDto::getName);
    }

    private void validateSaveAndSendKafkaMessage(){
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
            };
            orderDto.setProductDtoList(productDtoList);

            kafkaOrderProducerService.sendMessage("shop-order-requests", orderDto);
        }
    }

}
