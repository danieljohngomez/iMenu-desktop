package com.imenu.desktop.spring.ui.menu;

import com.imenu.desktop.spring.Category;
import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.Food;
import com.imenu.desktop.spring.ui.Card;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CategoryView extends VerticalLayout {

    private final FirebaseClient client;

    private final String menuId;

    private final Category category;

    private HorizontalLayout foodLayout;

    public CategoryView( FirebaseClient client, String menuId, Category category ) {
        this.client = client;
        this.menuId = menuId;
        this.category = category;
        Label label = new Label( category.getName() );

        foodLayout = new HorizontalLayout();
        for ( Food food : category.getItems() ) {
            foodLayout.add( createFoodCard( food ) );
        }

        Button addButton = new Button( "Add Food" );
        addButton.addClickListener( ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
            Food foodToAdd = new Food( null, "", 0 );
            FoodDialog dialog = new FoodDialog( client, menuId, category.getId(), foodToAdd );
            dialog.saveListeners.add(
                    ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent1 -> foodLayout.addComponentAtIndex(
                            foodLayout.getComponentCount() - 1, createFoodCard( foodToAdd ) ) );
            dialog.open();
        } );

        HorizontalLayout headerLayout = new HorizontalLayout( label, addButton );
        headerLayout.setDefaultVerticalComponentAlignment( Alignment.CENTER );
        add( headerLayout );
        add( foodLayout );
    }

    Component createFoodCard( Food food ) {
        Image image = new Image( food.getImage() == null ? "" : food.getImage(), "" );
        image.setWidth( "200px" );
        image.setHeight( "200px" );
        Label foodName = new Label( "Name:" );
        Label name = new Label( food.getName() );
        Div spacer = new Div();
        spacer.setHeight( "20px" );
        Div spacer2 = new Div();
        spacer2.setHeight( "20px" );
        Label foodPrice = new Label( "Price:" );
        Label price = new Label( String.format( "P%.2f", food.getPrice() ) );
        Card card = new Card( image, spacer, foodName, name, spacer2, foodPrice, price );
        card.setSpacing( false );
        card.addClickListener(
                ( ComponentEventListener<ClickEvent<Card>> ) clickEventClickEvent -> {
                    FoodDialog dialog = new FoodDialog( client, menuId, category.getId(), food );
                    dialog.saveListeners.add( ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
                        image.setSrc( food.getImage() == null ? "" : food.getImage() );
                        name.setText( food.getName() );
                        price.setText( String.format( "P%.2f", food.getPrice() ) );
                    } );
                    dialog.deleteListeners.add(
                            ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> foodLayout.remove( card ) );
                    dialog.open();
                } );
        return card;
    }

}
