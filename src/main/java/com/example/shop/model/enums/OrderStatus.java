package com.example.shop.model.enums;

public enum OrderStatus {
    FORMING("Формируется"),
    CANCELED("Отменён"),
    FORMED("Оформлен");

    private final String name;

    OrderStatus(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
