package com.spectrum.services.models.payfort;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 28/03/18.
 */
public class FortTokenResponce {
    @SerializedName("service_command")
    String service_command;
    @SerializedName("access_code")
    String access_code;
    @SerializedName("merchant_identifier")
    String merchant_identifier;
    @SerializedName("language")
    String language;
    @SerializedName("device_id")
    String device_id;
    @SerializedName("signature")
    String signature;
    @SerializedName("sdk_token")
    String sdk_token;
    @SerializedName("status")
    String status;
    @SerializedName("response_message")
    String response_message;
    @SerializedName("response_code")
    String response_code;

    public String getService_command() {
        return service_command;
    }

    public void setService_command(String service_command) {
        this.service_command = service_command;
    }

    public String getAccess_code() {
        return access_code;
    }

    public void setAccess_code(String access_code) {
        this.access_code = access_code;
    }

    public String getMerchant_identifier() {
        return merchant_identifier;
    }

    public void setMerchant_identifier(String merchant_identifier) {
        this.merchant_identifier = merchant_identifier;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSdk_token() {
        return sdk_token;
    }

    public void setSdk_token(String sdk_token) {
        this.sdk_token = sdk_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }
}
