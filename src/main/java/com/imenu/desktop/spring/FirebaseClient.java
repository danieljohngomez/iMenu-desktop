package com.imenu.desktop.spring;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import com.google.cloud.storage.Blob;

public interface FirebaseClient {

    List<Menu> getMenu();

    List<Category> getCategories( String menuName );

    List<Food> getFoods( String categoryName );

    String setFood( String path, Food food );

    void deleteFood( String path );

    void deleteCategory( String path );

    Category addCategory( String menuId, Category category );

    Table addTable( String name );

    void deleteTable( String id );

    List<Table> getTables();

    void setTableOrder( String tableId, List<FoodOrder> foodOrders );

    List<Order> getOrders();

    Order addOrder( Order order );

    void clearOrder( String tableId );

    List<Reservation> getReservations();

    Reservation upsertReservation( Reservation reservation );

    void removeReservation( String id );

    Blob upload( String path, String mimeType, InputStream inputStream );

    RestaurantInfo getInfo();

    void setInfo( RestaurantInfo info );

    List<Notification> getNotifications();

    void onNotification( Consumer<Notification> callback );

    void setNotification( Notification notification );
}
