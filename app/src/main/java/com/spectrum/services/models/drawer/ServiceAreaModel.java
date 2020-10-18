package com.spectrum.services.models.drawer;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 16/02/18.
 */

public class ServiceAreaModel {
    @SerializedName("status")
    private String status;

    @SerializedName("area_data")
    private List<SAreaData> dataList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SAreaData> getDataList() {
        return dataList;
    }

    public void setDataList(List<SAreaData> dataList) {
        this.dataList = dataList;
    }

    public class SAreaData
    {
        @SerializedName("longitude")
        private String longitude;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("name")
        private String name;

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
