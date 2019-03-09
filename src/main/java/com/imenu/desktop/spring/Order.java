package com.imenu.desktop.spring;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private String id;

    private LocalDateTime time;

    private String customer;

    private String table;

    private List<FoodOrder> foods;

    public Order( String id ) {
        this.id = id;
        this.foods = new ArrayList<>();
    }

    public Order( String id, LocalDateTime time, String customer, String table, List<FoodOrder> foods ) {
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
        for ( FoodOrder food : foods ) {
            double foodTotal = food.getPrice() * food.getQuantity();
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

    public List<FoodOrder> getFoods() {
        return foods;
    }

    public void setFoods( List<FoodOrder> foods ) {
        this.foods = foods;
    }
}
