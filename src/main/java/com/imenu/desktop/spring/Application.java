package com.imenu.desktop.spring;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {
        //InputStream serviceAccount = Application.class.getClassLoader().getResourceAsStream( "firebase.json" );

        //FirebaseOptions options = new Builder()
        //        .setCredentials( GoogleCredentials.fromStream( serviceAccount ) )
        //        .build();

        //FirebaseApp.initializeApp( options );
        SpringApplication.run(Application.class, args);
    }

}
