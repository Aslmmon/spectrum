package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 07/02/18.
 */

public class BookTime {
    @SerializedName("status")
    private String status;
    @SerializedName("responce_code")
    private  String responce_code;
    @SerializedName("message")
    private String message;
    @SerializedName("booking_time_data")
    private List<String> timeData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponce_code() {
        return responce_code;
    }

    public void setResponce_code(String responce_code) {
        this.responce_code = responce_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getTimeData() {
        return timeData;
    }

    public void setTimeData(List<String> timeData) {
        this.timeData = timeData;
    }
}
