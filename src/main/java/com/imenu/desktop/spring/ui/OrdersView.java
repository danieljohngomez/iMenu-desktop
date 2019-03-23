package com.imenu.desktop.spring.ui;

import java.time.format.DateTimeFormatter;

import com.imenu.desktop.spring.FirebaseClient;
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

    private final FirebaseClient client;

    Grid<Order> grid = new Grid<>();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern( "MMMM dd, YYYY hh:mm a" );

    public OrdersView( FirebaseClient client ) {
        this.client = client;
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
        grid.setItems( client.getOrders() );
        add( grid );
    }

    void openBillDialog(Order order) {
        BillDialog dialog = new BillDialog( order, client );
        dialog.getBillOutButton().setVisible( false );
        dialog.open();
    }

}
