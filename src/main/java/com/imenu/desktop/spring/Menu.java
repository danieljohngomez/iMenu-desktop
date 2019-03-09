package com.imenu.desktop.spring;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private String name;
    private List<Category> categories;

    public Menu( String name ) {
        this.name = name;
        this.categories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Category> getCategories() {
        return categories;
    }

}
