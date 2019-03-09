package com.imenu.desktop.spring;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Order {

    private String id;

    private LocalDateTime time;

    private String customer;

    private String table;

    private Map<Food, Integer> foods;

    public Order( String id ) {
        this.id = id;
        this.foods = new HashMap<>();
    }

    public Order( String id, LocalDateTime time, String customer, String table, Map<Food, Integer> foods ) {
        this( id );
        this.time = time;
        this.customer = customer;
        this.table = table;
        this.foods = foods;
    }

    public String getTable() {
        return table;
    }

    public void setTable( String table ) {
        this.table = table;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer( String customer ) {
        this.customer = customer;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime( LocalDateTime time ) {
        this.time = time;
    }

    public double getTotal() {
        double sum = 0;
        for ( Entry<Food, Integer> entry : foods.entrySet() ) {
            double foodTotal = entry.getKey().getPrice() * entry.getValue();
            sum += foodTotal;
        }
        return sum;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public Map<Food, Integer> getFoods() {
        return foods;
    }

}
