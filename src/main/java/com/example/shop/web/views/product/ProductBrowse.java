package com.example.shop.web.views.product;

import com.example.shop.model.dto.ProductDto;
import com.example.shop.service.ProductRestClient;
import com.example.shop.web.views.MainView;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@ParentLayout(MainView.class)
@PageTitle("Товары")
@Route(value = "product", layout = MainView.class)
public class ProductBrowse extends VerticalLayout implements RouterLayout {

    private final ProductRestClient productRestClient;

    private final ProductEditor productEditor;

    private final Grid<ProductDto> productGrid = new Grid<>(ProductDto.class, false);

    private final MenuBar menuBar;

    public ProductBrowse(ProductRestClient productRestClient, ProductEditor productEditor) {
        this.productRestClient = productRestClient;
        this.productEditor = productEditor;
        menuBar = createMenuBar();
        init();
    }

    private void init() {
        productEditor.setAfterSaveFunc(this::refreshProductGrid);
        H2 title = new H2("Продукты");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");

        productGrid.addColumn("id").setHeader("Id");
        productGrid.addColumn("name").setHeader("Имя");
        productGrid.addColumn("comment").setHeader("Описание");
        productGrid.addColumn("count").setHeader("Кол-во");

        productGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setResizable(true);
        });
        add(title, menuBar, productGrid);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Создать", e -> {
            productEditor.open(null);
        });
        MenuItem editItem = menuBar.addItem("Изменить", e -> {
            productEditor.open(productGrid.asSingleSelect().getValue());
        });
        editItem.setEnabled(false);
        MenuItem deleteItem = menuBar.addItem("Удалить", e -> {
            productRestClient.deleteProduct(productGrid.asSingleSelect().getValue().getId().toString());
            refreshProductGrid();
        });
        deleteItem.setEnabled(false);
        menuBar.addItem("Обновить", e -> {
            refreshProductGrid();
        });
        productGrid.addSelectionListener(e -> {
            editItem.setEnabled(e.getFirstSelectedItem().isPresent());
            deleteItem.setEnabled(e.getFirstSelectedItem().isPresent());
        });
        return menuBar;
    }

    private void refreshProductGrid() {
        productGrid.setItems(productRestClient.getAllProducts());
    }
}
