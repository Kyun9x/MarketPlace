package com.modul.marketplace.model.orderonline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DmDeliveryInfo implements Serializable {

    @SerializedName("receiverName")
    private String receiverName = "";

    @SerializedName("receiverPhone")
    private String receiverPhone = "";

    @SerializedName("address")
    private String address = "";

    @SerializedName("city")
    private String city = "";
    private String cityName = "";

    @SerializedName("district")
    private String district;
    private String districtName = "";

    @SerializedName("location_uid")
    private String locationUid;

    @SerializedName("estimateShipped")
    private Integer estimateShipped;

    @SerializedName("note")
    private String note;


    @SerializedName("lng")
    private double lng = 0.0;
    @SerializedName("lat")
    private double lat = 0.0;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Integer getEstimateShipped() {
        return estimateShipped;
    }

    public void setEstimateShipped(Integer estimateShipped) {
        this.estimateShipped = estimateShipped;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLocationUid() {
        return locationUid;
    }

    public void setLocationUid(String locationUid) {
        this.locationUid = locationUid;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
