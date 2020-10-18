package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 06/03/18.
 */

public class PriceResponseModel {
    @SerializedName("status")
    String status;

    @SerializedName("price")
    Price price;

    @SerializedName("promo_status")
    String coupen_status;

    @SerializedName("message")
    String message;

    public String getCoupen_status() {
        return coupen_status;
    }

    public void setCoupen_status(String coupen_status) {
        this.coupen_status = coupen_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public class Price
    {
        @SerializedName("vat_charge")
        String vat_charge;
        @SerializedName("service_rate")
        String service_rate;
        @SerializedName("gross_amount")
        String gross_amount;
        @SerializedName("hour_rate")
        String hour_rate;
        @SerializedName("vat_percentage")
        String vat_percentage;
        @SerializedName("coupon_id")
        String coupen_id;
        @SerializedName("coupon_val")
        String coupen_val;
        @SerializedName("discount")
        String discount;

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getCoupen_val() {
            return coupen_val;
        }

        public void setCoupen_val(String coupen_val) {
            this.coupen_val = coupen_val;
        }

        public String getVat_percentage() {
            return vat_percentage;
        }

        public void setVat_percentage(String vat_percentage) {
            this.vat_percentage = vat_percentage;
        }

        public String getCoupen_id() {
            return coupen_id;
        }

        public void setCoupen_id(String coupen_id) {
            this.coupen_id = coupen_id;
        }

        public String getVat_charge() {
            return vat_charge;
        }

        public void setVat_charge(String vat_charge) {
            this.vat_charge = vat_charge;
        }

        public String getService_rate() {
            return service_rate;
        }

        public void setService_rate(String service_rate) {
            this.service_rate = service_rate;
        }

        public String getGross_amount() {
            return gross_amount;
        }

        public void setGross_amount(String gross_amount) {
            this.gross_amount = gross_amount;
        }

        public String getHour_rate() {
            return hour_rate;
        }

        public void setHour_rate(String hour_rate) {
            this.hour_rate = hour_rate;
        }
    }

}
