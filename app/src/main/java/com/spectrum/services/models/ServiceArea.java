package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 01/03/18.
 */

public class ServiceArea {
    @SerializedName("status")
    private String status;

    @SerializedName("areas")
    private List<AreaData> areaData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AreaData> getAreaData() {
        return areaData;
    }

    public void setAreaData(List<AreaData> areaData) {
        this.areaData = areaData;
    }

    public class AreaData
    {
        @SerializedName("area_id")
        private String area_id;
        @SerializedName("zone_id")
        private String zone_id;
        @SerializedName("area_name")
        private String area_name;
        @SerializedName("area_charge")
        private String area_charge;
        @SerializedName("area_status")
        private String area_status;
        @SerializedName("zone_name")
        private String zone_name;

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getZone_id() {
            return zone_id;
        }

        public void setZone_id(String zone_id) {
            this.zone_id = zone_id;
        }

        public String getArea_name() {
            return area_name;
        }

        public void setArea_name(String area_name) {
            this.area_name = area_name;
        }

        public String getArea_charge() {
            return area_charge;
        }

        public void setArea_charge(String area_charge) {
            this.area_charge = area_charge;
        }

        public String getArea_status() {
            return area_status;
        }

        public void setArea_status(String area_status) {
            this.area_status = area_status;
        }

        public String getZone_name() {
            return zone_name;
        }

        public void setZone_name(String zone_name) {
            this.zone_name = zone_name;
        }
    }
}
