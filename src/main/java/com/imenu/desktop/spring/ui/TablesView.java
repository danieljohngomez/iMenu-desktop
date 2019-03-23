package com.imenu.desktop.spring.ui;

import static org.apache.commons.lang3.StringUtils.capitalize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.util.Strings;

import com.imenu.desktop.spring.Category;
import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.imenu.desktop.spring.Order;
import com.imenu.desktop.spring.Table;
import com.imenu.desktop.spring.Table.Status;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;

@Route( value = "tables", layout = MyAppLayoutRouterLayout.class )
public class TablesView extends HorizontalLayout {

    private FirebaseClient client;

    public TablesView( FirebaseClient client ) {
        this.client = client;
        setSizeFull();
        setSpacing( false );
        getStyle().set( "display", "flex" );
        getStyle().set( "flex-wrap", "wrap" );

        Div progressLayout = new Div();
        progressLayout.getStyle().set( "display", "flex" );
        progressLayout.getStyle().set( "align-items", "center" );
        progressLayout.getStyle().set( "justify-content", "center" );
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate( true );
        progressBar.setWidth("50%");

        progressLayout.add( progressBar );
        progressLayout.setSizeFull();

        add( progressLayout );
        List<Table> tables = client.getTables();
        removeAll();

        TextField addTableNumberField = new TextField( null, "Table Number" );
        Button addTable = new Button( "Add Table" );
        addTable.addClickListener( ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
            addTableNumberField.setInvalid( false );
            String tableNumber = addTableNumberField.getValue();
            if ( Strings.isBlank(tableNumber)) {
                addTableNumberField.setErrorMessage( "Required" );
                addTableNumberField.setInvalid( true );
            } else {
                Table table = client.addTable( tableNumber );
                add( createTableUi( table ) );
                addTableNumberField.setValue( "" );
            }
        } );

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth( "30%" );
        HorizontalLayout layout = new HorizontalLayout( verticalLayout, addTableNumberField, addTable );
        layout.setWidth( "100%" );
        layout.getStyle().set( "padding-top", "20px" );
        add( layout );

        setSizeUndefined();
        for ( Table table : tables ) {
            add( createTableUi( table ) );
        }
    }

    Component createTableUi(Table table) {
        Label title = new Label( "Table" );
        H1 tableNumber = new H1( table.getName() );
        tableNumber.getStyle().set( "margin", "0" );

        H4 status = new H4( capitalize( table.getStatus().name() ) );
        status.getStyle().set( "color", table.getStatus() == Status.OCCUPIED ? "red" : "green" );
        Button viewOrder = new Button( "View Order" );
        viewOrder.addClickListener( e -> {
            Order order = new Order( null );
            order.setTable( table.getName() );
            order.setFoods( table.getOrders() );
            order.setCustomer( table.getCustomer() );
            BillDialog billDialog = new BillDialog( order );
            billDialog.addBillOutListener( new ComponentEventListener<ClickEvent<Button>>() {
                @Override
                public void onComponentEvent( ClickEvent<Button> buttonClickEvent ) {
                    order.setTime( LocalDateTime.now() );
                    client.addOrder( order );
                    client.clearOrder( table.getId() );
                    Notification.show( "Successfully Billed Out", 3000, Position.MIDDLE );
                    billDialog.close();
                }
            } );
            billDialog.open();
        } );



        table.getOnChangeListeners().add( new Consumer<Table>() {
            @Override
            public void accept( Table table ) {
                getUI().get().access( new Command() {
                    @Override
                    public void execute() {
                        status.setText( capitalize( table.getStatus().name() ) );
                        status.getStyle().set( "color", table.getStatus() == Status.OCCUPIED ? "red" : "green" );
                    }
                } );
            }
        } );
        Button deleteTable = new Button( "Delete Table" );
        Card card = new Card( title, tableNumber, status, viewOrder, deleteTable );
        deleteTable.addClickListener( e -> {
            client.deleteTable( table.getId() );
            remove( card );
        } );
        return card;
    }

}
