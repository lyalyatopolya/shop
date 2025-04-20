package com.example.shop.web.views.person;

import com.example.shop.model.entity.Person;
import com.example.shop.repository.PersonRepository;
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
@PageTitle("Физю лица")
@Route(value = "person", layout = MainView.class)
public class PersonBrowse extends VerticalLayout implements RouterLayout {

    private final Grid<Person> personGrid = new Grid<>(Person.class, false);
    private final MenuBar menuBar;

    private final PersonRepository personRepository;

    private final PersonEditor personEditor;

    public PersonBrowse(PersonRepository personRepository, PersonEditor personEditor) {
        this.personRepository = personRepository;
        this.personEditor = personEditor;
        menuBar = createMenuBar();
        init();
        refreshPersonGrid();
    }

    private void init() {
        personEditor.setAfterSaveFunc(this::refreshPersonGrid);

        H2 title = new H2("Физические лица");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");

        personGrid.addColumn("id").setHeader("Id");
        personGrid.addColumn("name").setHeader("Имя");
        personGrid.addColumn("birthDate").setHeader("Дата рождения");

        personGrid.getColumns().forEach(col -> {
            col.setAutoWidth(true);
            col.setResizable(true);
        });
        add(title, menuBar, personGrid);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Создать", e -> {
            personEditor.open(null);
        });
        MenuItem editItem = menuBar.addItem("Изменить", e -> {
            personEditor.open(personGrid.asSingleSelect().getValue());
        });
        editItem.setEnabled(false);
        MenuItem deleteItem = menuBar.addItem("Удалить", e -> {
            personRepository.delete(personGrid.asSingleSelect().getValue());
            refreshPersonGrid();
        });
        deleteItem.setEnabled(false);
        menuBar.addItem("Обновить", e -> {
            refreshPersonGrid();
        });
        personGrid.addSelectionListener(e -> {
            editItem.setEnabled(e.getFirstSelectedItem().isPresent());
            deleteItem.setEnabled(e.getFirstSelectedItem().isPresent());
        });
        return menuBar;
    }

    private void refreshPersonGrid() {
        personGrid.setItems(personRepository.findAll());
    }
}
