package com.imenu.desktop.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Table {
    private String id;
    private String name;
    private Status status;
    private String customer;
    private List<FoodOrder> orders;
    private List<Consumer<Table>> onChangeListeners;

    public Table( String name, Status status ) {
        this.name = name;
        this.status = status;
        this.orders = new ArrayList<>();
        this.onChangeListeners = new ArrayList<>();
    }

    public Table( String name, Status status, List<FoodOrder> orders ) {
        this( name, status );
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    public List<FoodOrder> getOrders() {
        return orders;
    }

    public void setOrders( List<FoodOrder> orders ) {
        this.orders = orders;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public List<Consumer<Table>> getOnChangeListeners() {
        return onChangeListeners;
    }

    public void setOnChangeListeners( List<Consumer<Table>> onChangeListeners ) {
        this.onChangeListeners = onChangeListeners;
    }

    public void onChange() {
        this.onChangeListeners.forEach( listener -> listener.accept( this ) );
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer( String customer ) {
        this.customer = customer;
    }

    public enum Status {
        OCCUPIED,
        VACANT
    }

}
