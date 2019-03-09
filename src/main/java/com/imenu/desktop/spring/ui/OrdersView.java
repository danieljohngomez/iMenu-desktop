package com.imenu.desktop.spring.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.Food;
import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.imenu.desktop.spring.Order;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

@Route( value = "orders", layout = MyAppLayoutRouterLayout.class )
public class OrdersView extends VerticalLayout {

    Grid<Order> grid = new Grid<>();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern( "MMMM dd, YYYY hh:mm a" );

    public OrdersView( FirebaseClient client ) {
        List<Order> orders = ImmutableList.of(
                new Order( "123456", LocalDateTime.now(), "John Doe", "1",
                        client.getTables().get( 0 ).getOrders()
                )
        );

        grid.setHeightByRows( true );
        grid.addColumn( ( ValueProvider<Order, String> ) order -> order.getTime().format( dateTimeFormatter ) )
                .setHeader( "Date and Time" );
        grid.addColumn( Order::getCustomer ).setHeader( "Customer" );
        grid.addColumn( Order::getTable ).setHeader( "Table Number" );
        grid.addComponentColumn( ( ValueProvider<Order, Component> ) order -> {
            String content = String.format( "P%.2f", order.getTotal() );
            return new Button( content, e -> openBillDialog( order ) );
        } ).setHeader( "Bill" );
        grid.addItemDoubleClickListener( e -> openBillDialog( e.getItem() ) );
        grid.setItems( orders );
        add( grid );
    }

    void openBillDialog(Order order) {
        BillDialog dialog = new BillDialog( order );
        dialog.getBillOutButton().setVisible( false );
        dialog.open();
    }

}
