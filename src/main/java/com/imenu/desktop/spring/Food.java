package com.imenu.desktop.spring;

public class Food {
    private String image;
    private String name;
    private double price;

    public Food( String name ) {
        this.name = name;
    }

    public Food( String name, double price ) {
        this.name = name;
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
