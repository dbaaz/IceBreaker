package com.arbiter.droid.icebreakerprot1;

public class ImageRecyclerViewModel {

    private String title;
    private String url;
    private String message;


    public ImageRecyclerViewModel(String title, String message, String url) {
        this.title = title;
        this.message = message;
        this.url = url;
    }

    public ImageRecyclerViewModel() {

    }

    public String getUrl() { return url; }
    public void setUrl(String url) {this.url = url;}
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
