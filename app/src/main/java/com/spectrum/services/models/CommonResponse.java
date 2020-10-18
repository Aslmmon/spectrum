package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leo elstin on 7/18/18.
 */
public class CommonResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("responce_code")
    private  String responce_code;
    @SerializedName("message")
    private String message;

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
}
