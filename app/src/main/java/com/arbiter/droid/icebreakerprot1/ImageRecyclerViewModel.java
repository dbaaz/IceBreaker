package com.arbiter.droid.icebreakerprot1;

public class ImageRecyclerViewModel {

    private String url;


    public ImageRecyclerViewModel(String url) {
        this.url = url;
    }

    public ImageRecyclerViewModel() {

    }

    public String getUrl() { return url; }
    public void setUrl(String url) {this.url = url;}
}
