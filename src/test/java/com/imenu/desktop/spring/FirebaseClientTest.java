package com.imenu.desktop.spring;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket.BlobWriteOption;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseClientTest {

    @Test
    public void test() throws IOException, InterruptedException, ExecutionException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream( "firebase.json" );

        FirebaseOptions options = new Builder()
                .setCredentials( GoogleCredentials.fromStream( serviceAccount ) )
                .build();

        FirebaseApp.initializeApp( options );
        List<Menu> menuList = new ArrayList<>();
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
        new Scanner( System.in ).nextLine();
    }

}
