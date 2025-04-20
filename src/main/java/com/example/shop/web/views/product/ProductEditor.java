package com.example.shop.web.views.product;

import com.example.shop.model.dto.ProductDto;
import com.example.shop.service.ProductRestClient;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.Setter;

import java.util.Objects;

@SpringComponent
@RouteScope
public class ProductEditor extends Dialog {

    private final ProductRestClient productRestClient;

    private final Binder<ProductDto> binder = new BeanValidationBinder<>(ProductDto.class, false);

    private final TextField nameField = new TextField("Название");
    private final IntegerField countField = new IntegerField("Кол-во");
    private final TextField commentField = new TextField("Комментарий");

    private ProductDto productDto;

    @Setter
    private Runnable afterSaveFunc;

    public ProductEditor(ProductRestClient productRestClient) {
        this.productRestClient = productRestClient;
        initDialog();
    }

    public void open(ProductDto productDto) {
        this.productDto = Objects.requireNonNullElseGet(productDto, ProductDto::new);
        binder.removeBean();
        binder.setBean(this.productDto);

        this.open();
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
                String failResult;
                if (Objects.isNull(productDto.getId())) {
                    failResult = productRestClient.createProduct(productDto);
                } else {
                    failResult = productRestClient.updateProduct(productDto);
                }
                if(Objects.nonNull(failResult)){
                    Notification notification = new Notification(failResult);
                    notification.setDuration(5000);
                    notification.setPosition(Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    notification.open();
                }
                this.close();
                afterSaveFunc.run();
            }
        });
        this.getFooter().add(saveButton, closeButton);
        this.setHeaderTitle("Создать/Редактировать");
        this.setCloseOnOutsideClick(false);
    }
}
