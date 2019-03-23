package com.imenu.desktop.spring;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket.BlobWriteOption;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.imenu.desktop.spring.Notification.Type;
import com.imenu.desktop.spring.Table.Status;

@Component
final class DefaultFirebaseClient implements FirebaseClient {

    Firestore client;

    List<Menu> menuList;

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
        List<Menu> menuList = new ArrayList<>();
        try {
            for ( QueryDocumentSnapshot menuDoc : FirestoreClient.getFirestore().collection(
                    "menu" ).get().get().getDocuments() ) {
                Menu menu = new Menu( menuDoc.getId(), menuDoc.getString( "name" ) );
                for ( QueryDocumentSnapshot categoryDoc : menuDoc.getReference().collection(
                        "categories" ).get().get().getDocuments() ) {
                    Category category = new Category( categoryDoc.getId(), categoryDoc.getString( "name" ) );
                    for ( QueryDocumentSnapshot item : categoryDoc.getReference().collection(
                            "items" ).get().get().getDocuments() ) {
                        Double price = item.getDouble( "price" );
                        Food food = new Food( item.getId(), item.getString( "name" ), price != null ? price : 0 );
                        food.setImage( item.getString( "image" ) );
                        category.getItems().add( food );
                    }
                    menu.getCategories().add( category );
                }
                menuList.add( menu );
            }
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        this.menuList = menuList;
        Collections.sort(this.menuList, Comparator.comparing(Menu::getName));
        return this.menuList;
    }

    @Override
    public List<Category> getCategories( String menuName ) {
        if ( this.menuList == null )
            getMenu();
        return this.menuList.stream()
                .filter( m -> m.getName().equals( menuName ) )
                .flatMap( m -> m.getCategories().stream() )
                .collect( Collectors.toList() );
    }

    @Override
    public List<Food> getFoods( String categoryName ) {
        return this.menuList.stream()
                .flatMap( m -> m.getCategories().stream() )
                .filter( c -> c.getName().equals( categoryName ) )
                .flatMap( c -> c.getItems().stream() )
                .collect( Collectors.toList() );
    }

    @Override
    public String setFood( String path, Food food ) {
        try {
            if ( Strings.isBlank( food.getId() ) ) {
                return FirestoreClient.getFirestore().collection( path )
                        .add( toFirebaseModel( food ) ).get().get().get().getId();
            } else {
                FirestoreClient.getFirestore().document( path )
                        .set( toFirebaseModel( food ) )
                        .get();
                return food.getId();
            }
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteFood( String path ) {
        try {
            FirestoreClient.getFirestore().document( path )
                    .delete().get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCategory( String path ) {
        try {
            FirestoreClient.getFirestore().document( path ).delete().get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public Category addCategory( String menuId, Category category ) {
        Map<String, Object> model = new HashMap<>();
        model.put( "name", category.getName() );
        try {
            DocumentReference documentReference = FirestoreClient.getFirestore().collection(
                    "menu/" + menuId + "/categories" )
                    .add( model ).get();
            return new Category( documentReference.getId(), category.getName() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public Table addTable( String name ) {
        Table table = new Table( name, Status.VACANT );
        try {
            DocumentSnapshot documentReference = FirestoreClient.getFirestore().collection( "tables" )
                    .add( toFirebaseModel( table ) ).get().get().get();
            return toTable( documentReference );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return table;
    }

    @Override
    public void deleteTable( String id ) {
        try {
            FirestoreClient.getFirestore().document( "tables/" + id ).delete().get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
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
    public void setTableOrder( String tableId, List<FoodOrder> foodOrders ) {
        List<Map<String, Object>> foods = foodOrders.stream().map( this::toFirebaseModel ).collect(
                Collectors.toList() );
        try {
            FirestoreClient.getFirestore().document( "tables/" + tableId )
                    .set( ImmutableMap.of( "orders", foods ), SetOptions.merge() )
                    .get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
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
            if ( reservation.getId() == null ) {
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

    @Override
    public Blob upload( String path, String mimeType, InputStream inputStream ) {
        return StorageClient.getInstance().bucket( "imenu-59599.appspot.com" )
                .create( path, inputStream, mimeType,
                        BlobWriteOption.predefinedAcl( PredefinedAcl.PUBLIC_READ ) );
    }

    @Override
    public RestaurantInfo getInfo() {
        try {
            DocumentSnapshot doc = FirestoreClient.getFirestore().document(
                    "restaurant/info" ).get().get();
            return toRestaurantInfo( doc );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setInfo(RestaurantInfo info) {
        try {
            FirestoreClient.getFirestore().document(
                    "restaurant/info" ).set(toFirebaseModel( info )).get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> getNotifications() {
        try {
            return FirestoreClient.getFirestore().collection( "notifications" ).get().get().getDocuments().stream()
                    .map( this::toNotification )
                    .collect( Collectors.toList() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void onNotification( Consumer<Notification> callback ) {
        FirestoreClient.getFirestore().collection( "notifications" ).addSnapshotListener(
                ( queryDocumentSnapshots, e ) -> {
                    if ( queryDocumentSnapshots == null )
                        return;
                    for ( DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges() ) {
                        callback.accept( toNotification( documentChange.getDocument() ) );
                    }
                } );
    }

    @Override
    public void setNotification( Notification notification ) {
        try {
            FirestoreClient.getFirestore().document( "notifications/" + notification.getId() )
                    .set( toFirebaseModel( notification ) ).get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
    }

    private Notification toNotification( DocumentSnapshot doc ) {
        LocalDateTime time = ( doc.getUpdateTime().toDate() ).toInstant().atZone(
                ZoneId.systemDefault() ).toLocalDateTime();
        Boolean read = doc.getBoolean( "read" );
        String id = doc.getId();
        return new Notification( id, doc.getString( "table" ), Type.valueOf( doc.getString( "type" ) ), time,
                read == null ? false : read );
    }

    private Map<String, Object> toFirebaseModel( Notification notification ) {
        Map<String, Object> object = new HashMap<>();
        object.put( "read", notification.isRead() );
        object.put( "table", notification.getTable() );
        object.put( "type", notification.getType().name() );
        return object;
    }

    private Map<String, Object> toFirebaseModel( RestaurantInfo info ) {
        Map<String, Object> object = new HashMap<>();
        object.put( "address", info.getAddress() );
        object.put( "facebook", info.getFacebook() );
        object.put( "twitter", info.getTwitter() );
        object.put( "location", new GeoPoint( info.getLatitude(), info.getLongitude() ) );
        object.put( "phone", info.getPhone() );
        object.put( "schedule", info.getSchedule() );
        return object;
    }

    Map<String, Object> toFirebaseModel( Order order ) {
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

    Map<String, Object> toFirebaseModel( Reservation reservation ) {
        Map<String, Object> object = new HashMap<>();
        object.put( "customer", reservation.getCustomer() );
        object.put( "table", reservation.getTable() );
        object.put( "start", Date.from( reservation.getStart().atZone( ZoneId.systemDefault() ).toInstant() ) );
        object.put( "end", Date.from( reservation.getEnd().atZone( ZoneId.systemDefault() ).toInstant() ) );
        return object;
    }


    Map<String, Object> toFirebaseModel( Food food ) {
        Map<String, Object> object = new HashMap<>();
        object.put( "image", food.getImage() );
        object.put( "name", food.getName() );
        object.put( "price", food.getPrice() );
        return object;
    }

    private Map<String, Object> toFirebaseModel( Table table ) {
        Map<String, Object> object = new HashMap<>();
        object.put( "name", table.getName() );
        object.put( "status", table.getStatus().name().toLowerCase() );
        object.put( "customer", table.getCustomer() );
        List<Map<String, Object>> foodOrders = new ArrayList<>();
        for ( FoodOrder order : table.getOrders() ) {
            foodOrders.add( toFirebaseModel( order ) );
        }
        object.put( "orders", foodOrders );
        return object;
    }

    private Map<String, Object> toFirebaseModel( FoodOrder foodOrder ) {
        Map<String, Object> object = new HashMap<>();
        object.put( "name", foodOrder.getName() );
        object.put( "quantity", foodOrder.getQuantity() );
        object.put( "price", foodOrder.getPrice() );
        return object;
    }

    Table toTable( DocumentSnapshot doc ) {
        String name = doc.getString( "name" );
        Status status = Status.valueOf( doc.getString( "status" ).toUpperCase() );
        String customer = doc.getString( "customer" );
        List<Map<String, Object>> docOrders = ( List<Map<String, Object>> ) doc.get( "orders" );
        List<FoodOrder> foodOrders = docOrders.stream().map( this::toFoodOrder ).collect( Collectors.toList() );
        Table table = new Table( name, status, foodOrders );
        table.setId( doc.getId() );
        table.setCustomer( customer );
        return table;
    }

    Order toOrder( DocumentSnapshot doc ) {
        String id = doc.getId();
        LocalDateTime time = ( ( Date ) doc.get( "time" ) ).toInstant().atZone(
                ZoneId.systemDefault() ).toLocalDateTime();
        String customerId = doc.getString( "customerId" ); // TODO cutomer name
        String tableName = doc.getString( "tableName" );
        List<Map<String, Object>> orders = ( List<Map<String, Object>> ) doc.get( "orders" );
        List<FoodOrder> foodOrders = orders.stream().map( this::toFoodOrder ).collect( Collectors.toList() );
        return new Order( id, time, customerId, tableName, foodOrders );
    }

    void updateTable( Table table, DocumentSnapshot doc ) {
        Table updated = toTable( doc );
        table.setId( updated.getId() );
        table.setName( updated.getName() );
        table.setOrders( updated.getOrders() );
        table.setStatus( updated.getStatus() );
        table.setCustomer( updated.getCustomer() );
    }

    FoodOrder toFoodOrder( Map<String, Object> data ) {
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

    RestaurantInfo toRestaurantInfo( DocumentSnapshot doc ) {
        GeoPoint location = doc.getGeoPoint( "location" );
        return new RestaurantInfo(
                doc.getString( "phone" ),
                doc.getString( "facebook" ),
                doc.getString( "twitter" ),
                doc.getString( "address" ),
                doc.getString( "schedule" ),
                location.getLatitude(),
                location.getLongitude() );
    }

}
