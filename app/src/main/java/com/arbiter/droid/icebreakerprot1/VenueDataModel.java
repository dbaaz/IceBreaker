package com.arbiter.droid.icebreakerprot1;

public class VenueDataModel {

    private String title;

    private String message;


    public VenueDataModel(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public VenueDataModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
