package com.imenu.desktop.spring;

import java.io.InputStream;
import java.util.List;

import com.google.cloud.storage.Blob;

public interface FirebaseClient {

    List<Menu> getMenu();

    List<Category> getCategories( String menuName );

    List<Food> getFoods( String categoryName );

    String setFood( String path, Food food );

    void deleteFood( String path );

    List<Table> getTables();

    List<Order> getOrders();

    Order addOrder( Order order );

    void clearOrder( String tableId );

    List<Reservation> getReservations();

    Reservation upsertReservation( Reservation reservation );

    void removeReservation( String id );

    Blob upload( String path, String mimeType, InputStream inputStream );

    RestaurantInfo getInfo();

    void setInfo( RestaurantInfo info );

}
