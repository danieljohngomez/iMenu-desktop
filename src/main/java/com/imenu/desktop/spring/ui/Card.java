package com.imenu.desktop.spring.ui;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;

public class Card extends VerticalLayout implements ClickNotifier<Card> {

    public Card() {
        initStyle();
    }

    public Card( Component... components ) {
        super( components );
        initStyle();
    }

    void initStyle() {
        Style style = getStyle();
        style.set( "background", "#fff" );
        style.set( "border-radius", "2px" );
        //style.set( "display", "inline-block" );
        //style.set( "height", "300px" );
        style.set( "margin", "1rem" );
        //style.set( "position", "relative" );
        //style.set( "width", "300px" );
        style.set( "box-shadow", "0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23)" );
        style.set( "padding-left", "60px" );
        style.set( "padding-right", "60px" );
        style.set( "padding-top", "30px" );
        style.set( "padding-bottom", "30px" );
        setDefaultHorizontalComponentAlignment( Alignment.CENTER );
        setSizeUndefined();
    }
}
