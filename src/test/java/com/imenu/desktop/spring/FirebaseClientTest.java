package com.imenu.desktop.spring;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.auth.oauth2.GoogleCredentials;
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
            FirestoreClient.getFirestore().collection( "reservations" ).get().get().getDocuments().stream()
                    .map( this::toReservation )
                    .collect( Collectors.toList() );
        } catch ( InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        }
        new Scanner( System.in ).nextLine();
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
