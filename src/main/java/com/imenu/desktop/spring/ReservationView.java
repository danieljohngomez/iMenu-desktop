package com.imenu.desktop.spring;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route( value = "reservations", layout = MyAppLayoutRouterLayout.class )
public class ReservationView extends VerticalLayout {

    public ReservationView( FirebaseClient client ) {
        add( new ReservationsCalendar( client ) );
        setSizeFull();
    }
}