package io.goolean.tech.hawker.merchant.Model;

public class ShareLocationModelHistory {
    String customer_mobile_no,latitude,longitude,date_time,location_name,customer_name,description,close_status;

    public String getCustomer_mobile_no() {
        return customer_mobile_no;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getLocation_name() {
        return location_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClose_status(String close_status) {
        this.close_status = close_status;
    }

    public String getDescription() {
        return description;
    }

    public String getClose_status() {
        return close_status;
    }

    public void setCustomer_mobile_no(String customer_mobile_no) {
        this.customer_mobile_no = customer_mobile_no;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
}
