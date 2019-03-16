package com.imenu.desktop.spring.ui;

import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.imenu.desktop.spring.RestaurantInfo;
import com.imenu.desktop.spring.ui.googlemaps.DragEndEvent;
import com.imenu.desktop.spring.ui.googlemaps.GoogleMap;
import com.imenu.desktop.spring.ui.googlemaps.GoogleMapMarker;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route( value = "restaurant", layout = MyAppLayoutRouterLayout.class )
public class RestaurantView extends VerticalLayout {

    public RestaurantView( FirebaseClient client ) {
        RestaurantInfo info = client.getInfo();
        TextField phoneField = new TextField( "Phone Number", info.getPhone(), "" );
        TextField facebookField = new TextField( "Facebook", info.getFacebook(), "" );
        TextField twitterField = new TextField( "Twitter", info.getTwitter(), "" );
        TextField addressField = new TextField( "Address", info.getAddress(), "" );
        TextField scheduleField = new TextField( "Schedule", info.getSchedule(), "" );
        Label locationLabel = new Label( "Location" );

        GoogleMapMarker marker = new GoogleMapMarker();
        marker.setLatitude( info.getLatitude() );
        marker.setLongitude( info.getLongitude() );
        marker.setDraggable( true );
        marker.addDragEndListener( ( ComponentEventListener<DragEndEvent> ) dragEndEvent -> {
            marker.setLatitude( marker.getLatitude() );
            marker.setLongitude( marker.getLongitude() );
        } );

        GoogleMap map = new GoogleMap( "AIzaSyCgjZN7XltbVAmVK1375tTa6rN3HfhgMuY" );
        map.setLatitude( info.getLatitude() );
        map.setLongitude( info.getLongitude() );
        map.setZoom( 20 );

        map.addMarker( marker );

        Button saveButton = new Button( "Save" );
        saveButton.setWidth( "100px" );
        saveButton.addClickListener( ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
            info.setPhone( phoneField.getValue() );
            info.setFacebook( facebookField.getValue() );
            info.setTwitter( twitterField.getValue() );
            info.setAddress( addressField.getValue() );
            info.setSchedule( scheduleField.getValue() );
            info.setLatitude( marker.getLatitude() );
            info.setLongitude( marker.getLongitude() );
            client.setInfo( info );
            Notification.show( "Successfully updated restaurant info", 3000, Position.BOTTOM_END );
        } );
        add( phoneField, facebookField, twitterField, addressField, scheduleField, locationLabel, map, saveButton );
    }
}
