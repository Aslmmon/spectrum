package com.spectrum.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Abins Shaji on 27/02/18.
 */

public class UserModel {
    @SerializedName("status")
    private String status;
    @SerializedName("responce_code")
    private String responce_code;
    @SerializedName("message")
    private String message;

    @SerializedName("customer_details")
    private User  user;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponce_code() {
        return responce_code;
    }

    public void setResponce_code(String responce_code) {
        this.responce_code = responce_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class User
    {
        @SerializedName("name")
        private String name;
        @SerializedName("user_id")
        private String user_id;
        @SerializedName("email")
        private String email;
        @SerializedName("photo")
        private String photo;
        @SerializedName("phone")
        private String phone;
        @SerializedName("is_verified")
        private Boolean is_verified;
        @SerializedName("area_id")
        private String area_id;
        @SerializedName("area_name")
        private String area_name;
        @SerializedName("unit")
        private String unit;
        @SerializedName("building")
        private String building;
        @SerializedName("street")
        private String street;
        @SerializedName("other_area")
        private String other_area;

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getArea_name() {
            return area_name;
        }

        public void setArea_name(String area_name) {
            this.area_name = area_name;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getOther_area() {
            return other_area;
        }

        public void setOther_area(String other_area) {
            this.other_area = other_area;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Boolean getIs_verified() {
            return is_verified;
        }

        public void setIs_verified(Boolean is_verified) {
            this.is_verified = is_verified;
        }
    }
}
