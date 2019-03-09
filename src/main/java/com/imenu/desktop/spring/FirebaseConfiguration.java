package com.imenu.desktop.spring;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfiguration {

    FirebaseClient firebaseClient() {
        return new MockFirebaseClient();
    }

}
