package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 07/03/18.
 */

public class CleanBookingResponceModel {
    @SerializedName("status")
    String status;
    @SerializedName("responce_code")
    String responce_code;
    @SerializedName("booking_id")
    String booking_id;
    @SerializedName("message")
    String message;
    @SerializedName("reference_id")
    String reference_id;

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

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

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
