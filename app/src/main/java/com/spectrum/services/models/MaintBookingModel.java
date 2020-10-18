package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 08/03/18.
 */

public class MaintBookingModel {


    @SerializedName("customer_id")
    String customer_id;
    String service_type_name;
    @SerializedName("service_type")
    String service_type_id;
    @SerializedName("priority")
    String priority;
    @SerializedName("date")
    String date;
    @SerializedName("time_type")
    String time_type;
    @SerializedName("time")
    String time;
    @SerializedName("instructions")
    String instruction;
    @SerializedName("coupon_id")
    String coupen_id;
    @SerializedName("discount")
    String discount;

    public MaintBookingModel(String customer_id, String service_type_name, String service_type_id, String priority, String date, String time_type, String time, String instruction, String coupen_id, String discount) {
        this.customer_id = customer_id;
        this.service_type_name = service_type_name;
        this.service_type_id = service_type_id;
        this.priority = priority;
        this.date = date;
        this.time_type = time_type;
        this.time = time;
        this.instruction = instruction;
        this.coupen_id = coupen_id;
        this.discount = discount;
    }

    public String getService_type_id() {
        return service_type_id;
    }

    public void setService_type_id(String service_type_id) {
        this.service_type_id = service_type_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getService_type_name() {
        return service_type_name;
    }

    public void setService_type_name(String service_type_name) {
        this.service_type_name = service_type_name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_type() {
        return time_type;
    }

    public void setTime_type(String time_type) {
        this.time_type = time_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
