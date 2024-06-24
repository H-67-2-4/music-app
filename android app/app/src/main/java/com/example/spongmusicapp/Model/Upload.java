package com.example.spongmusicapp.Model;

public class Upload {

    public String name;
    public String url ;
    public String songsCategory;

    public Upload(String name,String url,String songsCategory) {
        this.songsCategory = songsCategory;
        this.url = url;
        this.name = name;
    }

    public Upload(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSongsCategory() {
        return songsCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

