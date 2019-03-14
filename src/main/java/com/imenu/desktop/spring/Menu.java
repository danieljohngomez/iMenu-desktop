package com.imenu.desktop.spring;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private String name;
    private List<Category> categories;
    private String id;

    public Menu( String id, String name ) {
        this.id = id;
        this.name = name;
        this.categories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getId() {
        return id;
    }
}
