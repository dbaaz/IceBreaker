package com.arbiter.droid.icebreakerprot1;

import java.util.ArrayList;

public class AbstractModel2 {

    private String title;

    private String message;


    public AbstractModel2(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public AbstractModel2() {

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
