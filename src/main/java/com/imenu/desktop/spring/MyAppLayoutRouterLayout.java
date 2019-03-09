package com.imenu.desktop.spring;

import static com.github.appreciated.app.layout.entity.Section.FOOTER;
import static com.github.appreciated.app.layout.entity.Section.HEADER;
import static com.vaadin.flow.component.notification.Notification.Position.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.vaadin.stefan.fullcalendar.BusinessHours;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.FullCalendarScheduler;

import com.github.appreciated.app.layout.behaviour.Behaviour;
import com.github.appreciated.app.layout.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder;
import com.github.appreciated.app.layout.component.appmenu.MenuHeaderComponent;
import com.github.appreciated.app.layout.component.appmenu.left.LeftClickableComponent;
import com.github.appreciated.app.layout.component.appmenu.left.LeftNavigationComponent;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.appmenu.left.builder.LeftSubMenuBuilder;
import com.github.appreciated.app.layout.entity.DefaultBadgeHolder;
import com.github.appreciated.app.layout.notification.DefaultNotificationHolder;
import com.github.appreciated.app.layout.notification.component.AppBarNotificationButton;
import com.github.appreciated.app.layout.notification.entitiy.DefaultNotification;
import com.github.appreciated.app.layout.router.AppLayoutRouterLayout;
import com.imenu.desktop.spring.ui.OrdersView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
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
                        .add( new LeftNavigationComponent( "Menu" , VaadinIcon.CUTLERY, Menu.class ) )
                        .add( new LeftNavigationComponent( "Reservations" , VaadinIcon.CALENDAR_O, Reservation.class ) )
                        .add( new LeftNavigationComponent( "Tables" , VaadinIcon.TABLE, Tables.class ) )
                        .add( new LeftNavigationComponent( "Customers" , VaadinIcon.USERS, Customers.class ) )
                        .add( new LeftNavigationComponent( "Orders" , VaadinIcon.CLIPBOARD, OrdersView.class ) )
                        .build() )
                .build() );
    }

    @Route( value = "menu", layout = MyAppLayoutRouterLayout.class )
    public static class Menu extends VerticalLayout {

        public Menu() {
            // Add the content for this path
        }
    }

    @Route( value = "reservations", layout = MyAppLayoutRouterLayout.class )
    public static class Reservation extends VerticalLayout {

        public Reservation() {
            add( new ReservationsCalendar() );
        }
    }

    @Route( value = "tables", layout = MyAppLayoutRouterLayout.class )
    public static class Tables extends VerticalLayout {

        public Tables() {
            // Add the content for this path
        }
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
