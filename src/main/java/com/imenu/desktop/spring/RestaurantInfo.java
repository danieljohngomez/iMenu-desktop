package com.imenu.desktop.spring;

public class RestaurantInfo {
    private String phone;
    private String facebook;
    private String twitter;
    private String address;
    private String schedule;
    private double latitude;
    private double longitude;

    public RestaurantInfo( String phone, String facebook, String twitter, String address, String schedule,
            double latitude,
            double longitude ) {
        this.phone = phone;
        this.facebook = facebook;
        this.twitter = twitter;
        this.address = address;
        this.schedule = schedule;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone( String phone ) {
        this.phone = phone;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook( String facebook ) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter( String twitter ) {
        this.twitter = twitter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule( String schedule ) {
        this.schedule = schedule;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude( double latitude ) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude( double longitude ) {
        this.longitude = longitude;
    }
}
