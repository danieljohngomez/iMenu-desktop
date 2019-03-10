package com.imenu.desktop.spring;

import java.util.List;

public interface FirebaseClient {

    List<Menu> getMenu();

    List<Category> getCategories( String menuId );

    List<Food> getFoods( String categoryId );

    List<Table> getTables();

    List<Order> getOrders();

    Order addOrder( Order order );

    void clearOrder( String tableId );

    List<Reservation> getReservations();

    Reservation upsertReservation( Reservation reservation );

    void removeReservation( String id );
}
