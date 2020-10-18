package com.spectrum.services.adapters;

public class Intro {

    private String title;
    private String subtitle;
    private Integer image;

    Intro(String title, String subtitle, Integer img) {
        this.title = title;
        this.subtitle = subtitle;
        this.image = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
