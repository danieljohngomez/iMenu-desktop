package com.imenu.desktop.spring;

import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.AGENDA_DAY;
import static org.vaadin.stefan.fullcalendar.CalendarViewImpl.BASIC_DAY;
import static org.vaadin.stefan.fullcalendar.SchedulerView.TIMELINE_DAY;
import static org.vaadin.stefan.fullcalendar.SchedulerView.TIMELINE_MONTH;
import static org.vaadin.stefan.fullcalendar.SchedulerView.TIMELINE_WEEK;
import static org.vaadin.stefan.fullcalendar.SchedulerView.TIMELINE_YEAR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.google.common.collect.ImmutableList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.ItemLabelGenerator;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.dom.ThemeList;

public class ReservationsCalendar extends Div {

    private FullCalendar calendar;

    private ComboBox<CalendarView> calendarViews;

    private Button buttonDatePicker;

    public ReservationsCalendar() {
        add( createToolbar() );

        add( new Hr() );

        calendar = createCalendar();
        calendar.setHeightByParent();
        calendar.getElement().getStyle().set( "flex-grow", "1" );
        calendar.setSizeFull();
        add( calendar );

        setSizeFull();
        getElement().getStyle().set( "display", "flex" );
        getElement().getStyle().set( "flex-direction", "column" );
    }

    private Component createToolbar() {
        Button buttonToday = new Button( "Today", VaadinIcon.HOME.create(), e -> calendar.today() );
        Button buttonPrevious = new Button( "Previous", VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous() );
        Button buttonNext = new Button( "Next", VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next() );
        buttonNext.setIconAfterText( true );

        List<CalendarView> calendarViews = new ArrayList<>();
        calendarViews.addAll( Arrays.asList( AGENDA_DAY, BASIC_DAY ) );
        calendarViews.addAll( Arrays.asList( SchedulerView.values() ) );
        this.calendarViews = new ComboBox<>( "", calendarViews );
        this.calendarViews.setAllowCustomValue( false );
        this.calendarViews.addValueChangeListener( e -> {
            if ( calendar != null ) {
                CalendarView value = e.getValue();
                calendar.changeView( value == null ? AGENDA_DAY : value );
            }
        } );
        this.calendarViews.setWidth( "250px" );
        this.calendarViews.setItemLabelGenerator( new ItemLabelGenerator<CalendarView>() {
            @Override
            public String apply( CalendarView calendarView ) {
                String label = "";
                if ( calendarView.equals( AGENDA_DAY ) )
                    label = "Agenda (Today)";
                else if ( calendarView.equals( BASIC_DAY ) )
                    label = "Basic (Today)";
                else if ( calendarView.equals( TIMELINE_DAY ) )
                    label = "Timeline (Today)";
                else if ( calendarView.equals( TIMELINE_WEEK ) )
                    label = "Timeline (This Week)";
                else if ( calendarView.equals( TIMELINE_MONTH ) )
                    label = "Timeline (This Month)";
                else if ( calendarView.equals( TIMELINE_YEAR ) )
                    label = "Timeline (This Year)";
                return label;
            }
        } );
        this.calendarViews.setValue( AGENDA_DAY );

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

    private FullCalendarScheduler createCalendar() {
        MyFullCalendarScheduler calendar = new MyFullCalendarScheduler();
        calendar.changeView( AGENDA_DAY );
        calendar.setNowIndicatorShown( true );
        calendar.setNumberClickable( true );
        calendar.setTimeslotsSelectable( true );

        ( ( Scheduler ) calendar ).setSchedulerLicenseKey( Scheduler.GPL_V3_LICENSE_KEY );

        calendar.addEntryClickedListener( event -> new EntryDialog( calendar, event.getEntry(), false ).open() );
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

            entry.setColor( "dodgerblue" );
            new EntryDialog( calendar, entry, true ).open();
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
                                    clickEvent -> new EntryDialog( calendar, entry, false ).open() );
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

        return calendar;
    }

    private void addReservation( Reservation reservation ) {
        Scheduler scheduler = ( Scheduler ) calendar;
        Resource resource = new Resource( reservation.getId(), "Table " + reservation.getTable(), null );
        scheduler.addResource( resource );
        LocalDateTime start = reservation.getStart();
        LocalDateTime end = reservation.getEnd();
        ResourceEntry entry = new ResourceEntry( reservation.getId(), reservation.getCustomer(), start, end,
                calendar.getTimezone(), false, true, null, "" );
        entry.addResources( ImmutableList.of( resource ) );
        calendar.addEntry( entry );
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
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "MMMM dd, YYYY" ).withLocale( locale ) );
                    break;
            }
        } else if ( view instanceof SchedulerView ) {
            switch ( ( SchedulerView ) view ) {
                default:
                case TIMELINE_MONTH:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "MMMM YYYY" ).withLocale( locale ) );
                    break;
                case TIMELINE_DAY:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "MMMM dd, YYYY" ).withLocale( locale ) );
                    break;
                case TIMELINE_WEEK:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "MMMM dd, YYYY" ).withLocale( locale ) )
                            + " - " + intervalStart.plusDays( 6 ).format(
                            DateTimeFormatter.ofPattern( "MMMM dd, YYYY" ).withLocale( locale ) );
                    break;
                case TIMELINE_YEAR:
                    text = intervalStart.format( DateTimeFormatter.ofPattern( "YYYY" ).withLocale( locale ) );
                    break;
            }
        }

        intervalLabel.setText( text );
    }

    public static class EntryDialog extends Dialog {

        EntryDialog( FullCalendar calendar, Entry entry, boolean newInstance ) {
            setCloseOnEsc( true );
            setCloseOnOutsideClick( true );

            VerticalLayout layout = new VerticalLayout();
            layout.setDefaultHorizontalComponentAlignment( FlexComponent.Alignment.STRETCH );
            layout.setSizeFull();

            TextField fieldTitle = new TextField( "Title" );
            fieldTitle.focus();

            TimePicker fieldStart = new TimePicker( "Start" );
            TimePicker fieldEnd = new TimePicker( "End" );

            fieldStart.setValue( entry.getStart().toLocalTime() );
            fieldEnd.setValue( entry.getEnd().toLocalTime() );

            layout.add( fieldTitle, fieldStart, fieldEnd );

            Binder<Entry> binder = new Binder<>( Entry.class );
            binder.forField( fieldTitle )
                    .asRequired()
                    .bind( Entry::getTitle, Entry::setTitle );
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

    }

}