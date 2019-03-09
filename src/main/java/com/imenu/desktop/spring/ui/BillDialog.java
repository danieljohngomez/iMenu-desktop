package com.imenu.desktop.spring.ui;

import java.util.Map.Entry;

import org.apache.logging.log4j.util.Strings;

import com.imenu.desktop.spring.Food;
import com.imenu.desktop.spring.FoodOrder;
import com.imenu.desktop.spring.Order;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;

public class BillDialog extends Dialog {

    Button billOut;

    public BillDialog( Order order ) {
        setCloseOnEsc( true );
        setCloseOnOutsideClick( true );

        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment( Alignment.CENTER );


        H4 title = new H4( "Order" );
        if ( Strings.isNotBlank( order.getId() ) )
            title.setText( title.getText() + " #" + order.getId() );
        layout.add( title );

        if ( Strings.isNotBlank(order.getCustomer()) ) {
            Label customer = new Label( "Customer: " + order.getCustomer() );
            layout.add( customer );
        }

        Grid<FoodOrder> grid = new Grid<>();
        grid.getStyle().set( "margin-top", "20px" );
        grid.setWidth( "300px" );
        grid.setHeightByRows( true );
        grid.setSelectionMode( SelectionMode.NONE );

        grid.addColumn( FoodOrder::getName ).setHeader( "Name" ).setTextAlign( ColumnTextAlign.CENTER );
        grid.addColumn( ( ValueProvider<FoodOrder, String> ) food -> String.format( "%.2f", food.getPrice() ) )
                .setHeader( "Price" )
                .setTextAlign( ColumnTextAlign.CENTER );
        grid.addColumn( ( ValueProvider<FoodOrder, String> ) food -> "" + food.getQuantity() )
                .setHeader( "Quantity" )
                .setTextAlign( ColumnTextAlign.CENTER );
        grid.setItems( order.getFoods() );
        layout.add( grid );

        H5 total = new H5( String.format( "Total: P%.2f", order.getTotal() ) );
        layout.add( total );

        billOut = new Button( "Bill Out" );
        billOut.setSizeFull();
        layout.add( billOut );

        Button close = new Button( "Close", e -> this.close() );
        close.setSizeFull();
        layout.add( close );

        add( layout );
    }

    public Button getBillOutButton() {
        return billOut;
    }

}
