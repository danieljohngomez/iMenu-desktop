package com.imenu.desktop.spring.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.imenu.desktop.spring.Food;
import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.imenu.desktop.spring.Order;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

@Route( value = "orders", layout = MyAppLayoutRouterLayout.class )
public class OrdersView extends VerticalLayout {

    Grid<Order> grid = new Grid<>();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern( "MMMM dd, YYYY hh:mm a" );

    public OrdersView() {
        List<Order> orders = ImmutableList.of(
                new Order( "123456", LocalDateTime.now(), "John Doe", "1",
                        ImmutableMap.of(
                                new Food( "Coffee", 100 ), 2,
                                new Food( "Milk Tea", 120 ), 3
                        )
                )
        );

        grid.setHeightByRows( true );
        grid.addColumn( ( ValueProvider<Order, String> ) order -> order.getTime().format( dateTimeFormatter ) )
                .setHeader( "Date and Time" );
        grid.addColumn( Order::getCustomer ).setHeader( "Customer" );
        grid.addColumn( Order::getTable ).setHeader( "Table Number" );
        grid.addComponentColumn( ( ValueProvider<Order, Component> ) order -> {
            String content = String.format( "P%.2f", order.getTotal() );
            return new Button( content, e -> new BillDialog( order ).open() );
        } ).setHeader( "Bill" );
        grid.addItemDoubleClickListener( e -> new BillDialog( e.getItem() ).open() );
        grid.setItems( orders );
        add( grid );
    }

    public static class BillDialog extends Dialog {

        public BillDialog( Order order ) {
            setCloseOnEsc( true );
            setCloseOnOutsideClick( true );

            VerticalLayout layout = new VerticalLayout();
            layout.setDefaultHorizontalComponentAlignment( Alignment.CENTER );

            H4 title = new H4( "Order #" + order.getId() );
            //title.getElement().getStyle().set( "font-size", "18px" );

            Grid<Food> grid = new Grid<>();
            grid.getStyle().set( "margin-top", "20px" );
            grid.setWidth( "300px" );
            grid.setHeightByRows( true );
            grid.setSelectionMode( SelectionMode.NONE );

            grid.addColumn( Food::getName ).setHeader( "Name" ).setTextAlign( ColumnTextAlign.CENTER );
            grid.addColumn( ( ValueProvider<Food, String> ) food -> String.format( "%.2f", food.getPrice() ) )
                    .setHeader( "Price" )
                    .setTextAlign( ColumnTextAlign.CENTER );
            grid.addColumn( ( ValueProvider<Food, String> ) food -> {
                Entry<Food, Integer> _food = order.getFoods().entrySet().stream().filter(
                        e -> e.getKey().getName().equals( food.getName() ) ).findFirst().orElse( null );
                return _food == null ? "" : "" + _food.getValue();
            } ).setHeader( "Quantity" ).setTextAlign( ColumnTextAlign.CENTER );
            grid.setItems( order.getFoods().keySet() );

            H5 total = new H5( String.format( "Total: P%.2f", order.getTotal() ) );

            Button close = new Button( "Close", e -> this.close() );
            close.setSizeFull();
            layout.add( title, grid, total, close );
            add( layout );
        }
    }
}
