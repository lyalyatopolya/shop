package com.example.shop.views.person;

import com.example.shop.model.Person;
import com.example.shop.repository.PersonRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.util.Objects;

@SpringComponent
@RouteScope
public class PersonEditor extends Dialog {

    private final PersonRepository personRepository;

    private final Binder<Person> binder = new BeanValidationBinder<>(Person.class, false);

    private final TextField nameField = new TextField("Имя");
    private final DatePicker birthDateField = new DatePicker("Дата рождения");

    private Person person;

    private Runnable afterSaveFunc;

    public PersonEditor(PersonRepository personRepository) {
        this.personRepository = personRepository;
        initDialog();
    }

    public void open(Person person) {
        this.person = Objects.requireNonNullElseGet(person, Person::new);
        binder.removeBean();
        binder.setBean(this.person);
        this.open();
    }

    public void setAfterSaveFunc(Runnable func) {
        afterSaveFunc = func;
    }

    private void initDialog() {
        binder.forField(nameField).bind("name");
        binder.forField(birthDateField).bind("birthDate");
        FormLayout fieldForm = new FormLayout(nameField, birthDateField);
        this.add(fieldForm);
        Button closeButton = new Button("Закрыть", e -> {
            this.close();
            afterSaveFunc.run();
        });
        Button saveButton = new Button("Сохранить", e -> {
            if (binder.validate().isOk()) {
                personRepository.save(person);
                this.close();
                afterSaveFunc.run();
            }
        });
        getFooter().add(saveButton, closeButton);
        setHeaderTitle("Создать/Редактировать");
        setCloseOnOutsideClick(false);
        setSizeFull();
    }
}
