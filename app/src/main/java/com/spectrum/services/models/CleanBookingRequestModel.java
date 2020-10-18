package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 07/03/18.
 */

public class CleanBookingRequestModel {
    @SerializedName("customer_id")
    String user_id;
    @SerializedName("service_type")
    String service_type;
    @SerializedName("instructions")
    String instructions;
    @SerializedName("maid_count")
    String maid_count;
    @SerializedName("hour_count")
    String hour_count;
    @SerializedName("cleaning_material")
    String cleaning_material;
    @SerializedName("price_per_hour")
    String price_per_hour;
    @SerializedName("service_charge")
    String service_charge;
    @SerializedName("vat_charge")
    String vat_charge;
    @SerializedName("total_amount")
    String total_amount;
    @SerializedName("crew_in")
    String crew_in;

    @SerializedName("coupon_id")
    String coupon_id;
    @SerializedName("coupon_val")
    String coupen_val;
    @SerializedName("discount")
    String discount;
    @SerializedName("schedule_list")
    List<CleaningBookModel>scheduleLists;
    @SerializedName("extra_service_id")
    List<String>extra_service_id;

    @SerializedName("payment_type")
    String payment_type;

    public String getPayment_type() {
        return payment_type;
    }

    public CleanBookingRequestModel(String user_id, String service_type, String instructions, String maid_count, String hour_count, String cleaning_material, String price_per_hour, String service_charge, String vat_charge, String total_amount, String crew_in, String coupon_id, String coupen_val, String discount, List<CleaningBookModel> scheduleLists, List<String> extra_service_id, String payment_type) {
        this.user_id = user_id;
        this.service_type = service_type;
        this.instructions = instructions;
        this.maid_count = maid_count;
        this.hour_count = hour_count;
        this.cleaning_material = cleaning_material;
        this.price_per_hour = price_per_hour;
        this.service_charge = service_charge;
        this.vat_charge = vat_charge;
        this.total_amount = total_amount;
        this.crew_in = crew_in;
        this.coupon_id = coupon_id;
        this.coupen_val = coupen_val;
        this.discount = discount;
        this.scheduleLists = scheduleLists;
        this.extra_service_id = extra_service_id;
        this.payment_type = payment_type;
    }

    public String getMaid_count() {
        return maid_count;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
}
