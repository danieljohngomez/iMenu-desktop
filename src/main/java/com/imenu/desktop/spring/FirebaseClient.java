package com.imenu.desktop.spring;

import java.util.List;

interface FirebaseClient {

    List<Menu> getMenu();

    List<Category> getCategories( String menuId );

    List<Food> getFoods( String categoryId );
}
