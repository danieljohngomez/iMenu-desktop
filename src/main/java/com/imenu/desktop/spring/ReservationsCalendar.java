package com.imenu.desktop.spring;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarScheduler;
import org.vaadin.stefan.fullcalendar.Resource;
import org.vaadin.stefan.fullcalendar.ResourceEntry;
import org.vaadin.stefan.fullcalendar.Scheduler;
import org.vaadin.stefan.fullcalendar.SchedulerView;
import org.vaadin.stefan.fullcalendar.TimeslotsSelectedSchedulerEvent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.dom.ThemeList;

//@Route( value = "reservations", layout = MyAppLayoutRouterLayout.class )
//@HtmlImport("frontend://styles.html")
//@HtmlImport("frontend://styles_scheduler.html")
public class ReservationsCalendar extends Div {

    private FullCalendar calendar;

    private ComboBox<CalendarView> calendarViews;

    private Button buttonDatePicker;

    public ReservationsCalendar() {
        add( createToolbar() );

        add( new Hr() );

        createCalendarInstance();
        add( calendar );

        setSizeFull();
        //calendar.setHeightByParent();
        setFlexStyles( true );
    }

    private Component createToolbar() {
        Button buttonToday = new Button( "Today", VaadinIcon.HOME.create(), e -> calendar.today() );
        Button buttonPrevious = new Button( "Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous() );
        Button buttonNext = new Button( "Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next() );
        buttonNext.setIconAfterText( true );

        List<CalendarView> calendarViews = new ArrayList<>();
        calendarViews.addAll( Arrays.asList( CalendarViewImpl.values() ) );
        calendarViews.addAll( Arrays.asList( SchedulerView.values() ) );
        this.calendarViews = new ComboBox<>( "", calendarViews );
        this.calendarViews.setValue( CalendarViewImpl.AGENDA_DAY );
        this.calendarViews.addValueChangeListener( e -> {
            CalendarView value = e.getValue();
            calendar.changeView( value == null ? CalendarViewImpl.AGENDA_DAY : value );
        } );

        // simulate the date picker light that we can use in polymer
        DatePicker gotoDate = new DatePicker();
        gotoDate.addValueChangeListener( event1 -> calendar.gotoDate( event1.getValue() ) );
        gotoDate.getElement().getStyle().set( "visibility", "hidden" );
        gotoDate.getElement().getStyle().set( "position", "fixed" );
        gotoDate.setWidth( "0px" );
        gotoDate.setHeight( "0px" );
        gotoDate.setWeekNumbersVisible( true );
        buttonDatePicker = new Button( VaadinIcon.CALENDAR.create() );
        buttonDatePicker.getElement().appendChild( gotoDate.getElement() );
        buttonDatePicker.addClickListener( event -> gotoDate.open() );

        HorizontalLayout toolbar = new HorizontalLayout( buttonPrevious, buttonDatePicker, buttonToday, buttonNext,
                this.calendarViews );
        toolbar.setSpacing( true );
        return toolbar;
    }

    private void setFlexStyles( boolean flexStyles ) {
        if ( flexStyles ) {
            calendar.getElement().getStyle().set( "flex-grow", "1" );
            getElement().getStyle().set( "display", "flex" );
            getElement().getStyle().set( "flex-direction", "column" );
        } else {
            calendar.getElement().getStyle().remove( "flex-grow" );
            getElement().getStyle().remove( "display" );
            getElement().getStyle().remove( "flex-direction" );
        }
    }

    private void createCalendarInstance() {
        calendar = new MyFullCalendarScheduler();
        calendar.changeView( CalendarViewImpl.AGENDA_DAY );
        calendar.setNowIndicatorShown( true );
        calendar.setNumberClickable( true );
        calendar.setTimeslotsSelectable( true );

        ( ( Scheduler ) calendar ).setSchedulerLicenseKey( Scheduler.GPL_V3_LICENSE_KEY );

        calendar.addEntryClickedListener( event -> new DemoDialog( calendar, event.getEntry(), false ).open() );
        calendar.addEntryResizedListener( event -> {
            event.applyChangesOnEntry();

            Entry entry = event.getEntry();

            Notification.show( entry.getTitle() + " resized to " + entry.getStart() + " - " + entry.getEnd() + " "
                    + calendar.getTimezone().getClientSideValue() + " by " + event.getDelta() );
        } );
        calendar.addEntryDroppedListener( event -> {
            event.applyChangesOnEntry();

            Entry entry = event.getEntry();
            LocalDateTime start = entry.getStart();
            LocalDateTime end = entry.getEnd();

            String text = entry.getTitle() + " moved to " + start + " - " + end + " "
                    + calendar.getTimezone().getClientSideValue() + " by " + event.getDelta();

            if ( entry instanceof ResourceEntry ) {
                Set<Resource> resources = ( ( ResourceEntry ) entry ).getResources();
                if ( !resources.isEmpty() ) {
                    text += text + " - rooms are " + resources;
                }
            }

            Notification.show( text );
        } );
        calendar.addViewRenderedListener(
                event -> updateIntervalLabel( buttonDatePicker, calendarViews.getValue(), event.getIntervalStart() ) );

        calendar.addTimeslotsSelectedListener( ( TimeslotsSelectedSchedulerEvent event ) -> {
            Entry entry = new Entry();

            entry.setStart( calendar.getTimezone().convertToUTC( event.getStartDateTime() ) );
            entry.setEnd( calendar.getTimezone().convertToUTC( event.getEndDateTime() ) );
            entry.setAllDay( event.isAllDay() );
            Optional<Resource> resource = event.getResource();
            System.out.println( resource );

            entry.setColor( "dodgerblue" );
            new DemoDialog( calendar, entry, true ).open();
        } );

        calendar.addLimitedEntriesClickedListener( event -> {
            Collection<Entry> entries = calendar.getEntries( event.getClickedDate() );
            if ( !entries.isEmpty() ) {
                Dialog dialog = new Dialog();
                VerticalLayout dialogLayout = new VerticalLayout();
                dialogLayout.setSpacing( false );
                dialogLayout.setPadding( false );
                dialogLayout.setMargin( false );
                dialogLayout.setDefaultHorizontalComponentAlignment( FlexComponent.Alignment.STRETCH );

                dialogLayout.add( new Span( "Entries of " + event.getClickedDate() ) );
                entries.stream()
                        .sorted( Comparator.comparing( Entry::getTitle ) )
                        .map( entry -> {
                            NativeButton button = new NativeButton( entry.getTitle(),
                                    clickEvent -> new DemoDialog( calendar, entry, false ).open() );
                            Style style = button.getStyle();
                            style.set( "background-color",
                                    Optional.ofNullable( entry.getColor() ).orElse( "rgb(58, 135, 173)" ) );
                            style.set( "color", "white" );
                            style.set( "border", "0 none black" );
                            style.set( "border-radius", "3px" );
                            style.set( "text-align", "left" );
                            style.set( "margin", "1px" );
                            return button;
                        } ).forEach( dialogLayout::add );

                dialog.add( dialogLayout );
                dialog.open();
            }
        } );

        calendar.addDayNumberClickedListener( event -> {
            calendarViews.setValue( CalendarViewImpl.LIST_DAY );
            calendar.gotoDate( event.getDateTime().toLocalDate() );
        } );
        calendar.addWeekNumberClickedListener( event -> {
            calendarViews.setValue( CalendarViewImpl.LIST_WEEK );
            calendar.gotoDate( event.getDateTime().toLocalDate() );
        } );

        createTestEntries( calendar );
    }

    private void createTestEntries( FullCalendar calendar ) {
        LocalDate now = LocalDate.now();

        Resource table1 = createResource( ( Scheduler ) calendar, "Table #1", "red" );
        Resource table2 = createResource( ( Scheduler ) calendar, "Table #2", "green" );
        Resource table3 = createResource( ( Scheduler ) calendar, "Table #3", "blue" );

        createTimedEntry( calendar, "Kickoff meeting with customer #1", now.withDayOfMonth( 3 ).atTime( 10, 0 ), 120,
                null, table3, table2, table1 );
        createTimedBackgroundEntry( calendar, now.withDayOfMonth( 3 ).atTime( 10, 0 ), 120, null, table3,
                table2, table1 );
        createTimedEntry( calendar, "Kickoff meeting with customer #2", now.withDayOfMonth( 7 ).atTime( 11, 30 ), 120,
                "mediumseagreen", table1 );
        createTimedEntry( calendar, "Kickoff meeting with customer #3", now.withDayOfMonth( 12 ).atTime( 9, 0 ), 120,
                "mediumseagreen", table2 );
        createTimedEntry( calendar, "Kickoff meeting with customer #4", now.withDayOfMonth( 13 ).atTime( 10, 0 ), 120,
                "mediumseagreen", table2 );
        createTimedEntry( calendar, "Kickoff meeting with customer #5", now.withDayOfMonth( 17 ).atTime( 11, 30 ), 120,
                "mediumseagreen", table3 );
        createTimedEntry( calendar, "Kickoff meeting with customer #6", now.withDayOfMonth( 22 ).atTime( 9, 0 ), 120,
                "mediumseagreen", table1 );

        createTimedEntry( calendar, "Grocery Store", now.withDayOfMonth( 7 ).atTime( 17, 30 ), 45, "violet" );
        createTimedEntry( calendar, "Dentist", now.withDayOfMonth( 20 ).atTime( 11, 30 ), 60, "violet" );
        createTimedEntry( calendar, "Cinema", now.withDayOfMonth( 10 ).atTime( 20, 30 ), 140, "dodgerblue" );
        createDayEntry( calendar, "Short trip", now.withDayOfMonth( 17 ), 2, "dodgerblue" );
        createDayEntry( calendar, "John's Birthday", now.withDayOfMonth( 23 ), 1, "gray" );
        createDayEntry( calendar, "This special holiday", now.withDayOfMonth( 4 ), 1, "gray" );

        createDayEntry( calendar, "Multi 1", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 2", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 3", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 4", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 5", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 6", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 7", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 8", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 9", now.withDayOfMonth( 12 ), 2, "tomato" );
        createDayEntry( calendar, "Multi 10", now.withDayOfMonth( 12 ), 2, "tomato" );

        createDayBackgroundEntry( calendar, now.withDayOfMonth( 4 ), 6, "#B9FFC3" );
        createDayBackgroundEntry( calendar, now.withDayOfMonth( 19 ), 2, "#CEE3FF" );
        createTimedBackgroundEntry( calendar, now.withDayOfMonth( 20 ).atTime( 11, 0 ), 150, "#FBC8FF" );
    }

    private Resource createResource( Scheduler calendar, String s, String color ) {
        Resource resource = new Resource( null, s, color );
        calendar.addResource( resource );
        return resource;
    }

    private void createDayEntry( FullCalendar calendar, String title, LocalDate start, int days, String color ) {
        ResourceEntry entry = new ResourceEntry();
        setValues( calendar, entry, title, start.atStartOfDay(), days, ChronoUnit.DAYS, color );
        calendar.addEntry( entry );
    }

    private void createTimedEntry( FullCalendar calendar, String title, LocalDateTime start, int minutes,
            String color ) {
        createTimedEntry( calendar, title, start, minutes, color, ( Resource[] ) null );
    }

    private void createTimedEntry( FullCalendar calendar, String title, LocalDateTime start, int minutes, String color,
            Resource... resources ) {
        ResourceEntry entry = new ResourceEntry();
        setValues( calendar, entry, title, start, minutes, ChronoUnit.MINUTES, color );
        if ( resources != null && resources.length > 0 ) {
            entry.addResources( Arrays.asList( resources ) );
        }
        calendar.addEntry( entry );
    }

    private void createDayBackgroundEntry( FullCalendar calendar, LocalDate start, int days, String color ) {
        ResourceEntry entry = new ResourceEntry();
        setValues( calendar, entry, "BG", start.atStartOfDay(), days, ChronoUnit.DAYS, color );

        entry.setRenderingMode( Entry.RenderingMode.BACKGROUND );
        calendar.addEntry( entry );
    }

    private void createTimedBackgroundEntry( FullCalendar calendar, LocalDateTime start, int minutes, String color ) {
        ResourceEntry entry = new ResourceEntry();
        setValues( calendar, entry, "BG", start, minutes, ChronoUnit.MINUTES, color );

        entry.setRenderingMode( Entry.RenderingMode.BACKGROUND );
        calendar.addEntry( entry );
    }

    private void createTimedBackgroundEntry( FullCalendar calendar, LocalDateTime start, int minutes, String color,
            Resource... resources ) {
        ResourceEntry entry = new ResourceEntry();
        setValues( calendar, entry, "BG", start, minutes, ChronoUnit.MINUTES, color );
        entry.setRenderingMode( Entry.RenderingMode.BACKGROUND );
        if ( resources != null && resources.length > 0 ) {
            entry.addResources( Arrays.asList( resources ) );
        }
        calendar.addEntry( entry );
    }

    private void setValues( FullCalendar calendar, ResourceEntry entry, String title, LocalDateTime start,
            int amountToAdd, ChronoUnit unit, String color ) {
        entry.setTitle( title );
        entry.setStart( start, calendar.getTimezone() );
        entry.setEnd( entry.getStartUTC().plus( amountToAdd, unit ) );
        entry.setAllDay( unit == ChronoUnit.DAYS );
        entry.setColor( color );
    }

    private void updateIntervalLabel( HasText intervalLabel, CalendarView view, LocalDate intervalStart ) {
        String text = "--";
        Locale locale = calendar.getLocale();

        if ( view == null ) {
            text = intervalStart.format( DateTimeFormatter.ofPattern( "MMMM yyyy" ).withLocale( locale ) );
        } else if ( view instanceof CalendarViewImpl ) {
            switch ( ( CalendarViewImpl ) view ) {
                default:
                case AGENDA_DAY:
                case BASIC_DAY:
                case LIST_DAY:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "dd.MM.yyyy" ).withLocale( locale ) );
                    break;
                case AGENDA_WEEK:
                case BASIC_WEEK:
                case LIST_WEEK:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "dd.MM.yy" ).withLocale( locale ) )
                            + " - " + intervalStart.plusDays( 6 ).format(
                            DateTimeFormatter.ofPattern( "dd.MM.yy" ).withLocale( locale ) ) + " (cw "
                            + intervalStart.format( DateTimeFormatter.ofPattern( "ww" ).withLocale( locale ) ) + ")";
                    break;
                case LIST_YEAR:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "yyyy" ).withLocale( locale ) );
                    break;
            }
        } else if ( view instanceof SchedulerView ) {
            switch ( ( SchedulerView ) view ) {
                default:
                case TIMELINE_MONTH:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "MMMM yyyy" ).withLocale( locale ) );
                    break;
                case TIMELINE_DAY:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "dd.MM.yyyy" ).withLocale( locale ) );
                    break;
                case TIMELINE_WEEK:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "dd.MM.yy" ).withLocale( locale ) )
                            + " - " + intervalStart.plusDays( 6 ).format(
                            DateTimeFormatter.ofPattern( "dd.MM.yy" ).withLocale( locale ) ) + " (cw "
                            + intervalStart.format( DateTimeFormatter.ofPattern( "ww" ).withLocale( locale ) ) + ")";
                    break;
                case TIMELINE_YEAR:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "yyyy" ).withLocale( locale ) );
                    break;
            }
        }

        intervalLabel.setText( text );
    }

    public static class DemoDialog extends Dialog {

        DemoDialog( FullCalendar calendar, Entry entry, boolean newInstance ) {
            setCloseOnEsc( true );
            setCloseOnOutsideClick( true );

            VerticalLayout layout = new VerticalLayout();
            layout.setDefaultHorizontalComponentAlignment( FlexComponent.Alignment.STRETCH );
            layout.setSizeFull();

            TextField fieldTitle = new TextField( "Title" );
            fieldTitle.focus();

            TextArea fieldDescription = new TextArea( "Description" );
            TimePicker fieldStart = new TimePicker( "Start" );
            TimePicker fieldEnd = new TimePicker( "End" );

            fieldStart.setValue( entry.getStart().toLocalTime() );
            fieldEnd.setValue( entry.getEnd().toLocalTime() );

            layout.add( fieldTitle, fieldDescription, fieldStart, fieldEnd );

            Binder<Entry> binder = new Binder<>( Entry.class );
            binder.forField( fieldTitle )
                    .asRequired()
                    .bind( Entry::getTitle, Entry::setTitle );

            binder.bind( fieldDescription, Entry::getDescription, Entry::setDescription );
            binder.setBean( entry );

            HorizontalLayout buttons = new HorizontalLayout();
            Button buttonSave;
            if ( newInstance ) {
                buttonSave = new Button( "Create", e -> calendar.addEntry( entry ) );
            } else {
                buttonSave = new Button( "Save", e -> calendar.updateEntry( entry ) );
            }
            buttonSave.addClickListener( e -> close() );
            buttons.add( buttonSave );

            Button buttonCancel = new Button( "Cancel", e -> close() );
            buttonCancel.getElement().getThemeList().add( "tertiary" );
            buttons.add( buttonCancel );

            if ( !newInstance ) {
                Button buttonRemove = new Button( "Remove", e -> {
                    calendar.removeEntry( entry );
                    close();
                } );
                ThemeList themeList = buttonRemove.getElement().getThemeList();
                themeList.add( "error" );
                themeList.add( "tertiary" );
                buttons.add( buttonRemove );
            }

            add( layout, buttons );
        }
    }

    public static class MyFullCalendarScheduler extends FullCalendarScheduler {

        public MyFullCalendarScheduler() {
        }

        public MyFullCalendarScheduler( int entryLimit ) {
            super( entryLimit );
        }
    }

}