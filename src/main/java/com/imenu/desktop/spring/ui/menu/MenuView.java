package com.imenu.desktop.spring.ui.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imenu.desktop.spring.Category;
import com.imenu.desktop.spring.FirebaseClient;
import com.imenu.desktop.spring.Menu;
import com.imenu.desktop.spring.MyAppLayoutRouterLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

@Route( value = "menu", layout = MyAppLayoutRouterLayout.class )
public class MenuView extends VerticalLayout {

    public MenuView( FirebaseClient firebaseClient ) {
        //setSizeFull();

        List<Menu> menuList = firebaseClient.getMenu();
        Map<Tab, Component> tabsToPages = new HashMap<>();
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

        public MenuItemView( FirebaseClient firebaseClient, Menu menu ) {
            Button addButton = new Button( "Add" );
            add( addButton );
            setHorizontalComponentAlignment( Alignment.CENTER, addButton );

            for ( Category category : menu.getCategories() ) {
                CategoryView categoryView = new CategoryView( firebaseClient, menu.getId(), category );
                categoryView.getStyle().set( "max-height", "600px" );
                add( categoryView );
            }
        }
    }

}
