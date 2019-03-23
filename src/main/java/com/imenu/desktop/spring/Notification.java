package com.imenu.desktop.spring;

import java.time.LocalDateTime;

public class Notification {

    private String id;

    private boolean read;

    private LocalDateTime date;

    private String table;

    private Type type;

    public Notification( String id, String table, Type type, LocalDateTime date, boolean read) {
        this.id = id;
        this.table = table;
        this.type = type;
        this.date = date;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setRead( boolean read ) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public String getTable() {
        return table;
    }

    public Type getType() {
        return type;
    }

    String getDescription() {
        if ( type == Type.bill_out )
            return table + " is billing out";
        if ( type == Type.order )
            return table + " is ready to order";
        if ( type == Type.assistance )
            return table + " needs assistance";
        return "";
    }

    public LocalDateTime getDate() {
        return date;
    }

    public enum Type {
        bill_out, assistance, order
    }
}
