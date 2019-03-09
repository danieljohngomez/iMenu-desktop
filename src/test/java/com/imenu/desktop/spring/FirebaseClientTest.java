package com.imenu.desktop.spring;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.testng.annotations.Test;

import com.google.auth.oauth2.GoogleCredentials;
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
        FirestoreClient.getFirestore().getCollections().forEach( collectionReference -> {
            System.out.println( collectionReference.getId() );
        } );
        new Scanner( System.in ).nextLine();
    }
}
