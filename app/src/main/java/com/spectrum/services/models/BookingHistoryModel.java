package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 26/02/18.
 */

public class BookingHistoryModel {
    @SerializedName("status")
    private String status;
    @SerializedName("booking_data_current")
    private List<CurrentData>currentData;
    @SerializedName("booking_data_past")
    private List<CurrentData>pastData;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CurrentData> getCurrentData() {
        return currentData;
    }

    public void setCurrentData(List<CurrentData> currentData) {
        this.currentData = currentData;
    }

    public List<CurrentData> getPastData() {
        return pastData;
    }

    public void setPastData(List<CurrentData> pastData) {
        this.pastData = pastData;
    }

    public class CurrentData
    {
        @SerializedName("type")
        private String type;
        @SerializedName("date")
        private String date;
        @SerializedName("rate")
        private String rate;
        @SerializedName("status")
        private String status;
        @SerializedName("service_type")
        private String service_type;
        @SerializedName("reference_id")
        private String reference_id;

        @SerializedName("is_cancel")
        private String is_cancel;

        @SerializedName("shift")
        private List<Shifts> schedule;
        @SerializedName("maids_count")
        private String maids;
        @SerializedName("cleaning_mat")
        private String clean_mat;
        @SerializedName("pay_status")
        private String pay_status;

        @SerializedName("hours")
        private String hours;
        @SerializedName("priority")
        private String priority;

        @SerializedName("vat")
        private String vat_perc;

        public String getVat_perc() {
            return vat_perc;
        }

        public void setVat_perc(String vat_perc) {
            this.vat_perc = vat_perc;
        }

        public String getReference_id() {
            return reference_id;
        }

        public void setReference_id(String reference_id) {
            this.reference_id = reference_id;
        }

        public String getService_type() {
            return service_type;
        }

        public void setService_type(String service_type) {
            this.service_type = service_type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getMaids() {
            return maids;
        }

        public void setMaids(String maids) {
            this.maids = maids;
        }

        public String getClean_mat() {
            return clean_mat;
        }

        public void setClean_mat(String clean_mat) {
            this.clean_mat = clean_mat;
        }

        public String getPay_status() {
            return pay_status;
        }

        public void setPay_status(String pay_status) {
            this.pay_status = pay_status;
        }

        public String getHours() {
            return hours;
        }

        public void setHours(String hours) {
            this.hours = hours;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public List<Shifts> getSchedule() {
            return schedule;
        }

        public void setSchedule(List<Shifts> schedule) {
            this.schedule = schedule;
        }

        public String getIs_cancel() {
            return is_cancel;
        }

        public void setIs_cancel(String is_cancel) {
            this.is_cancel = is_cancel;
        }

        public class Shifts
        {
            @SerializedName("date")
            public String date;
            @SerializedName("shift")
            public String time;
            @SerializedName("status")
            public String status;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }



}
