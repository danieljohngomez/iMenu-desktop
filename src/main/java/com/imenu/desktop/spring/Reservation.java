package com.imenu.desktop.spring;

import java.time.LocalDateTime;

public class Reservation {
    private String id;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    private String table;
    private LocalDateTime start;
    private LocalDateTime end;
    private String customer;

    public Reservation( String table, LocalDateTime start, LocalDateTime end, String customer ) {
        this.table = table;
        this.start = start;
        this.end = end;
        this.customer = customer;
    }

    public String getTable() {
        return table;
    }

    public void setTable( String table ) {
        this.table = table;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart( LocalDateTime start ) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd( LocalDateTime end ) {
        this.end = end;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer( String customer ) {
        this.customer = customer;
    }
}
