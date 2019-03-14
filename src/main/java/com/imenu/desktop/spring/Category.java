package com.imenu.desktop.spring;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Food> items;
    private String id;

    public Category( String id, String name ) {
        this.id = id;
        this.name = name;
        this.items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Food> getItems() {
        return items;
    }

    public String getId() {
        return id;
    }
}
