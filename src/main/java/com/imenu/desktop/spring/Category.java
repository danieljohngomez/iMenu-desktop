package com.imenu.desktop.spring;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Food> items;

    public Category( String name ) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Food> getItems() {
        return items;
    }
}
