package com.example.shop.views;

import com.example.shop.views.order.OrderBrowse;
import com.example.shop.views.person.PersonBrowse;
import com.example.shop.views.product.ProductBrowse;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends AppLayout {

    public MainView() {
        SideNav nav = createSideNav();
        addToNavbar(nav);
    }

    private SideNav createSideNav() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Физ. лица",
                PersonBrowse.class));
        nav.addItem(new SideNavItem("Заказы",
                OrderBrowse.class));
        nav.addItem(new SideNavItem("Товары",
                ProductBrowse.class));
        return nav;
    }
}
