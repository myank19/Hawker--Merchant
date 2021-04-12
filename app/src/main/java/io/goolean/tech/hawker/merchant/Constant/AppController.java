package io.goolean.tech.hawker.merchant.Constant;

import android.app.Application;

public class AppController extends Application {

    String city,lattitude,longitude;


    public void setCity(String city) {
        this.city=city;
    }

    public void setLatitude(String lattitude) {
        this.lattitude=lattitude;
    }

    public void setLongitude(String longitude) {
        this.longitude=longitude;
    }


    public String getCity() {
        return city;
    }

    public String getLattitude() {
        return lattitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
