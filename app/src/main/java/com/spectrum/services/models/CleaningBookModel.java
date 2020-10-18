package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 05/02/18.
 */

public class CleaningBookModel {
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CleaningBookModel(String date, String time) {
        this.date = date;
        this.time = time;
    }
}
