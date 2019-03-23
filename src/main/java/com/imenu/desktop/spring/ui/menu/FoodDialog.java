package com.imenu.desktop.spring.ui.menu;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.google.cloud.storage.Blob;
import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.Food;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.FinishedEvent;
import com.vaadin.flow.component.upload.StartedEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;

public class FoodDialog extends Dialog {

    private static Field field;

    private final FirebaseClient firebaseClient;

    private final String menuId;

    private final String categoryId;

    private final Food food;

    private TextField nameField;

    private TextField priceField;

    private TextField maxOrderField;

    private Button saveButton;

    private Image image;

    List<ComponentEventListener<ClickEvent<Button>>> saveListeners = new ArrayList<>();
    List<ComponentEventListener<ClickEvent<Button>>> deleteListeners = new ArrayList<>();

    static {
        try {
            field = FileOutputStream.class.getDeclaredField( "path" );
            field.setAccessible( true );
        } catch ( NoSuchFieldException e ) {
            e.printStackTrace();
        }

    }

    public FoodDialog( FirebaseClient firebaseClient, String menuId, String categoryId ) {
        this( firebaseClient, menuId, categoryId, new Food( null, "", 0 ) );
    }

    public FoodDialog( FirebaseClient firebaseClient, String menuId, String categoryId, Food food ) {
        this.firebaseClient = firebaseClient;
        this.menuId = menuId;
        this.categoryId = categoryId;
        this.food = food;
        VerticalLayout layout = new VerticalLayout();

        Label imageLabel = new Label( "Image" );
        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload( fileBuffer );
        image = new Image();
        image.setWidth( "300px" );
        image.setHeight( "300px" );
        if ( Strings.isNotBlank( food.getImage() ) )
            image.setSrc( food.getImage() );
        else
            image.setVisible( false );
        upload.setAcceptedFileTypes( "image/jpeg", "image/png", "image/gif" );
        upload.addSucceededListener( event -> {
            FileOutputStream os = ( FileOutputStream ) fileBuffer.getFileData().getOutputBuffer();
            try {
                String path = "" + field.get( os );
                InputStream inputStream = new BufferedInputStream( new FileInputStream( path ) );
                Blob blob = firebaseClient.upload( event.getFileName(), event.getMIMEType(), inputStream );
                image.setSrc( blob.getMediaLink() );
                image.setVisible( true );
            } catch ( IllegalAccessException | FileNotFoundException e ) {
                e.printStackTrace();
            }
        } );
        upload.addStartedListener(
                ( ComponentEventListener<StartedEvent> ) startedEvent -> saveButton.setEnabled( false ) );
        upload.addFinishedListener(
                ( ComponentEventListener<FinishedEvent> ) finishedEvent -> saveButton.setEnabled( true ) );

        nameField = new TextField( "Name" );
        nameField.setWidth( "100%" );
        nameField.setValue( food.getName() );
        priceField = new TextField( "Price" );
        priceField.setWidth( "100%" );
        priceField.setValue( String.format( "%.2f", food.getPrice() ) );

        maxOrderField = new TextField( "Max Order" );
        maxOrderField.setWidth( "100%" );
        maxOrderField.setValue( "" + ( food.getMaxOrder() <= -1 ? "" : food.getMaxOrder() ) );

        saveButton = new Button( "Save", this::save );
        saveButton.setWidth( "100%" );

        Button deleteButton = new Button( "Delete", e -> close() );
        deleteButton.addClickListener( ( ComponentEventListener<ClickEvent<Button>> ) e -> {
            String path = "menu/" + menuId + "/categories/" + categoryId + "/items/" + food.getId();
            firebaseClient.deleteFood( path );
            deleteListeners.forEach( dl -> dl.onComponentEvent( e ) );
        } );
        deleteButton.setWidth( "100%" );

        Button closeButton = new Button( "Close", e -> close() );
        closeButton.setWidth( "100%" );

        layout.add( imageLabel );
        layout.add( upload );
        layout.add( image );
        layout.add( nameField );
        layout.add( priceField );
        layout.add( maxOrderField );
        layout.add( saveButton );
        if ( Strings.isNotBlank( food.getId() ) )
            layout.add( deleteButton );
        layout.add( closeButton );
        layout.setHorizontalComponentAlignment( Alignment.CENTER, upload );
        add( layout );
    }

    public void save( ClickEvent<Button> e ) {
        nameField.setInvalid( false );
        priceField.setInvalid( false );
        maxOrderField.setInvalid( false );

        String name = nameField.getValue();
        if ( Strings.isBlank( name ) ) {
            nameField.setErrorMessage( "Required" );
            nameField.setInvalid( true );
        }

        String price = priceField.getValue();
        if ( Strings.isBlank( price ) ) {
            priceField.setInvalid( true );
            priceField.setErrorMessage( "Required" );
        } else {
            int maxOrderInt = -1;
            try {
                String maxOrder = maxOrderField.getValue();
                if (!Strings.isBlank( maxOrder))
                    maxOrderInt = Integer.parseInt( maxOrder );
            } catch ( NumberFormatException ex ) {
                maxOrderField.setInvalid( true );
                maxOrderField.setErrorMessage( "Must be a valid value" );
                return;
            }
            try {
                double priceDouble = Double.parseDouble( price );
                food.setImage( image.getSrc() );
                food.setName( name );
                food.setPrice( priceDouble );
                food.setMaxOrder( maxOrderInt );
                String path = "menu/" + menuId + "/categories/" + categoryId + "/items";
                if ( Strings.isNotBlank( food.getId() ) )
                    path += "/" + food.getId();
                String id = firebaseClient.setFood( path, food );
                food.setId(id);
                saveListeners.forEach( sl -> sl.onComponentEvent( e ) );
                close();
            } catch ( NumberFormatException ex ) {
                priceField.setInvalid( true );
                priceField.setErrorMessage( "Must be a valid number" );
            }
        }
    }

}
