package io.goolean.tech.hawker.merchant.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.JobParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;
import io.goolean.tech.hawker.merchant.View.Home;

import static io.goolean.tech.hawker.merchant.Translate.App.CHANNEL_ID;

public class LocationService extends Service {
    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;
    private int service_time=10000;
    private Handler mHandler = new Handler();
    private int deviceStatus,batteryLevel;
    private SMSReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;
    private String android_id;
    IntentFilter intentfilter;
    private LocationManager locationManager;
    private String strStatus, strDuty_status;
    private Runnable runnable;


    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final String TAG = "MyLocationService";
    private String LATTITUDE,LONGITUDE;
    Notification notification;
    private SwitchButtonListener switchButtonListener  = new SwitchButtonListener();
    private SwitchButtonListenerON switchButtonListenerON  = new SwitchButtonListenerON();

    /*Location update*/
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

    /* Here Srevice Functions*/
    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputData");
       // startNotification();
        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_business)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        Intent dutyIntent = new Intent("action.cancel.notification");
        PendingIntent pendingDutyIntent = PendingIntent.getBroadcast(this,0,dutyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        //the intent that is started when the notification is clicked (works)
        notification.contentView = notificationView;
        notification.contentIntent = pendingDutyIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;


        notificationView.setOnClickPendingIntent(R.id.closeDuty, pendingDutyIntent);

        ///   https://www.yudiz.com/custom-notification/

        startForeground(1,notification);
        mSMSreceiver = new SMSReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(mSMSreceiver, mIntentFilter);

        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("action.cancel.notification");
        registerReceiver(switchButtonListener, ifilter);

        IntentFilter ifilters = new IntentFilter();
        ifilters.addAction("dutyON");
        registerReceiver(switchButtonListenerON, ifilters);


        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        LocationService.this.registerReceiver(broadcastreceiver, intentfilter);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPrefrence_Login.getDataLogin(getApplicationContext());
        SharedPrefrence_Login.getDataONOFF(getApplicationContext());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        /*service_time = SharedPrefrence_Login.getSeller_time();
        Log.d("service_time",""+service_time);*/
        Log.d("service_time",""+service_time);
        startTimer();

        Log.d("isGPSEnabled",""+isGPSEnabled(getApplicationContext()));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastreceiver);
            unregisterReceiver(switchButtonListenerON);
            unregisterReceiver(switchButtonListener);
        } catch (Exception ee) {

        }
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    private void stopTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /*public void startTimer() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.i("Service_hello","Job Start");
                Log.i("onoff",SharedPrefrence_Login.getONOFF());
                Log.i("sellerr",SharedPrefrence_Login.getPtype());
                //Toast.makeText(getApplicationContext(),"Location start",Toast.LENGTH_LONG).show();
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (SharedPrefrence_Login.getPtype().equals("seller")) {
                        if (SharedPrefrence_Login.getONOFF().equals("1")) {
                            Toast.makeText(getApplicationContext(),"Location onn",Toast.LENGTH_LONG).show();
                            sendSellerLocation(SharedPrefrence_Login.getMhawker_code());
                            Log.i("onnn","service on");
                        } else if (SharedPrefrence_Login.getONOFF().equals("0")) {
                            // mHandler.removeCallbacks(runnable);
                            // stopTimer(timer);
                            Log.i("offf","service off");
                        }
                    }
                } else {
                    Log.i("Serviceee", "DUTY OFF");
                    if (SharedPrefrence_Login.getPtype().equals("seller")) {
                        fun_DutyONOFFScreen("0", SharedPrefrence_Login.getMhawker_code(), Urls.URL_DUTY_ON_OFF_SELLER);
                        Toast.makeText(getApplicationContext(),"Location off",Toast.LENGTH_LONG).show();
                    }
                }
            }
        },0,10,TimeUnit.SECONDS);
    }*/

    public void startTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {

                Log.i("Service_hello","Job Start");
                Log.i("onoff",SharedPrefrence_Login.getONOFF());
                Log.i("sellerr",SharedPrefrence_Login.getPtype());
                //Toast.makeText(getApplicationContext(),"Location start",Toast.LENGTH_LONG).show();
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (SharedPrefrence_Login.getPtype().equals("seller")) {
                        if (SharedPrefrence_Login.getONOFF().equals("1")) {
                            sendSellerLocation(SharedPrefrence_Login.getMhawker_code());
                            Log.i("onnn","service on");
                        } else if (SharedPrefrence_Login.getONOFF().equals("0")) {
                            // mHandler.removeCallbacks(runnable);
                            // stopTimer(timer);
                            Log.i("offf","service off");
                        }
                    }
                } else {
                    Log.i("Serviceee", "DUTY OFF");
                    if (SharedPrefrence_Login.getPtype().equals("seller")) {
                        fun_DutyONOFFScreen("0", SharedPrefrence_Login.getMhawker_code(), Urls.URL_DUTY_ON_OFF_SELLER);
                    }
                }
            }
        };
        timer.schedule(timerTask, 1000, service_time); //
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendSellerLocation(final String sellerID) {
        final RequestQueue requestQueue = VolleySingleton.getInstance(LocationService.this).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_SELLER_CURRENT_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            Log.d("responseee",response);
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            if (jsoObject.getString("status").equals("1")) {
                                Log.i("API_START", "========= " + (counter++));
                                Log.i("BATTERY_STATUS", "========= " + (batteryLevel));
                                Log.i("BATTERY_STATUS", "========= " + (LATTITUDE));
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
        final RequestQueue requestQueue = VolleySingleton.getInstance(LocationService.this).getRequestQueue();
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus = jsoObject.getString("status");
                            strDuty_status = jsoObject.getString("duty_status");
                            if (strStatus.equals("1")) {
                                if (!strDuty_status.equals("1")) {
                                    // stopTimer(timer);
                                    mHandler.removeCallbacks(runnable);
                                    SharedPrefrence_Login.saveONOFF(LocationService.this, "0");
                                } else {
                                    // stopTimer(timer);
                                    mHandler.removeCallbacks(runnable);
                                    SharedPrefrence_Login.saveONOFF(LocationService.this, "0");
                                }
                            } else {
                                if (!strDuty_status.equals("1")) {
                                    //stopTimer(timer);
                                    mHandler.removeCallbacks(runnable);
                                    SharedPrefrence_Login.saveONOFF(LocationService.this, "0");
                                } else {
                                    //stopTimer(timer);
                                    mHandler.removeCallbacks(runnable);
                                    SharedPrefrence_Login.saveONOFF(LocationService.this, "0");
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
                        // Toast.makeText(Demo_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("seller_id", sellerID);
                params.put("latitude", LATTITUDE);
                params.put("longitude", LONGITUDE);
                params.put("duty_status", sStatus);
                return params;
            }
        };
        requestQueue.add(getRequest);
    }

    public boolean isGPSEnabled (Context mContext){
        locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }



}
