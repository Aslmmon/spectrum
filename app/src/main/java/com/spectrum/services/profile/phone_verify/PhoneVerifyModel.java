package com.spectrum.services.profile.phone_verify;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Azinova-Lap on 3/23/2018.
 */

public class PhoneVerifyModel {
    @SerializedName("status")
    String status;
    @SerializedName("message")
    String message;
    @SerializedName("phone")
    String phone;
    @SerializedName("customer_id")
    String customer_id;
    @SerializedName("otp")
    String otp;

    public PhoneVerifyModel(String phone, String customer_id) {
        this.phone = phone;
        this.customer_id = customer_id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
