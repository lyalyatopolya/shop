package com.example.shop.views.product;

import com.example.shop.dto.ProductDto;
import com.example.shop.model.Person;
import com.example.shop.repository.PersonRepository;
import com.example.shop.service.ProductRestService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.Objects;

@SpringComponent
@RouteScope
public class ProductEditor extends Dialog {

    private final ProductRestService productRestService;

    private final Binder<ProductDto> binder = new BeanValidationBinder<>(ProductDto.class, false);

    private final TextField nameField = new TextField("Название");
    private final IntegerField countField = new IntegerField("Кол-во");
    private final TextField commentField = new TextField("Комментарий");

    private ProductDto productDto;

    private Runnable afterSaveFunc;

    public ProductEditor(ProductRestService productRestService) {
        this.productRestService = productRestService;
        initDialog();
    }

    public void open(ProductDto productDto) {
        this.productDto = Objects.requireNonNullElseGet(productDto, ProductDto::new);
        binder.removeBean();
        binder.setBean(this.productDto);
        this.setHeaderTitle("Создать/Редактировать");
        this.setCloseOnOutsideClick(false);
        this.open();
    }

    public void setAfterSaveFunc(Runnable func) {
        afterSaveFunc = func;
    }

    private void initDialog() {
        binder.forField(nameField).bind("name");
        binder.forField(countField).bind("count");
        binder.forField(commentField).bind("comment");
        FormLayout fieldForm = new FormLayout(nameField, countField, commentField);
        this.add(fieldForm);
        Button closeButton = new Button("Закрыть", e -> {
            this.close();
            afterSaveFunc.run();
        });
        Button saveButton = new Button("Сохранить", e -> {
            if (binder.validate().isOk()) {
                productRestService.createProduct(productDto);
                this.close();
                afterSaveFunc.run();
            }
        });
        this.getFooter().add(saveButton, closeButton);
    }
}
