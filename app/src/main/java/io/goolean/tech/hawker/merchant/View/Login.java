package io.goolean.tech.hawker.merchant.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Constant.AppStatus;
import io.goolean.tech.hawker.merchant.Constant.ConnectionDetector;
import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;

import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Dialog.Dialog_;
import io.goolean.tech.hawker.merchant.FCM.Config;
import io.goolean.tech.hawker.merchant.FCM.NotificationUtils;
import io.goolean.tech.hawker.merchant.Location.GetLatLngActivity;
import io.goolean.tech.hawker.merchant.Location.Location_Update;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;
import io.goolean.tech.hawker.merchant.Translate.LocaleHelper;


public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText edtNumber;
    private Button btnSubmit;
    private RequestQueue requestQueue;
    private String device_id;
    private SharedPrefrence_Login sharedPrefrence_login;
    private View animateView;

    private static final String TAG = Splash.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public String regId;
    private Dialog dialog;
    private Button btnd_ok, btnd_cancel;
    private TextView tvd_message;
    private String STR_LOGOUT_CONFIRM_STATUS = "1";
    private LocationManager locationManager;
    boolean doubleBackToExitPressedOnce = false;
    private TextView textView_heading;
    private Dialog dialogOtherCityRequest, notifyDialog;
    private Location_Update location_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        if (savedInstanceState == null) {
            initValue();
            funNotification();
        }
    }

    private void initValue() {
        edtNumber = findViewById(R.id.edit_text_number);
        btnSubmit = findViewById(R.id.btn_submit_id);
        btnSubmit.setOnClickListener(this);
        animateView = findViewById(R.id.animate_view);
        textView_heading = findViewById(R.id.tv_heading1);
        textView_heading.setText("Signin & Register to continue" + "\n" + "साइन इन करें और जारी रखने के लिए पंजीकरण करें");
        SharedPrefrence_Login.getNumberData(getApplicationContext());
        SharedPrefrence_Login.getTokenS(getApplicationContext());
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        sharedPrefrence_login = new SharedPrefrence_Login(getApplicationContext());
        dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout_from_device);
        dialog.setCancelable(true);
        btnd_ok = dialog.findViewById(R.id.button_ok_id);
        btnd_cancel = dialog.findViewById(R.id.button_cancel_id);
        tvd_message = dialog.findViewById(R.id.tv_info_id);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location_update = new Location_Update(getApplicationContext());
        dialogOtherCityRequest = new Dialog(Login.this);
        dialogOtherCityRequest.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOtherCityRequest.setContentView(R.layout.dialog_message_info);
        dialogOtherCityRequest.setCanceledOnTouchOutside(false);
        dialogOtherCityRequest.setCancelable(false);
        dialogOtherCityRequest.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        notifyDialog = new Dialog(Login.this);
        notifyDialog.setContentView(R.layout.dialog_notify_info);
        notifyDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        notifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        notifyDialog.setCancelable(false);


    }

    private void Runtime_Permission() {

        Dexter.withActivity(Login.this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                location_update = new Location_Update(Login.this);
                                if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                                    funNotification();
                                } else {
                                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
                                }
                            } else {
                                //  Toast.makeText(HomeActivity.this, "GPS STATUS", Toast.LENGTH_SHORT).show();
                                turnGPSOn();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void funNotification() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                }
            }
        };
        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        SharedPrefrence_Login.saveToken(Login.this, regId);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId)) {
            //  Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Firebase Reg Id is not received yet!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_id:
                funNotification();
                if (TextUtils.isEmpty(edtNumber.getText().toString().trim())) {
                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Enter mobile number", MessageConstant.toast_warning);

                } else if (edtNumber.getText().toString().length() < 10) {
                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Enter 10 digit mobile number", MessageConstant.toast_warning);

                } else if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                    funNotification();
                    if (TextUtils.isEmpty(location_update.city)) {
                        Toast.makeText(this, "Gps is being start properly", Toast.LENGTH_SHORT).show();
                    } else {
                        //  Toast.makeText(this, ""+location_update.city, Toast.LENGTH_SHORT).show();
                        func_Login(STR_LOGOUT_CONFIRM_STATUS, regId, Urls.URL_LOGIN);
                    }
                } else {
                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
                }


                break;
        }
    }

    private void func_Login(final String STR_LOGOUT_CONFIRM_STATUS, final String regId, String urlLogin) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //if everything is fine
        final ProgressDialog _progressDialog = ProgressDialog.show(
                Login.this, "", "Please wait...", true, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Login.this.cancel(true);
                        finish();
                    }
                }
        );
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            Log.d("merchentloginres",response);
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            _progressDialog.dismiss();
                            if (jsoObject.getString("status").equals("0")) {
                                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Server not respond! Try Again Later ", MessageConstant.toast_warning);
                            } else if (jsoObject.getString("status").equals("1")) {
                                String str_type = jsoObject.getString("type");
                                if (str_type.equalsIgnoreCase("seller")) {
                                    if (jsoObject.getString("active_status").equals("1")) {
                                        String hawker_code = jsoObject.getString("hawker_code");
                                        String name = jsoObject.getString("name");
                                        String user_type = jsoObject.getString("type");
                                        String Show_status = jsoObject.getString("status");
                                        //int seller_timer = jsoObject.getInt("seller_timer");
                                        //Log.d("seller_timerr","seller: "+seller_timer);
                                        SharedPrefrence_Login.saveDataLogin(getApplicationContext(), edtNumber.getText().toString().trim(),
                                                device_id, regId, name, hawker_code, str_type, user_type, Show_status,0);
                                        startActivity(new Intent(getApplicationContext(), Otp.class));
                                        finish();
                                        finishAffinity();
                                    }
                                }
                            } else if (jsoObject.getString("status").equals("2")) {
                                _progressDialog.dismiss();
                                notifyDialog.show();
                                notifyDialog("Request for Our Service. Press Ok.\n हमारी सेवा के लिए अनुरोध करें। ओके दबाएँ।");

                            } else if (jsoObject.getString("status").equals("3")) {
                                String message = jsoObject.getString("msg");
                                dialog.show();
                                tvd_message.setText(message + "\n" + "क्या आप इस मोबाइल में लॉगिन करना चाहते हैं। कृपया दूसरे मोबाइल से पहले लॉग आउट करें।");
                                btnd_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                                            dialog.dismiss();
                                            func_Login(STR_LOGOUT_CONFIRM_STATUS, regId, Urls.URL_LOGOUT_FROM_OTHER_DEVICE);
                                        } else {
                                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
                                        }
                                    }
                                });
                                btnd_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            } else if (jsoObject.getString("status").equals("4")) {
                                _progressDialog.dismiss();
                                notifyDialog.show();
                                notifyDialog("We are not providing service in your area yet. Request for Our Service. Press Ok. " + "\n" + "अभी हम आपके क्षेत्र में सेवा प्रदान नहीं कर रहे हैं। हमारी सेवा के लिए अनुरोध। ओके दबाएँ।");


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progressDialog.dismiss();
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
                params.put("status", STR_LOGOUT_CONFIRM_STATUS);
                params.put("mobile_no", edtNumber.getText().toString().trim());
                params.put("device_id", device_id);
                params.put("notification_id", SharedPrefrence_Login.getTokenS());
                params.put("city", location_update.city);
                params.put("sPin", location_update.postalCode);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }

    private void notifyDialog(String message) {
        TextView tv_notify_id = notifyDialog.findViewById(R.id.tv_notify_id);
        tv_notify_id.setText(message);
        Button button_ok_id = notifyDialog.findViewById(R.id.button_ok_id);
        button_ok_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDialog.dismiss();
                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "हम कुछ समय बाद आपसे संपर्क करेंगे। " + "\n" + "We will contact with you after sometime.", MessageConstant.toast_success);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 3500);
            }
        });
    }

    private void cancel(boolean b) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
            funNotification();
        } else {
            //  CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection", MessageConstant.toast_warning);
        }
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
        } else {
            if (AppStatus.getInstance(this).isOnline()) {
                funNotification();
                Runtime_Permission();
            } else {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getApplicationContext(),"Loc On",Toast.LENGTH_SHORT).show();

                } else {
                    turnGPSOn();
                    Toast.makeText(getApplicationContext(),"Loc Off",Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    public void turnGPSOn() {
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
                            //toast("GPS is not on");
                            try {
                                status.startResolutionForResult(Login.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*Setting_______________________________*/

    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Login.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Runtime_Permission();
        }
    }
}
