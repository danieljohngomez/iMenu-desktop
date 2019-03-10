package com.imenu.desktop.spring;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.imenu.desktop.spring.Table.Status;

//@Component
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

    @Override
    public List<Table> getTables() {
        Table table1 = new Table( "16", Status.OCCUPIED, ImmutableList.of(
            new FoodOrder( "Coffee", 100, 5 ),
            new FoodOrder( "Milk Tea", 120, 3 )
        ) );

        Table table2 = new Table( "17", Status.VACANT, new ArrayList<>() );
        return ImmutableList.of( table1, table2 );
    }

    @Override
    public List<Order> getOrders() {
        return ImmutableList.of(
                new Order( "123456", LocalDateTime.now(), "John Doe", "1",
                        getTables().get( 0 ).getOrders()
                )
        );
    }

    @Override
    public Order addOrder( Order order ) {
        return order;
    }

    @Override
    public void clearOrder( String tableId ) {
    }

    @Override
    public List<Reservation> getReservations() {
        return Collections.emptyList();
    }

    @Override
    public Reservation upsertReservation( Reservation reservation ) {
        return reservation;
    }

    @Override
    public void removeReservation( String id ) {

    }

}
