package com.imenu.desktop.spring;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@Route
@PWA(name = "My Restaurant", shortName = "My Restaurant")
public class MainView extends MyAppLayoutRouterLayout {

    public MainView(@Autowired MessageBean bean) {
        //AppLayout appLayout = new AppLayout();
        //AppLayoutMenu menu = appLayout.createMenu();
        //Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        //img.setHeight("44px");
        //appLayout.setBranding(img);

        //menu.addMenuItems(new AppLayoutMenuItem("Page 1", "page1"),
        //        new AppLayoutMenuItem("Page 2", "page2"),
        //        new AppLayoutMenuItem("Page 3", "page3"),
        //        new AppLayoutMenuItem("Page 4", "page4"));
        //
        //Component content = new Span(new H3("Page title"),
        //        new Span("Page content"));
        //Component content = new PaperSlider();
        //appLayout.setContent(content);
        //add( appLayout );
        //Button button = new Button("Click me",
        //        e -> Notification.show(bean.getMessage()));
        //add(button);
        //add( new MyAppLayoutRouterLayout() );
    }

    @Tag("paper-slider")
    @HtmlImport("bower_components/paper-slider/paper-slider.html")
    public class PaperSlider extends Component {

    }
}
