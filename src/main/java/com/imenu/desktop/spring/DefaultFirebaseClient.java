package com.imenu.desktop.spring;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.cloud.FirestoreClient;
import com.imenu.desktop.spring.Table.Status;

@Component
final class DefaultFirebaseClient implements FirebaseClient {

    Firestore client;

    public DefaultFirebaseClient() {
        if ( FirebaseApp.getApps().isEmpty() ) {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream( "firebase.json" );
            FirebaseOptions options;
            try {
                options = new Builder()
                        .setCredentials( GoogleCredentials.fromStream( serviceAccount ) )
                        .build();
            } catch ( IOException e ) {
                throw new RuntimeException( "Unable to initialize firebase", e );
            }
            FirebaseApp.initializeApp( options );
        }
        this.client = FirestoreClient.getFirestore();
    }

    @Override
    public List<Menu> getMenu() {
        return Collections.emptyList();
    }

    @Override
    public List<Category> getCategories( String menuId ) {
        return Collections.emptyList();
    }

    @Override
    public List<Food> getFoods( String categoryId ) {
        return Collections.emptyList();
    }

    @Override
    public List<Table> getTables() {
        try {
            return FirestoreClient.getFirestore().collection( "tables" ).get().get().getDocuments().stream()
                    .map( this::toTable )
                    .peek( table -> {
                        FirestoreClient.getFirestore().document( "tables/" + table.getId() )
                                .addSnapshotListener( ( documentSnapshot, e ) -> {
                                    updateTable( table, documentSnapshot );
                                    table.onChange();
                                } );
                    } )
                    .collect( Collectors.toList() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Order> getOrders() {
        try {
            return FirestoreClient.getFirestore().collection( "orders" ).get().get().getDocuments().stream()
                    .map( this::toOrder )
                    .collect( Collectors.toList() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Order addOrder( Order order ) {
        try {
            DocumentSnapshot orderSnapshot = FirestoreClient.getFirestore().collection( "orders" ).add(
                    toFirebaseModel( order ) ).get().get().get();
            return toOrder( orderSnapshot );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public void clearOrder( String tableId ) {
        try {
            FirestoreClient.getFirestore().document( "tables/" + tableId )
                    .update( "orders", new ArrayList<>(), "customer", null )
                    .get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservation> getReservations() {
        try {
            return FirestoreClient.getFirestore().collection( "reservations" ).get().get().getDocuments().stream()
                    .map( this::toReservation )
                    .collect( Collectors.toList() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Reservation upsertReservation( Reservation reservation ) {
        try {
            if (reservation.getId() == null) {
                DocumentSnapshot doc = FirestoreClient.getFirestore().collection( "reservations" )
                        .add( toFirebaseModel( reservation ) ).get().get().get();
                return toReservation( doc );
            } else {
                FirestoreClient.getFirestore().document( "reservations/" + reservation.getId() )
                        .set( toFirebaseModel( reservation ) )
                        .get();
            }
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return reservation;
    }

    @Override
    public void removeReservation( String id ) {
        try {
            FirestoreClient.getFirestore().document( "reservations/" + id ).delete().get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
    }

    Map<String, Object> toFirebaseModel(Order order) {
        Map<String, Object> object = new HashMap<>();
        object.put( "customerId", order.getCustomer() );
        object.put( "tableName", order.getTable() );
        object.put( "time", Date.from( order.getTime().atZone( ZoneId.systemDefault() ).toInstant() ) );
        List<Map<String, Object>> orders = new ArrayList<>();
        for ( FoodOrder food : order.getFoods() ) {
            Map<String, Object> foodMap = new HashMap<>();
            foodMap.put( "name", food.getName() );
            foodMap.put( "price", food.getPrice() );
            foodMap.put( "quantity", food.getQuantity() );
            orders.add( foodMap );
        }
        object.put( "orders", orders );
        return object;
    }

    Map<String, Object> toFirebaseModel(Reservation reservation) {
        Map<String, Object> object = new HashMap<>();
        object.put( "customer", reservation.getCustomer() );
        object.put( "table", reservation.getTable() );
        object.put( "start", Date.from( reservation.getStart().atZone( ZoneId.systemDefault() ).toInstant() ) );
        object.put( "end", Date.from( reservation.getEnd().atZone( ZoneId.systemDefault() ).toInstant() ) );
        return object;
    }

    Table toTable( DocumentSnapshot doc ) {
        String name = doc.getString( "name" );
        Status status = Status.valueOf( doc.getString( "status" ).toUpperCase() );
        String customer = doc.getString( "customer" );
        List<Map<String, Object>> docOrders = ( List<Map<String, Object>> ) doc.get( "orders" );
        List<FoodOrder> foodOrders = docOrders.stream().map( this::toFoodOrder ).collect( Collectors.toList());
        Table table = new Table( name, status, foodOrders );
        table.setId( doc.getId() );
        table.setCustomer( customer );
        return table;
    }

    Order toOrder( DocumentSnapshot doc ) {
        String id = doc.getId();
        LocalDateTime time = (( Date )doc.get( "time" )).toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
        String customerId = doc.getString("customerId"); // TODO cutomer name
        String tableName = doc.getString( "tableName" );
        List<Map<String, Object>> orders = ( List<Map<String, Object>> ) doc.get( "orders" );
        List<FoodOrder> foodOrders = orders.stream().map( this::toFoodOrder ).collect( Collectors.toList());
        return new Order( id, time, customerId, tableName, foodOrders );
    }

    void updateTable(Table table, DocumentSnapshot doc) {
        Table updated = toTable( doc );
        table.setId( updated.getId() );
        table.setName( updated.getName() );
        table.setOrders( updated.getOrders() );
        table.setStatus( updated.getStatus() );
        table.setCustomer( updated.getCustomer() );
    }

    FoodOrder toFoodOrder(Map<String, Object> data) {
        String orderName = "" + data.getOrDefault( "name", "" );
        double orderPrice = Double.parseDouble( "" + data.getOrDefault( "price", "0" ) );
        int orderQuantity = Integer.parseInt( "" + data.getOrDefault( "quantity", "0" ) );
        return new FoodOrder( orderName, orderPrice, orderQuantity );
    }

    Reservation toReservation( DocumentSnapshot doc ) {
        String table = doc.getString( "table" );
        String customer = doc.getString( "customer" );
        LocalDateTime start =
                ( ( Date ) doc.get( "start" ) ).toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
        LocalDateTime end =
                ( ( Date ) doc.get( "end" ) ).toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
        Reservation reservation = new Reservation( table, start, end, customer );
        reservation.setId( doc.getId() );
        return reservation;
    }

}
