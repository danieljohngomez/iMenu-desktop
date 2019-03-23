package com.imenu.desktop.spring;

import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.notification.NotificationHolder.NotificationsChangeListener;
import com.github.appreciated.app.layout.notification.component.AppBarNotificationButton;
import com.github.appreciated.app.layout.notification.entitiy.DefaultNotification;
import com.github.appreciated.app.layout.notification.entitiy.Priority;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.imenu.desktop.spring.Notification.Type;
import com.imenu.desktop.spring.ui.OrdersView;
import com.imenu.desktop.spring.ui.RestaurantView;
import com.imenu.desktop.spring.ui.TablesView;
import com.imenu.desktop.spring.ui.menu.MenuView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.Command;

@Push
public class MyAppLayoutRouterLayout extends AppLayoutRouterLayout {

    private final FirebaseClient firebaseClient;

    /**
     * Do not initialize here. This will lead to NPEs
     */
    private DefaultNotificationHolder notificationHolder;

    //private DefaultBadgeHolder badge;

    public MyAppLayoutRouterLayout( FirebaseClient firebaseClient ) {
        this.firebaseClient = firebaseClient;
        this.notificationHolder = new DefaultNotificationHolder( newStatus -> {
        } );

        notificationHolder.addNotificationsChangeListener( new NotificationsChangeListener() {

            @Override
            public void onNotificationRemoved(
                    com.github.appreciated.app.layout.notification.entitiy.Notification notification ) {
                readNotification( ( ( ExtendedNotification ) notification ) );
            }
        } );
        notificationHolder.addClickListener(
                newStatus -> readNotification( ( ( ExtendedNotification ) newStatus ) ) );
        //badge = new DefaultBadgeHolder( 5 );
        //LeftNavigationComponent menuEntry = new LeftNavigationComponent( "Menu", VaadinIcon.MENU.create(),
        //        View6.class );
        //badge.bind( menuEntry.getBadge() );

        //Select<String> accounts = new Select<>( "Sign Out" );
        //Div iconWrapper = new Div();
        //iconWrapper.add(VaadinIcon.USER.create());
        //accounts.addToPrefix( iconWrapper );

        init( AppLayoutBuilder
                .get( Behaviour.LEFT_RESPONSIVE_HYBRID )
                .withTitle( "iMenu" )
                .withAppBar( AppBarBuilder.get()
                        .add( new AppBarNotificationButton( VaadinIcon.BELL, this.notificationHolder ) )
                        //.add( accounts )
                        .build() )
                .withAppMenu( LeftAppMenuBuilder.get()
                        .add( new LeftNavigationComponent( "Menu", VaadinIcon.CUTLERY, MenuView.class ) )
                        .add( new LeftNavigationComponent( "Reservations", VaadinIcon.CALENDAR_O,
                                ReservationView.class ) )
                        .add( new LeftNavigationComponent( "Tables", VaadinIcon.SAFE, TablesView.class ) )
                        //.add( new LeftNavigationComponent( "Customers" , VaadinIcon.USERS, Customers.class ) )
                        .add( new LeftNavigationComponent( "Orders", VaadinIcon.CLIPBOARD, OrdersView.class ) )
                        .add( new LeftNavigationComponent( "Restaurant", VaadinIcon.INFO, RestaurantView.class ) )
                        .build() )
                .build() );

        firebaseClient.onNotification( notification -> {
            if ( notification.isRead() )
                return;
            System.out.println( "Received: " + notification.getDescription() );
            String title = "Notification";
            if ( notification.getType() == Type.bill_out )
                title = "Bill Out";
            else if ( notification.getType() == Type.assistance )
                title = "Needs Assistance";
            else if ( notification.getType() == Type.order )
                title = "Ready to Order";
            String finalTitle = title;
            getUI().get().access( ( Command ) () -> {
                ExtendedNotification defaultNotification = new ExtendedNotification( finalTitle,
                        notification.getDescription(), null, false );
                defaultNotification.setNotification( notification );
                defaultNotification.setRead( notification.isRead() );
                defaultNotification.setCreationTime( notification.getDate() );
                notificationHolder.addNotification( defaultNotification );
            } );
        } );
    }

    void readNotification( ExtendedNotification notification ) {
        Notification originalNotif = notification.getNotification();
        originalNotif.setRead( true );
        firebaseClient.setNotification( originalNotif );
    }

    class ExtendedNotification extends DefaultNotification {

        Notification notification;

        public ExtendedNotification( String title, String description ) {
            super( title, description );
        }

        public ExtendedNotification( String title, String description,
                Priority priority ) {
            super( title, description, priority );
        }

        public ExtendedNotification( String title, String description,
                Priority priority, boolean isSticky ) {
            super( title, description, priority, isSticky );
        }

        public void setNotification( Notification notification ) {
            this.notification = notification;
        }

        public Notification getNotification() {
            return notification;
        }
    }

}
