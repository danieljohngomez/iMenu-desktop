package com.imenu.desktop.spring;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QueryDocumentSnapshot;
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

    Table toTable( DocumentSnapshot doc ) {
        String name = doc.getString( "name" );
        Status status = Status.valueOf( doc.getString( "status" ).toUpperCase() );
        List<Map<String, Object>> docOrders = ( List<Map<String, Object>> ) doc.get( "orders" );
        List<FoodOrder> foodOrders = new ArrayList<>();
        for ( Map<String, Object> order : docOrders ) {
            String orderName = "" + order.getOrDefault( "name", "" );
            double orderPrice = Double.parseDouble( "" + order.getOrDefault( "price", "0" ) );
            int orderQuantity = Integer.parseInt( "" + order.getOrDefault( "quantity", "0" ) );
            FoodOrder foodOrder = new FoodOrder( orderName, orderPrice, orderQuantity );
            foodOrders.add( foodOrder );
        }
        Table table = new Table( name, status, foodOrders );
        table.setId( doc.getId() );
        return table;
    }

    void updateTable(Table table, DocumentSnapshot doc) {
        Table updated = toTable( doc );
        table.setId( updated.getId() );
        table.setName( updated.getName() );
        table.setOrders( updated.getOrders() );
        table.setStatus( updated.getStatus() );
    }

}
