package com.imenu.desktop.spring.ui;

import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route( value = "tables", layout = MyAppLayoutRouterLayout.class )
public class TablesView extends VerticalLayout {

    public TablesView() {

    }
}
