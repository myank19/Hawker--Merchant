package io.goolean.tech.hawker.merchant.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Constant.AppStatus;
import io.goolean.tech.hawker.merchant.Constant.ConnectionDetector;
import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.FCM.Config;
import io.goolean.tech.hawker.merchant.FCM.NotificationUtils;
import io.goolean.tech.hawker.merchant.Location.Location_Update;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;


public class Splash extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private ImageView imageView;
    private TextView textViewVersionInfo;
    private Animation animation1, animation2;
    private SharedPrefrence_Login sharedPrefrence_login;
    private static final String TAG = Splash.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private RequestQueue requestQueue;
    private Dialog dialog;
    private Button btnd_ok, btnd_cancel;
    private TextView tvd_message, tv_msg;
    private LocationManager locationManager;
    private Location_Update location_update;
    Button getBtnd_ok;
    AppUpdateManager appUpdateManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.sendUnsentReports();

        crashlytics.log("my message");



// To log a message to a crash report, use the following syntax:
        crashlytics.log("E/TAG: my message");

//       getBtnd_ok.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//
//           }
//       });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

//        getBtnd_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        initValue();
        funNotification();


        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
           //Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_LONG).show();
        }else{
            getVersionInfo();

        }

    }

    private void getVersionInfo() {
        String versionName = "";

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            func_CheckVersion(versionName, versionCode);
            //checkValidation();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textViewVersionInfo = (TextView) findViewById(R.id.tv_versioncode);
        textViewVersionInfo.setText(String.format("V" + versionName));
    }

    private void func_CheckVersion(final String versionName, final int versionCode) {
        final RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_APP_VERSION_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            if (jsoObject.getString("status").equals("0")) {
                                String message = jsoObject.getString("message");
                                String update_status = jsoObject.getString("update_status");
                                if (update_status.equals("1")) {
                                    // checkValidation();
                                    if (TextUtils.isEmpty(SharedPrefrence_Login.getUPDATE_STATUS())) {
                                        dialog = new Dialog(Splash.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_for_update);
                                        dialog.setCancelable(false);
                                        btnd_ok = dialog.findViewById(R.id.button_ok_id);
                                        tv_msg = dialog.findViewById(R.id.tv_info_id);
                                        btnd_cancel = dialog.findViewById(R.id.button_cancel_id);
                                        dialog.show();
                                        tv_msg.setText(message);
                                        btnd_ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int MY_REQUEST_CODE=101;
                                                 appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
                                                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                                appUpdateManager.startUpdateFlowForResult(
                                                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                                        appUpdateInfoTask,
                                                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                                        AppUpdateType.IMMEDIATE,
                                                        // The current activity making the update request.
                                                        this,
                                                        // Include a request code to later monitor this update request.
                                                        MY_REQUEST_CODE);
                                            }
                                        });
                                        btnd_cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                SharedPrefrence_Login.saveUpdateStatus(Splash.this, "1");
                                                checkValidation();
                                            }
                                        });
                                    } else if (SharedPrefrence_Login.getUPDATE_STATUS().equals("0")) {
                                        dialog = new Dialog(Splash.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_for_update);
                                        dialog.setCancelable(false);
                                        btnd_ok = dialog.findViewById(R.id.button_ok_id);
                                        tv_msg = dialog.findViewById(R.id.tv_info_id);
                                        btnd_cancel = dialog.findViewById(R.id.button_cancel_id);
                                        dialog.show();
                                        tv_msg.setText(message);
                                        btnd_ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int MY_REQUEST_CODE=101;
                                                 appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
                                                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                                appUpdateManager.startUpdateFlowForResult(
                                                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                                        appUpdateInfoTask,
                                                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                                        AppUpdateType.IMMEDIATE,
                                                        // The current activity making the update request.
                                                        this,
                                                        // Include a request code to later monitor this update request.
                                                        MY_REQUEST_CODE);
//                                                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
//                                                if (!url.startsWith("http://") && !url.startsWith("https://"))
//                                                    url = "http://" + url;
//                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                                startActivity(browserIntent);
                                            }
                                        });
                                        btnd_cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                SharedPrefrence_Login.saveUpdateStatus(Splash.this, "1");
                                                checkValidation();
                                            }
                                        });
                                    } else if (SharedPrefrence_Login.getUPDATE_STATUS().equals("1")) {
                                        checkValidation();
                                    }

                                } else if (update_status.equals("0")) {
                                    dialog = new Dialog(Splash.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_for_update);
                                    dialog.setCancelable(true);
                                    btnd_ok = dialog.findViewById(R.id.button_ok_id);
                                    tv_msg = dialog.findViewById(R.id.tv_info_id);
                                    btnd_cancel = dialog.findViewById(R.id.button_cancel_id);

                                    tv_msg.setText(message);
                                    btnd_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int MY_REQUEST_CODE=101;
                                             appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
                                            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                            appUpdateManager.startUpdateFlowForResult(
                                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                                    appUpdateInfoTask,
                                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                                    AppUpdateType.IMMEDIATE,
                                                    // The current activity making the update request.
                                                    this,
                                                    // Include a request code to later monitor this update request.
                                                    MY_REQUEST_CODE);
                                        }
                                    });
                                    btnd_cancel.setBackgroundResource(R.color.gray);
                                    btnd_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SharedPrefrence_Login.saveUpdateStatus(Splash.this, "0");
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                    dialog.show();
                                }
                            } else {
                                checkValidation();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
                        if (error.getClass().equals(TimeoutError.class)) {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "It took longer than expected to get the response from Server.",
                                    MessageConstant.toast_warning);
                        } else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Server Respond Error! Try Again Later",
                                    MessageConstant.toast_warning);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("version_name", versionName);
                params.put("version_code", String.valueOf(versionCode));

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);

    }


    private void turnGPSOn() {

        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true); // this is the key ingredient
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            toast("Success");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                status.startResolutionForResult(Splash.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            break;
                    }
                }
            });
        }

    }

    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            //log("Window has been closed");
        }
    }

    private void funNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    // Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        // displayFirebaseRegId();

    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId))
            Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Firebase Reg Id is not received yet!", Toast.LENGTH_SHORT).show();
    }
    // Fetches reg id from shared preferences
    // and displays on the screen

    private void initValue() {
        sharedPrefrence_login = new SharedPrefrence_Login(getApplicationContext());
        dialog = new Dialog(Splash.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout_from_device);
        dialog.setCancelable(true);
        btnd_ok = dialog.findViewById(R.id.button_ok_id);
        btnd_cancel = dialog.findViewById(R.id.button_cancel_id);
        tvd_message = dialog.findViewById(R.id.tv_info_id);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location_update = new Location_Update(getApplicationContext());
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    private void checkValidation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPrefrence_Login.getDataLogin(getApplicationContext());
                SharedPrefrence_Login.fun_getcheckOTP(getApplicationContext());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!SharedPrefrence_Login.getMhawker_code().equals("") && SharedPrefrence_Login.getcheckOTP().equals("1")) {
                            Intent i = new Intent(getApplicationContext(), Home.class);
                            finish();
                            startActivity(i);
                        } else if (!SharedPrefrence_Login.getMhawker_code().equals("") && SharedPrefrence_Login.getcheckOTP().equals("0") ||
                                SharedPrefrence_Login.getcheckOTP().equals("")) {
                            Intent i = new Intent(getApplicationContext(), Login.class);
                            finish();
                            startActivity(i);
                        } else if (SharedPrefrence_Login.getMhawker_code().equals("") && SharedPrefrence_Login.getcheckOTP().equals("0") ||
                                SharedPrefrence_Login.getcheckOTP().equals("")) {
                            Intent i = new Intent(getApplicationContext(), Login.class);
                            finish();
                            startActivity(i);
                        }
                    }
                }, 2500);

            }

        }, 1000);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
        } else {

            if (AppStatus.getInstance(this).isOnline()) {

                if (AppStatus.getInstance(this).isDataAvailable()) {

                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "You are online", MessageConstant.toast_success);
                    funNotification();
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // getVersionInfo();
                    } else {
                        turnGPSOn();
                    }
                } else {
                    Toast.makeText(this, "Data not available", Toast.LENGTH_SHORT).show();

                }

            } else {
                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);

            }
        }
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

}
