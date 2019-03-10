package com.imenu.desktop.spring;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseClientTest {

    @Test
    public void test() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream( "firebase.json" );

        FirebaseOptions options = new Builder()
                .setCredentials( GoogleCredentials.fromStream( serviceAccount ) )
                .build();

        FirebaseApp.initializeApp( options );
        try {
            FirestoreClient.getFirestore().document( "tables/MtMKTpOfCFe097bVGiqx" )
                    .update( "orders", new ArrayList<>() )
                    .get();
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        new Scanner( System.in ).nextLine();
    }

    Order toOrder( DocumentSnapshot doc ) {
        String id = doc.getId();
        LocalDateTime time = ((Date)doc.get( "time" )).toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
        String customerId = doc.getId();
        String tableName = doc.getString( "tableName" );
        List<Map<String, Object>> orders = ( List<Map<String, Object>> ) doc.get( "orders" );
        List<FoodOrder> foodOrders = orders.stream().map( this::toFoodOrder ).collect( Collectors.toList());
        return new Order( id, time, customerId, tableName, foodOrders );
    }


    FoodOrder toFoodOrder( Map<String, Object> data) {
        String orderName = "" + data.getOrDefault( "name", "" );
        double orderPrice = Double.parseDouble( "" + data.getOrDefault( "price", "0" ) );
        int orderQuantity = Integer.parseInt( "" + data.getOrDefault( "quantity", "0" ) );
        return new FoodOrder( orderName, orderPrice, orderQuantity );
    }

}
