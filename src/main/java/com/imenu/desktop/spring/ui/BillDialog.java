package com.imenu.desktop.spring.ui;

import org.apache.logging.log4j.util.Strings;

import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.FoodOrder;
import com.imenu.desktop.spring.Order;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.ValueProvider;

public class BillDialog extends Dialog {

    Button billOut;

    public BillDialog( Order order, FirebaseClient client ) {
        setCloseOnEsc( true );
        setCloseOnOutsideClick( true );

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing( false );
        layout.setDefaultHorizontalComponentAlignment( Alignment.START );

        H4 title = new H4( "Order" );
        layout.add( title );
        layout.setHorizontalComponentAlignment( Alignment.START, title );

        if ( Strings.isNotBlank( order.getId() ) ) {
            Label reference = new Label( "Reference: " + order.getId() );
            layout.add( reference );
            layout.setHorizontalComponentAlignment( Alignment.START, reference );
        }

        if ( Strings.isNotBlank( order.getCustomer() ) ) {
            Label customer = new Label( "Customer: " + order.getCustomer() );
            layout.add( customer );
            layout.setHorizontalComponentAlignment( Alignment.START, customer );
        }

        Grid<FoodOrder> grid = new Grid<>();
        grid.getStyle().set( "margin-top", "20px" );
        grid.setWidth( "800px" );
        grid.setHeightByRows( true );
        grid.setSelectionMode( SelectionMode.NONE );

        grid.addColumn( FoodOrder::getName ).setHeader( "Name" ).setTextAlign( ColumnTextAlign.CENTER )
                .setWidth( "300px" );
        grid.addColumn( ( ValueProvider<FoodOrder, String> ) food -> String.format( "%.2f", food.getPrice() ) )
                .setHeader( "Price" )
                .setTextAlign( ColumnTextAlign.CENTER );
        grid.addColumn( ( ValueProvider<FoodOrder, String> ) food -> "" + food.getQuantity() )
                .setHeader( "Quantity" )
                .setTextAlign( ColumnTextAlign.CENTER );
        if ( order.getTableId() != null ) {
            grid.addColumn( new ComponentRenderer<>( new SerializableFunction<FoodOrder, Component>() {
                @Override
                public Component apply( FoodOrder foodOrder ) {
                    HorizontalLayout buttons = new HorizontalLayout();
                    Button removeButton = new Button( "Remove" );
                    Button decrementButton = new Button( "-" );
                    Button incrementButton = new Button( "+" );
                    buttons.add( decrementButton, incrementButton, removeButton );
                    buttons.setSizeFull();

                    removeButton.addClickListener( ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
                        order.getFoods().removeIf( f -> f.getName().equals( foodOrder.getName() ) );
                        grid.setItems( order.getFoods() );
                        client.setTableOrder( order.getTableId(), order.getFoods() );
                    } );
                    decrementButton.addClickListener( new ComponentEventListener<ClickEvent<Button>>() {
                        @Override
                        public void onComponentEvent( ClickEvent<Button> buttonClickEvent ) {
                            if ( foodOrder.getQuantity() <= 1 )
                                removeButton.click();
                            else {
                                foodOrder.setQuantity( foodOrder.getQuantity() - 1 );
                                grid.setItems( order.getFoods() );
                                client.setTableOrder( order.getTableId(), order.getFoods() );
                            }
                        }
                    } );

                    incrementButton.addClickListener(
                            ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
                                foodOrder.setQuantity( foodOrder.getQuantity() + 1 );
                                grid.setItems( order.getFoods() );
                                client.setTableOrder( order.getTableId(), order.getFoods() );
                            } );

                    return buttons;
                }
            } ) ).setWidth( "300px" );
        }
        grid.setItems( order.getFoods() );
        layout.add( grid );

        H5 total = new H5( String.format( "Total: P%.2f", order.getTotal() ) );
        total.getStyle().set( "margin-bottom", "10px" );
        layout.add( total );

        billOut = new Button( "Bill Out" );
        billOut.setSizeFull();
        layout.add( billOut );
        if ( order.getFoods().isEmpty() )
            billOut.setEnabled( false );

        Button close = new Button( "Close", e -> this.close() );
        close.setSizeFull();
        layout.add( close );

        add( layout );
    }

    public Button getBillOutButton() {
        return billOut;
    }

    void addBillOutListener( ComponentEventListener<ClickEvent<Button>> listener ) {
        billOut.addClickListener( listener );
    }

}
