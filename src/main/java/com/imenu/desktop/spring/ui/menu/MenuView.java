package com.imenu.desktop.spring.ui.menu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.imenu.desktop.spring.Category;
import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.Menu;
import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route( value = "menu", layout = MyAppLayoutRouterLayout.class )
public class MenuView extends VerticalLayout {

    public MenuView( FirebaseClient firebaseClient ) {
        //setSizeFull();

        List<Menu> menuList = firebaseClient.getMenu();
        Map<Tab, Component> tabsToPages = new LinkedHashMap<>();
        for ( int i = 0; i < menuList.size(); i++ ) {
            Menu menu = menuList.get( i );
            Tab menuTab = new Tab( menu.getName() );
            MenuItemView menuView = new MenuItemView( firebaseClient, menu );
            if ( i > 0 )
                menuView.setVisible( false );
            tabsToPages.put( menuTab, menuView );
        }

        Tabs tabs = new Tabs( tabsToPages.keySet().toArray( new Tab[0] ) );
        tabs.addSelectedChangeListener( event -> {
            tabsToPages.values().forEach( c -> c.setVisible( false ) );
            Component selectedPage = tabsToPages.get( tabs.getSelectedTab() );
            selectedPage.setVisible( true );
        } );

        tabs.setFlexGrowForEnclosedTabs( 1 );
        tabs.setWidth( "100%" );
        add( tabs );
        tabsToPages.values().forEach( this::add );
    }

    class MenuItemView extends VerticalLayout {

        private final FirebaseClient firebaseClient;

        private final Menu menu;

        public MenuItemView( FirebaseClient firebaseClient, Menu menu ) {
            this.firebaseClient = firebaseClient;
            this.menu = menu;
            TextField categoryNameField = new TextField( null, "Category Name" );
            Button addCategoryButton = new Button( "Add Category" );
            addCategoryButton.addClickListener( ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> {
                categoryNameField.setInvalid( false );
                String categoryName = categoryNameField.getValue();
                if ( Strings.isBlank(categoryName)) {
                    categoryNameField.setErrorMessage( "Required" );
                    categoryNameField.setInvalid( true );
                } else {
                    Category category = firebaseClient.addCategory( menu.getId(), new Category( null, categoryName ) );
                    menu.getCategories().add( category );
                    add( toCategoryView( category ) );
                    categoryNameField.setValue( "" );
                }
            } );
            HorizontalLayout layout = new HorizontalLayout( categoryNameField, addCategoryButton );
            add( layout );
            setHorizontalComponentAlignment( Alignment.CENTER, layout );

            for ( Category category : menu.getCategories() ) {
                add( toCategoryView( category ) );
            }
        }

        Component toCategoryView(Category category) {
            CategoryView categoryView = new CategoryView( firebaseClient, menu.getId(), category );
            categoryView.getStyle().set( "max-height", "600px" );
            categoryView.removeCategoryListeners.add(
                    ( ComponentEventListener<ClickEvent<Button>> ) buttonClickEvent -> remove( categoryView ) );
            return categoryView;
        }
    }

}
