package io.goolean.tech.hawker.merchant.Constant;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.content.Context.LOCATION_SERVICE;

public class AppStatus  {
    private static AppStatus instance = new AppStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;
    boolean available = false;
    LocationManager locationManager;

    public static AppStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    public boolean isEnabled() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            connected = true;
        }
        return connected;
    }

    public boolean isDataAvailable()
    {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            //Connected to working internet connection
            available = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            //Internet not available
            return available;
        }
        return available;
    }
}
