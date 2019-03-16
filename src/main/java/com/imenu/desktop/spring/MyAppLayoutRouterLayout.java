package com.imenu.desktop.spring;

import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.github.appreciated.app.layout.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.notification.component.AppBarNotificationButton;
import com.github.appreciated.app.layout.notification.entitiy.DefaultNotification;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.imenu.desktop.spring.ui.RestaurantView;
import com.imenu.desktop.spring.ui.menu.MenuView;
import com.imenu.desktop.spring.ui.OrdersView;
import com.imenu.desktop.spring.ui.TablesView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;

@Push
public class MyAppLayoutRouterLayout extends AppLayoutRouterLayout {

    /**
     * Do not initialize here. This will lead to NPEs
     */
    private DefaultNotificationHolder notifications;

    private DefaultBadgeHolder badge;

    public MyAppLayoutRouterLayout() {
        notifications = new DefaultNotificationHolder( newStatus -> {
        } );
        badge = new DefaultBadgeHolder( 5 );
        for ( int i = 1; i < 6; i++ ) {
            notifications.addNotification(
                    new DefaultNotification( "Test title" + i, "A rather long test description ..............." + i ) );
        }
        LeftNavigationComponent menuEntry = new LeftNavigationComponent( "Menu", VaadinIcon.MENU.create(),
                View6.class );
        badge.bind( menuEntry.getBadge() );

        Select<String> accounts = new Select<>( "Sign Out" );
        Div iconWrapper = new Div();
        iconWrapper.add(VaadinIcon.USER.create());
        accounts.addToPrefix( iconWrapper );

        init( AppLayoutBuilder
                .get( Behaviour.LEFT_RESPONSIVE_HYBRID )
                .withTitle( "iMenu" )
                .withAppBar( AppBarBuilder.get()
                        .add( new AppBarNotificationButton( VaadinIcon.BELL, notifications ) )
                        .add( accounts )
                        .build() )
                .withAppMenu( LeftAppMenuBuilder.get()
                        .add( new LeftNavigationComponent( "Menu" , VaadinIcon.CUTLERY, MenuView.class ) )
                        .add( new LeftNavigationComponent( "Reservations" , VaadinIcon.CALENDAR_O, ReservationView.class ) )
                        .add( new LeftNavigationComponent( "Tables" , VaadinIcon.SAFE, TablesView.class ) )
                        .add( new LeftNavigationComponent( "Customers" , VaadinIcon.USERS, Customers.class ) )
                        .add( new LeftNavigationComponent( "Orders" , VaadinIcon.CLIPBOARD, OrdersView.class ) )
                        .add( new LeftNavigationComponent( "Restaurant" , VaadinIcon.INFO, RestaurantView.class ) )
                        .build() )
                .build() );
    }

    @Route( value = "customers", layout = MyAppLayoutRouterLayout.class )
    public static class Customers extends VerticalLayout {

        public Customers() {
            // Add the content for this path
        }
    }

    @Route( value = "view5", layout = MyAppLayoutRouterLayout.class )
    public static class View5 extends VerticalLayout {

        public View5() {
            // Add the content for this path
        }
    }

    @Route( value = "view6", layout = MyAppLayoutRouterLayout.class )
    public static class View6 extends VerticalLayout {

        public View6() {
            // Add the content for this path
        }
    }
}
