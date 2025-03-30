package com.example.shop.views.order;

import com.example.shop.views.MainView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@ParentLayout(MainView.class)
@PageTitle("Заказы")
@Route(value = "order", layout = MainView.class)
public class OrderBrowse extends VerticalLayout implements RouterLayout {

    public OrderBrowse() {

    }

    private void init() {

    }
}
