package io.goolean.tech.hawker.merchant.Location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeoAddress {

  private FragmentActivity context;
    private Context mcontext;
    private List<Address> addresses = null;

String latitude,longitude;

    public GeoAddress(FragmentActivity context) {
        this.context = context;
    }

    public GeoAddress(Context context) {
        this.mcontext = context;
    }

    public String func_GeoAddress(double curr_lati, double curr_long) {


            StringBuilder result = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(curr_lati, curr_long, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String state = address.getAdminArea();
                    String city=address.getLocality();
                    String sector = address.getSubLocality();
                    String featurName= address.getFeatureName();
                    String pin = address.getPostalCode();
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return result.toString();
        }


}
