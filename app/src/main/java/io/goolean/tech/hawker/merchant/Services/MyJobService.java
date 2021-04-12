package io.goolean.tech.hawker.merchant.Services;

import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Location.GetLatLngActivity;
import io.goolean.tech.hawker.merchant.Location.Location_Update;
import io.goolean.tech.hawker.merchant.Model.Model_Latlng;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;


public class MyJobService extends JobService {
    BackgroundTask backgroundTask;
    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    private int service_time;
    private SMSReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;
    private Location_Update location_update;
    private String android_id;
    private int deviceStatus;
    private int batteryLevel;
    IntentFilter intentfilter;
    private static String uniqueID = null;
    private LocationManager locationManager;
    private String strStatus, strDuty_status;
    NotificationChannel mChannel;
    Intent intent;
    Location location;


    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final String TAG = "MyLocationService";
    private String LATTITUDE,LONGITUDE;



    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            LATTITUDE = location.getLatitude()+"";
            LONGITUDE = location.getLongitude()+"";
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };


    @Override
    public boolean onStartJob(@NonNull final JobParameters job) {
        mSMSreceiver = new SMSReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(mSMSreceiver, mIntentFilter);
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        MyJobService.this.registerReceiver(broadcastreceiver, intentfilter);
        // registerReceiver(mSMSreceiver, mIntentFilter);
        location_update = new Location_Update(MyJobService.this);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPrefrence_Login.getDataLogin(getApplicationContext());
        SharedPrefrence_Login.getDataONOFF(getApplicationContext());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("bcdqaj",SharedPrefrence_Login.getONOFF());

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        service_time = SharedPrefrence_Login.getSeller_time();
        Log.d("service_timejob",""+service_time);
        startTimer(job);

        return true;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTimer(final JobParameters job) {
        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {
                backgroundTask = new BackgroundTask() {
                    @Override
                    protected void onPostExecute(String s) {
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            if (SharedPrefrence_Login.getPtype().equals("seller")) {
                                if (SharedPrefrence_Login.getONOFF().equals("1")) {
                                    //Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();

                                    sendSellerLocation(SharedPrefrence_Login.getMhawker_code());
                                 } else if (SharedPrefrence_Login.getONOFF().equals("0")) {
                                    //Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_SHORT).show();
                                    stopTimer(timer);
                                }
                            }
                        } else {
                            Log.i("DUTY STATUS", "DUTY OFF");
                            if (SharedPrefrence_Login.getPtype().equals("seller")) {
                                fun_DutyONOFFScreen("0", SharedPrefrence_Login.getMhawker_code(), Urls.URL_DUTY_ON_OFF_SELLER);
                            }

                        }
                    }
                };
                backgroundTask.execute();
            }
        };
        timer.schedule(timerTask, 1000, service_time); //
    }


    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
// stopTimer(timer);
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
        }
        Log.i("TAG", "onStopJob");
        return true;
    }

    private void stopTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    public static class BackgroundTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return "Hello from background job";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
// Unregister the SMS receiver
            unregisterReceiver(mSMSreceiver);
            unregisterReceiver(broadcastreceiver);
        } catch (Exception ee) {

        }
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryLevel = (int) (((float) level / (float) scale) * 100.0f);
        }
    };

    private void sendSellerLocation(final String sellerID) {
// HttpsTrustManager.allowAllSSL();
        final RequestQueue requestQueue = VolleySingleton.getInstance(MyJobService.this).getRequestQueue();
//if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_SELLER_CURRENT_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            if (jsoObject.getString("status").equals("1")) {
                                Log.i("API_START", "========= " + (counter++));
                                Log.i("BATTERY_STATUS", "========= " + (batteryLevel));
                                Log.i("BATTERY_STATUS", "========= " + (LATTITUDE));

                                //Toast.makeText(MyJobService.this, "Seller Success ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
// Toast.makeText(getApplicationContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", sellerID);
                params.put("device_id", android_id);
                params.put("latitude", LATTITUDE);
                params.put("longitude",LONGITUDE);
                params.put("battery_status", batteryLevel + "");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////

    private void fun_DutyONOFFScreen(final String sStatus, final String sellerID, final String url) {
        final RequestQueue requestQueue = VolleySingleton.getInstance(MyJobService.this).getRequestQueue();
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//converting response to json object
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus = jsoObject.getString("status");
                            strDuty_status = jsoObject.getString("duty_status");
                            if (strStatus.equals("1")) {
                                if (!strDuty_status.equals("1")) {
                                    stopTimer(timer);
                                    SharedPrefrence_Login.saveONOFF(MyJobService.this, "0");
                                } else {
                                    stopTimer(timer);
                                    SharedPrefrence_Login.saveONOFF(MyJobService.this, "0");
                                }
                            } else {
                                if (!strDuty_status.equals("1")) {
                                    stopTimer(timer);
                                    SharedPrefrence_Login.saveONOFF(MyJobService.this, "0");
                                } else {
                                    stopTimer(timer);
                                    SharedPrefrence_Login.saveONOFF(MyJobService.this, "0");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//Toast.makeText(Demo_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", sellerID);
                params.put("longitude", location_update.LONGITUDE);
                params.put("latitude", location_update.LATTITUDE);
                params.put("duty_status", sStatus);
                return params;
            }
        };
        requestQueue.add(getRequest);
    }

}