package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CleanExtraService {
    @SerializedName("status")
    private String status;

    @SerializedName("extra_service_data")
    private List<ExtraServiceData> extraServiceData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ExtraServiceData> getExtraServiceData() {
        return extraServiceData;
    }

    public void setExtraServiceData(List<ExtraServiceData> extraServiceData) {
        this.extraServiceData = extraServiceData;
    }

    public class ExtraServiceData {
        @SerializedName("extra_service_id")
        private String extra_service_id;
        @SerializedName("name")
        private String extra_service_name;
        @SerializedName("rate")
        private String extra_service_amount;
        @SerializedName("image")
        private String extra_service_image;

        private  Boolean is_selected=false;

        public Boolean getIs_selected() {
            return is_selected;
        }

        public void setIs_selected(Boolean is_selected) {
            this.is_selected = is_selected;
        }

        public String getExtra_service_id() {
            return extra_service_id;
        }

        public void setExtra_service_id(String extra_service_id) {
            this.extra_service_id = extra_service_id;
        }

        public String getExtra_service_name() {
            return extra_service_name;
        }

        public void setExtra_service_name(String extra_service_name) {
            this.extra_service_name = extra_service_name;
        }

        public String getExtra_service_amount() {
            return extra_service_amount;
        }

        public void setExtra_service_amount(String extra_service_amount) {
            this.extra_service_amount = extra_service_amount;
        }

        public String getExtra_service_image() {
            return extra_service_image;
        }

        public void setExtra_service_image(String extra_service_image) {
            this.extra_service_image = extra_service_image;
        }
    }



}