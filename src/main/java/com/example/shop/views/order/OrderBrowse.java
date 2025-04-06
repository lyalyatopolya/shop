package com.example.shop.views.order;

import com.example.shop.model.Order;
import com.example.shop.repository.OrderRepository;
import com.example.shop.views.MainView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import java.util.stream.Collectors;

@ParentLayout(MainView.class)
@PageTitle("Заказы")
@Route(value = "order", layout = MainView.class)
public class OrderBrowse extends VerticalLayout implements RouterLayout {

    private final Grid<Order> orderGrid = new Grid<>(Order.class, false);
    private final MenuBar menuBar;

    private final OrderRepository orderRepository;

    private final OrderEditor orderEditor;

    public OrderBrowse(OrderRepository orderRepository,
                       OrderEditor orderEditor) {
        this.orderRepository = orderRepository;
        this.orderEditor = orderEditor;
        menuBar = createMenuBar();
        init();
        refreshOrderGrid();
    }

    private void init() {
        orderEditor.setAfterSaveFunc(this::refreshOrderGrid);
        H2 title = new H2("Заказы лица");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");

        orderGrid.addColumn("id").setHeader("Id");
        orderGrid.addColumn(e->e.getOrderStatus().getName()).setHeader("Статус");
        orderGrid.addColumn("statusComment").setHeader("Коменнтарий статуса");
        orderGrid.addColumn(e->e.getPerson().getName()).setHeader("ФИО");
        orderGrid.addColumn(e->e.getOrderProducts()
                .stream()
                .map(e1->e1.getProductName()+ " кол-во: " + e1.getCount())
                .collect(Collectors.joining(", "))).setHeader("Продукты");

        orderGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setResizable(true);
        });
        add(title, menuBar, orderGrid);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Создать", e -> {
            orderEditor.openEditor();
        });

        menuBar.addItem("Обновить", e -> {
            refreshOrderGrid();
        });
        return menuBar;
    }

    private void refreshOrderGrid() {
        orderGrid.setItems(orderRepository.findAll());
    }
}
