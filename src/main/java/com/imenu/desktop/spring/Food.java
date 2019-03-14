package com.imenu.desktop.spring;

public class Food {
    private String image;
    private String name;
    private double price;
    private String id;

    public Food( String id, String name ) {
        this.id = id;
        this.name = name;
    }

    public Food( String id, String name, double price ) {
        this(id, name);
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

    public void setImage( String image ) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setPrice( double price ) {
        this.price = price;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setId( String id ) {
        this.id = id;
    }
}
