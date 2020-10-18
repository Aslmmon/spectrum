package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Abins Shaji on 06/03/18.
 */

public class PriceRequestModel {
    @SerializedName("customer_id")
    String user_id;
    @SerializedName("hours_no")
    String hours_no;
    @SerializedName("maids_no")
    String maids_no;
    @SerializedName("cleaning_material")
    String cleaning_material;
    @SerializedName("service_type")
    String service_type;
    @SerializedName("daycount")
    String daycount;
    @SerializedName("coupon_val")
    String coupen_val;
    @SerializedName("date_for_price")
    String date;
    @SerializedName("extra_service")
    ArrayList<String> extra_service;

    public PriceRequestModel(String user_id, String hours_no, String maids_no, String cleaning_material, String service_type, String daycount, String coupen_val, ArrayList<String> extra_service,String date) {
        this.user_id = user_id;
        this.hours_no = hours_no;
        this.maids_no = maids_no;
        this.cleaning_material = cleaning_material;
        this.service_type = service_type;
        this.daycount = daycount;
        this.coupen_val = coupen_val;
        this.extra_service = extra_service;
        this.date=date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public PriceRequestModel() {
    }

    public PriceRequestModel(String hours_no, String maids_no, String cleaning_material, String service_type, String daycount, String coupen_val, ArrayList<String> extra_service,String date) {
        this.hours_no = hours_no;
        this.maids_no = maids_no;
        this.cleaning_material = cleaning_material;
        this.service_type = service_type;
        this.daycount = daycount;
        this.coupen_val = coupen_val;
        this.extra_service = extra_service;
        this.date=date;
    }

    public String getCoupen_val() {
        return coupen_val;
    }

    public void setCoupen_val(String coupen_val) {
        this.coupen_val = coupen_val;
    }

    public ArrayList<String> getExtra_service() {
        return extra_service;
    }

    public void setExtra_service(ArrayList<String> extra_service) {
        this.extra_service = extra_service;
    }

    public String getHours_no() {
        return hours_no;
    }

    public void setHours_no(String hours_no) {
        this.hours_no = hours_no;
    }

    public String getMaids_no() {
        return maids_no;
    }

    public void setMaids_no(String maids_no) {
        this.maids_no = maids_no;
    }

    public String getCleaning_material() {
        return cleaning_material;
    }

    public void setCleaning_material(String cleaning_material) {
        this.cleaning_material = cleaning_material;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getDaycount() {
        return daycount;
    }

    public void setDaycount(String daycount) {
        this.daycount = daycount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
