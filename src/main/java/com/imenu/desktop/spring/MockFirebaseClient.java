package com.imenu.desktop.spring;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

class MockFirebaseClient implements FirebaseClient {

    private List<Menu> menu;

    public MockFirebaseClient() {
        Menu mainCourse = new Menu( "Main Course" );
        Category meat = new Category( "Meat" );
        meat.getItems().add( new Food( "Meat1" ) );
        meat.getItems().add( new Food( "Meat2" ) );
        mainCourse.getCategories().add( meat );

        this.menu = ImmutableList.of( mainCourse );
    }

    public List<Menu> getMenu() {
        return this.menu;
    }

    public List<Category> getCategories( String menuId ) {
        return getMenu().stream()
                .flatMap( menu -> menu.getCategories().stream() )
                .collect( Collectors.toList() );
    }

    public List<Food> getFoods( String categoryId ) {
        return getCategories( "" ).stream()
                .flatMap( category -> category.getItems().stream() )
                .collect( Collectors.toList() );
    }

}
