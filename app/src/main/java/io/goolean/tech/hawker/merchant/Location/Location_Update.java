package io.goolean.tech.hawker.merchant.Location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 2/5/2018.
 */

public class Location_Update implements LocationListener {
    private LocationManager locationManager;
    String provider, tran_id;
    Location location = null;
    Context context ;
    boolean enabledGPS;
    public String LATTITUDE="0.0", LONGITUDE ="0.0";
    public float SPEED;
    public String address="NA",city="";
    public String postalCode="";

    public Location_Update(Context context) {
        this.context =  context ;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (location != null) {
            LATTITUDE = location.getLatitude()+"";
            LONGITUDE = location.getLongitude()+"";
            SPEED = location.getSpeed();
            getAddressFromLatLon(location.getLatitude(),location.getLongitude());

            onLocationChanged(location);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            if (location != null) {
                LATTITUDE = location.getLatitude()+"";
                LONGITUDE = location.getLongitude()+"";
                SPEED = location.getSpeed();
                getAddressFromLatLon(location.getLatitude(),location.getLongitude());
                onLocationChanged(location);
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
                if (location != null) {
                    try {
                        LATTITUDE = location.getLatitude()+"";
                        LONGITUDE = location.getLongitude()+"";
                        SPEED = location.getSpeed();
                        getAddressFromLatLon(location.getLatitude(),location.getLongitude());
                        onLocationChanged(location);
                    } catch (NullPointerException e) {
                    }
                }
            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        LATTITUDE = location.getLatitude()+"";
        LONGITUDE = location.getLongitude()+"";
        SPEED = location.getSpeed();
        getAddressFromLatLon(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }

    public String getAddressFromLatLon(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String add = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            // address = add+" "+city+" "+state+" "+country+" "+postalCode;
            address = add+" "+city+" "+state;
        } catch (Exception e) {
            address = "NA";
            e.printStackTrace();
        }
        return city;
    }
}
