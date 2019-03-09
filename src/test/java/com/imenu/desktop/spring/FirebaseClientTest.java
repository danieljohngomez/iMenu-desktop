package com.imenu.desktop.spring;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.cloud.FirestoreClient;
import com.imenu.desktop.spring.Table.Status;

public class FirebaseClientTest {

    @Test
    public void test() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream( "firebase.json" );

        FirebaseOptions options = new Builder()
                .setCredentials( GoogleCredentials.fromStream( serviceAccount ) )
                .build();

        FirebaseApp.initializeApp( options );
        try {
            FirestoreClient.getFirestore().collection("tables").get().get().getDocuments().stream()
                    .map( d -> toTable( d ) )
            .collect( Collectors.toList() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        new Scanner( System.in ).nextLine();
    }

    Table toTable(QueryDocumentSnapshot doc) {
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
        return table;
    }


}
