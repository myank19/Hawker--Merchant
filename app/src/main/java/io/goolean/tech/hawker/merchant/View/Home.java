 package io.goolean.tech.hawker.merchant.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.goolean.tech.hawker.merchant.Constant.AppController;
import io.goolean.tech.hawker.merchant.Constant.AppStatus;
import io.goolean.tech.hawker.merchant.Constant.ConnectionDetector;
import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;
import io.goolean.tech.hawker.merchant.Constant.StringConstant;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Dialog.Dialog_;
import io.goolean.tech.hawker.merchant.Lib.FancyToast.FancyToast;
import io.goolean.tech.hawker.merchant.Lib.Fusion_Map.Draw_Polyline.TaskLoadedCallback;
import io.goolean.tech.hawker.merchant.Location.Location_Update;
import io.goolean.tech.hawker.merchant.Model.ShareLocationModel;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Services.LocationService;
import io.goolean.tech.hawker.merchant.Services.SwitchButtonListener;
import io.goolean.tech.hawker.merchant.Services.SwitchButtonListenerON;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;
import io.goolean.tech.hawker.merchant.Translate.App;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;
import static io.goolean.tech.hawker.merchant.Translate.App.CHANNEL_ID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, TaskLoadedCallback, ShareLocationAdapter.CallClick, ShareLocationAdapter.NavigateClick, ShareLocationAdapter.CancelClick,
        ShareLocationAdapter.CompleteClick {
    //Goolean@123#
    private ImageView iv_navDrawer;
    //testing
    private DrawerLayout drawer;
    private GoogleMap mMap;
    private Switch swActiveInactive;
    private String totalClicks;
    boolean bSwitch = true;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient googleApiClient;
    private Polyline currentPolyline;
    private ImageView imv_drawer_id;
    boolean bDrawer = true;
    private static final String Job_Tag = "my_job_tag";
    private FirebaseJobDispatcher jobDispatcher;
    private String strId, strSales_id, strName, strBusiness_name, strEmail_id,
            strImage_url, strAddress, strMobile_no, strCheckDuty_status, strTota_Call, str_cityStaus, str_Citymsg, str_version_status, str_status;
    private Location_Update location_update;
    private String strStatus, strDuty_status, android_id, strActive_status, strActive_msg;
    private LocationManager lm;
    private String strSeller_id, strShop_name, strShop_state, strSho_city, strShop_address, strShop_area,
            strShop_no, strShop_pin, strTotalCall;
    private CircularImageView imageView;
    CircularImageView profile_img;
    private String logout_status = "";
    LocationManager locationManager;
    Context mcontext;
    private TextView tvName, tvBusinessID, tvBusinessName, tvTotalCall, tv_Info, tv_total_call_id1;
    private String TAG = "MapsActivity";
    AppController appController;
    private RequestQueue requestQueue;
    private ProgressDialog _progressDialog;
    NotificationChannel mChannel;
    public String strDutyStatus = "0";
    // private LinearLayout linearLayoutCall;
    String versionName = "";
    int versionCode;
    private Dialog dialog;

    private Button btn_info_ok;
    private TextView tv_dutyhindi, tv_dutyenglish, tv_callus;
    private SwitchButtonListener switchButtonListener = new SwitchButtonListener();
    private SwitchButtonListenerON switchButtonListenerON = new SwitchButtonListenerON();

    private RecyclerView rv_upcomingLocation;
    private ArrayList<ShareLocationModel> locationModelArrayList;
    private ShareLocationModel shareLocationModel;
    private ShareLocationAdapter shareLocationAdapter;
    private LinearLayout ll_recycler_layout;
    private TextView tv_notFound, tv_total_call_id2;
    String totalCategoryClick;
    private ImageView slide_image;
    private CardView reward_cardView;
    private TextView tvReferral, text1, txt2, txtv2;
    boolean checkStatus = false;
    AlertDialog.Builder builder;
    AppUpdateManager appUpdateManager;
    int RequestUpdate = 1;
    //private static final RC_APP_UPDATE=100;


    //private static final int = RC_APP_UPDATE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appUpdateManager = AppUpdateManagerFactory.create(Home.this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                result,
                                AppUpdateType.IMMEDIATE,
                                Home.this,
                                RequestUpdate);
                    }
                    catch (IntentSender.SendIntentException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        //checkUpdate();
        builder = new AlertDialog.Builder(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SharedPrefrence_Login.getDataONOFF(getApplicationContext());
        SharedPrefrence_Login.getDataLogin(getApplicationContext());

        tv_total_call_id1 = (TextView) findViewById(R.id.tv_total_call_id1);

        tv_total_call_id2 = (TextView) findViewById(R.id.tv_total_call_id2);

        text1 = (TextView) findViewById(R.id.text1);

        txt2 = (TextView) findViewById(R.id.txt2);

        txtv2 = (TextView) findViewById(R.id.txtv2);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        MenuItem target = menu.findItem(R.id.nav_history);

        if (SharedPrefrence_Login.getmShow_status().equalsIgnoreCase("1")) {
            target.setVisible(true);
        } else {
            target.setVisible(false);
        }

        //

        navigationView.setNavigationItemSelectedListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location_update = new Location_Update(getApplicationContext());
        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Log.d("locationStatus",""+isGPSEnabled(this));

    }



    @Override
    protected void onStop() {
        super.onStop();
        //removeInstallStateUpdateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if(result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                result,
                                AppUpdateType.IMMEDIATE,
                                Home.this,
                                RequestUpdate);
                    }
                    catch (IntentSender.SendIntentException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });






    }

    private void openDrawer() {
        drawer.openDrawer(Gravity.LEFT);
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void funRuntimePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            //Toast.makeText(getApplicationContext(),"Location on",Toast.LENGTH_SHORT).show();
                             initValue();
                            if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                                if (TextUtils.isEmpty(location_update.city)) {
                                    //Toast.makeText(getApplicationContext(),"successs",Toast.LENGTH_LONG).show();
                                    //funRuntimePermission();

                                } else {
                                    initValue();
                                    fun_Profile(SharedPrefrence_Login.getMhawker_code(), versionCode, versionName);
                                    fun_hindi();
                                }
                            } else {
                                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
                            }
                            //  fun_Active_Inactive(SharedPrefrence_Login.getMhawker_code(),SharedPrefrence_Login.getPdevice_id(),appController.getCity());
                        } else {
                            //Toast.makeText(getApplicationContext(),"Location off",Toast.LENGTH_SHORT).show();
                            turnGPSOn();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        /* ... */
                        showSettingsDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        /* ... */
                    }
                }).check();

    }


    private void initValue() {
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        swActiveInactive = findViewById(R.id.swActiveInactive_id);
        imv_drawer_id = findViewById(R.id.imv_drawer_id);
        tvName = findViewById(R.id.tv_name_id);
        tvBusinessID = findViewById(R.id.tv_number_id);
        tvBusinessName = findViewById(R.id.tv_Business_name);
        tvTotalCall = findViewById(R.id.tv_total_call_id);
        // linearLayoutCall = findViewById(R.id.ll_call_us_id);
        tv_dutyhindi = findViewById(R.id.tv_duty_on_hindi);
        tv_dutyenglish = findViewById(R.id.tv_duty_on_english);
        //tv_callus = findViewById(R.id.tv_help_id);
        rv_upcomingLocation = findViewById(R.id.rv_upcomingLocation);
        ll_recycler_layout = findViewById(R.id.ll_recycler_layout);
        tv_notFound = findViewById(R.id.tv_notFound);

        // Refer the ImageView like this
        slide_image = (ImageView) findViewById(R.id.img);
        reward_cardView = findViewById(R.id.reward_cardView);
        //tvReferral = findViewById(R.id.tvReferral);

// Load the animation like this
        Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        Animation animSlideleft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);

// Start the animation like this
        startAnimation(animSlide, animSlideleft);


        tvTotalCall.setText("0");
        //tvReferral.setText(SharedPrefrence_Login.getPnumber());
        //tv_callus.setText("हमें फोन करें"+ "\n" + "Call Us" + "\n");
        dialog = new Dialog(Home.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message_info);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        tv_Info = dialog.findViewById(R.id.tv_info_id);
        btn_info_ok = dialog.findViewById(R.id.button_ok_id);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        swActiveInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                        //Toast.makeText(getApplicationContext(),"Location on",Toast.LENGTH_SHORT).show();
                        if (bSwitch == true) {
                            if (!location_update.LATTITUDE.equals("0.0")) {
                                Log.i("Duty Status", "ON");
                                strDutyStatus = "1";
                                fun_DutyONOFF(strDutyStatus, SharedPrefrence_Login.getMhawker_code());
                            } else {
                                if (bSwitch == true) {
                                    swActiveInactive.setChecked(false);
                                } else {
                                    swActiveInactive.setChecked(true);
                                }
                                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Your GPS is being start properly.", MessageConstant.toast_warning);
                            }
                        } else {
                            Log.i("Duty Status", "OFF");
                            strDutyStatus = "0";
                            fun_DutyONOFF(strDutyStatus, SharedPrefrence_Login.getMhawker_code());
                        }
                    } else {
                        if (bSwitch == true) {
                            swActiveInactive.setChecked(false);
                        } else {
                            swActiveInactive.setChecked(true);
                        }
                        CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
                    }
                } else {
                    if (bSwitch == true) {
                        swActiveInactive.setChecked(false);
                    } else {
                        swActiveInactive.setChecked(true);
                    }
                    //Toast.makeText(getApplicationContext(),"Location off",Toast.LENGTH_SHORT).show();
                    //offLocationNotification();
                    turnGPSOn();
                }
            }
        });

        if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
            Log.i("Duty Status", "CHECK ON/ OFF");
            strDutyStatus = "2";
            fun_DutyONOFF(strDutyStatus, SharedPrefrence_Login.getMhawker_code());
        } else {
            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
        }

        imv_drawer_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bDrawer == true) {
                    openDrawer();
                    bDrawer = true;
                } else {
                    closeDrawer();
                    bDrawer = true;
                }
            }
        });


        String sStatus = SharedPrefrence_Login.getmShow_status();
        if (sStatus.equals("1")) {
            ll_recycler_layout.setVisibility(View.VISIBLE);
            locationModelArrayList = new ArrayList<>();
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            MyRecycler recyclerview = new MyRecycler(getApplicationContext());
            recyclerview.enableVersticleScroll(false);
//            linearLayoutManager = new LinearLayoutManager(getApplicationContext()) {
//                @Override
//                public boolean canScrollVertically() {
//                    return false;
//                }
//            };
            LinearLayoutManager layoutManager = new
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerview.setLayoutManager(layoutManager);
            //rv_upcomingLocation.setLayoutManager(linearLayoutManager);
            //CustomGridLayoutManager customLayoutManager = new CustomGridLayoutManager(getApplicationContext());
            recyclerview.setLayoutManager(layoutManager);

            shareLocationAdapter = new ShareLocationAdapter(getApplicationContext(), locationModelArrayList);
            if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                func_ShareLocationInfo();
            } else {
                FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED, FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
            }
        } else {
            ll_recycler_layout.setVisibility(View.GONE);
        }

        reward_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet()==true){
//                    fungetAmountDetail(SharedPrefrence_Login.getPnumber());
//                }else {
//                    FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED,FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
//                }

            }
        });


    }

    private void offLocationNotification() {

        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_DUTY_ONOFF,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseLoconoff",response);
                        if (response != null && response.length() > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jObj = jsonObject.getJSONObject("data");
                                String status = jObj.optString("status");
                                String message = jObj.optString("message");
                                createNotificationON(getApplicationContext());
                                //startJob();
                                //Toast.makeText(getApplicationContext(),"locationn"+message,Toast.LENGTH_SHORT).show();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //parseData(response);
                        } else {

                            Toast.makeText(Home.this, "Something is wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_LONG).show();
                        Log.d("errorr",""+error.getMessage());


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("device_id", SharedPrefrence_Login.getPdevice_id());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
        stringRequest.setShouldCache(false);
        App.getInstance().addToRequestQueue(stringRequest, Urls.URL_DUTY_ONOFF);

        /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(StringConstant.REPEAT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);*/

    }

    private void cancel(boolean b) {
    }


    private void fungetAmountDetail(final String mobile_number) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        _progressDialog = ProgressDialog.show(
                Home.this, "", "Please wait...", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Home.this.cancel(true);
                        finish();
                    }
                }
        );


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_REFERRAL_MONEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && response.length() > 0) {
                            _progressDialog.dismiss();
                            parseData(response);
                        } else {
                            _progressDialog.dismiss();
                            Toast.makeText(Home.this, "Something is wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progressDialog.dismiss();
                        if (error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(Home.this, StringConstant.RESPONSE_FAILURE_MORE_TIME, Toast.LENGTH_LONG).show();
                        } else if (error.getClass().equals(ServerError.class)) {
                            Toast.makeText(Home.this, StringConstant.RESPONSE_FAILURE_NOT_RESPOND, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Home.this, StringConstant.RESPONSE_FAILURE_SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", mobile_number);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(StringConstant.REPEAT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

    }

    private void parseData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject jsoObject = new JSONObject(obj.getString("data"));
            String sStatus = jsoObject.getString("status");
            if (sStatus.equals("1")) {
                funshowAmount(jsoObject.getString("count"), jsoObject.getString("total_count"));
            } else if (sStatus.equals("0")) {
                funshowAmount(jsoObject.getString("count"), jsoObject.getString("total_count"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG_Error", e + "");
        }
    }


    private void funshowAmount(final String sCount, final String sTotalcount) {
        final BottomSheetDialog dialog = new BottomSheetDialog(Home.this);
        dialog.setContentView(R.layout.layout_amount);
        ImageView img_cancel_id = dialog.findViewById(R.id.img_cancel_id);
        Button btnReceived = dialog.findViewById(R.id.btnReceived);
        final TextView tvAmount = dialog.findViewById(R.id.tvAmount);
        final TextView tvTotalCount = dialog.findViewById(R.id.tvTotalCount);
        final CheckBox cbReceivedStatus = dialog.findViewById(R.id.cbReceivedStatus);
        tvAmount.setText(getResources().getString(R.string.Rs) + " " + sCount);
        //tvTotalCount.setText(sTotalcount);
        tvTotalCount.setText("आज आपने " + sTotalcount + " ग्राहक पंजीकृत किए हैं!\nToday you have registered " + sTotalcount + " Customer.");
        img_cancel_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (Integer.parseInt(sCount) > 0) {
            btnReceived.setClickable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btnReceived.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C27B0")));
            } else {
                Drawable drawable = new ColorDrawable(Color.parseColor("#9C27B0"));
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.parseColor("#9C27B0"));
                btnReceived.setBackground(drawable);
            }
        } else {
            btnReceived.setClickable(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btnReceived.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7A7A7A")));
            } else {
                Drawable drawable = new ColorDrawable(Color.parseColor("#7A7A7A"));
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.parseColor("#7A7A7A"));
                btnReceived.setBackground(drawable);
            }
        }
        cbReceivedStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus = isChecked;
                // Toast.makeText(Home.this, ""+checkStatus, Toast.LENGTH_SHORT).show();
            }
        });

        btnReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(sCount) > 0) {
                    if (checkStatus == false) {
                        CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please checked first",
                                MessageConstant.toast_warning);
                    } else {
                        if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                            funReceivedAmountDetail(SharedPrefrence_Login.getPnumber(), dialog, sCount);
                        } else {
                            FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED, FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
                        }
                    }
                } else {
                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "You have low balance.",
                            MessageConstant.toast_warning);
                }
            }
        });
        dialog.show();
    }


    private void funReceivedAmountDetail(final String smobile_number, final BottomSheetDialog bottomSheetDialog,
                                         final String scount) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_RECEIVED_REFERRAL_MONEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && response.length() > 0) {
                            parseReceivedData(response, bottomSheetDialog);
                        } else {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(Home.this, StringConstant.RESPONSE_FAILURE_MORE_TIME, Toast.LENGTH_LONG).show();
                        } else if (error.getClass().equals(ServerError.class)) {
                            Toast.makeText(Home.this, StringConstant.RESPONSE_FAILURE_NOT_RESPOND, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Home.this, StringConstant.RESPONSE_FAILURE_SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", smobile_number);
                params.put("money", scount);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(StringConstant.REPEAT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

    }

    private void parseReceivedData(String response, final BottomSheetDialog sheetDialog) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject jsoObject = new JSONObject(obj.getString("data"));
            String sStatus = jsoObject.getString("status");
            if (sStatus.equals("1")) {
                sheetDialog.dismiss();
                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), jsoObject.getString("message"),
                        MessageConstant.toast_success);
            } else if (sStatus.equals("0")) {
                sheetDialog.dismiss();
                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), jsoObject.getString("message"),
                        MessageConstant.toast_error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG_Error", e + "");
        }
    }

    public void startAnimation(final Animation animSlide, final Animation animSlideleft) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                slide_image.startAnimation(animSlideleft);
                // slide_image.startAnimation(animSlide);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /*Setting_______________________________*/

    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Home.this);
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


    //////////////////////////////////////////////////////////////////////////////////

    private void showSettingsDialogs() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Home.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettingss();
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
    private void openSettingss() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 102);
    }


    public void startJob() {
        final Intent intent = new Intent(this, LocationService.class);
        intent.putExtra("inputData", "Location service running in background");
        Thread thread = new Thread() {
            @Override
            public void run() {
                startService(intent);
            }
        };
        thread.start();

        ContextCompat.startForegroundService(this, intent);
        //Toast.makeText(this, "DUTY ON", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the App/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_callus) {
            funcallus();
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(getApplicationContext(), ShareLocationHistory.class));
        }
//        else if (id == R.id.navReferralMoneyReceivedHistory) {
//           //startActivity(new Intent(getApplicationContext(),ReferralMoneyReceivedHistory.class));
//        }
        else if (id == R.id.nav_Logout) {

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                turnGPSOn();
            } else if (!AppStatus.getInstance(this).isOnline()) {
                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
            } else {

                builder.setMessage("Do you want to exit")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logout_status = "LogOut";
                                //finishAffinity();
                                fun_DutyONOFF("0", SharedPrefrence_Login.getMhawker_code());
                                //System.exit(0);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.show();

            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void funcallus() {
        Dexter.withActivity(Home.this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + "07827958484"));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showSettingsDialogs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    //=========================================================================================================


    private void fun_Profile(final String hawker_code, final int versionCode, final String versionName) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_SELLER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            str_status = jsoObject.getString("status");
                            if (str_status.equals("0")) {
                                //jobDispatcher.cancelAll();
                                stopService(new Intent(Home.this, LocationService.class));
                                SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                                finish();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            } else {
                                str_cityStaus = jsoObject.getString("city_status");
                                str_version_status = jsoObject.getString("version_status");
                                if (str_version_status.equals("1")) {
                                    if (str_cityStaus.equals("0")) {
                                        str_Citymsg = jsoObject.getString("city_message");
                                        Dialog_.getInstanceDialog(Home.this).dialog_MessageAppFinish(Home.this,
                                                "Your current location is " + location_update.city + ". We are not providing service in this area" + "\n" + "आपका अभी का स्थान " + location_update.city + " है| हम इस क्षेत्र में सेवा प्रदान नहीं कर रहे हैं");

                                    } else if (str_cityStaus.equals("1")) {
                                        strId = jsoObject.getString("id");
                                        strSeller_id = jsoObject.getString("hawker_code");
                                        strName = jsoObject.getString("name");
                                        strImage_url = jsoObject.getString("image_url");
                                        strBusiness_name = jsoObject.getString("business_name");
                                        strCheckDuty_status = jsoObject.getString("duty_status");
                                        strTotalCall = jsoObject.getString("total_call");
                                        totalClicks = jsoObject.getString("totalclick");
                                        totalCategoryClick = jsoObject.getString("totalCategoryclick");
                                        tv_total_call_id1.setText(totalClicks);
                                        tv_total_call_id2.setText(totalCategoryClick);
                                        //        Picasso.with(getApplicationContext()).load(strImage_url).centerCrop().fit().into(imageView);
                                        tvName.setText(strName);
                                        tvTotalCall.setText(strTotalCall);
                                        //check here
                                        tvBusinessID.setText("Phone Number: " + SharedPrefrence_Login.getPnumber());
                                        tvBusinessName.setText(strBusiness_name);

                                        if (strCheckDuty_status.equals("0")) {
                                            swActiveInactive.setChecked(false);
                                            //  swActiveInactive.setText("Duty OFF");
                                            tv_dutyenglish.setText("Duty OFF");
                                            tv_dutyhindi.setText("ड्यूटी बंद है");
                                            // jobDispatcher.cancelAll();
                                            stopService(new Intent(Home.this, LocationService.class));
                                            bSwitch = true;
                                        } else {
                                            swActiveInactive.setChecked(true);
                                            //    swActiveInactive.setText("Duty ON");
                                            tv_dutyenglish.setText("Duty ON");
                                            tv_dutyhindi.setText("ड्यूटी चालू है");
                                            startJob();
                                            bSwitch = false;
                                        }
                                    }
                                } else {
                                    dialog.show();
                                    tv_Info.setText("Update your application" + "\n" + "अपने एप्लिकेशन को अपडेट करें");
                                    btn_info_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                                            if (!url.startsWith("http://") && !url.startsWith("https://"))
                                                url = "http://" + url;
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(browserIntent);
                                        }
                                    });
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
                        //     _progressDialog.dismiss();
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
                params.put("hawker_code", hawker_code);
                params.put("version_code", String.valueOf(versionCode));
                params.put("version_name", versionName);
                if (location_update.city.equalsIgnoreCase("")) {
                    params.put("city", "Gurugram");
                } else {
                    params.put("city", location_update.city);
                }
                if (location_update.postalCode.equalsIgnoreCase("")) {
                    params.put("sPin", "122001");
                } else {
                    params.put("sPin", location_update.postalCode);
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }


    private void fun_hindi() {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.URL_HINDI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            String h1 = data.getString("H1");
                            String e1 = data.getString("E1");
                            String e2 = data.getString("E2");
                            String e3 = data.getString("E3");
                            String h2 = data.getString("H2");
                            String h3 = data.getString("H3");
                            text1.setText(h1 + "\n" + e1);
                            txt2.setText(h2 + "\n" + e2);
                            txtv2.setText(h3 + "\n" + e3);
//
//
//                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //     _progressDialog.dismiss();
                        if (error.getClass().equals(TimeoutError.class)) {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "It took longer than expected to get the response from Server.",
                                    MessageConstant.toast_warning);
                        } else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Server Respond Error! Try Again Later",
                                    MessageConstant.toast_warning);
                        }
                    }
                }) {

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }


    private void fun_DutyONOFF(final String sStatus, final String hawker_code) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //fun_progressBar();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_DUTY_ON_OFF_SELLER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus = jsoObject.getString("status");
                            strActive_status = jsoObject.getString("active_status");
                            strActive_msg = jsoObject.getString("active_message");

                            if (strActive_status.equals("0")) {
                                /*Rejected*/
                                Dialog_.getInstanceDialog(Home.this).dialog_MessageAppStop(getApplicationContext(), strActive_msg);
                                swActiveInactive.setChecked(false);
                                stopService(new Intent(Home.this, LocationService.class));

                            } else if (strActive_status.equals("1")) {
                                /*Approved*/
                                strDuty_status = jsoObject.getString("duty_status");
                                if (strStatus.equals("1")) {

                                    if (strDuty_status.equals("1")) {
                                        SharedPrefrence_Login.saveONOFF(getApplicationContext(), "1");
                                        createNotificationON(getApplicationContext());
                                        startJob();
                                        swActiveInactive.setChecked(true);
                                        tv_dutyenglish.setText("Duty ON");
                                        tv_dutyhindi.setText("ड्यूटी चालू है");
                                        bSwitch = false;
                                    } else {
                                        if (logout_status.equals("LogOut")) {
                                            SharedPrefrence_Login.saveONOFF(getApplicationContext(), "0");
                                            funLogout(SharedPrefrence_Login.getMhawker_code());
                                        } else {
                                            SharedPrefrence_Login.saveONOFF(getApplicationContext(), "0");
                                            tv_dutyenglish.setText("Duty OFF");
                                            tv_dutyhindi.setText("ड्यूटी बंद है");
                                            swActiveInactive.setChecked(false);
                                            bSwitch = true;
                                            stopService(new Intent(Home.this, LocationService.class));
                                        }
                                    }
                                }

                            } else if (strActive_status.equals("2")) {
                                Dialog_.getInstanceDialog(Home.this).dialog_MessageAppStop(getApplicationContext(), strActive_msg);
                                swActiveInactive.setChecked(false);
                                stopService(new Intent(Home.this, LocationService.class));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                params.put("hawker_code", hawker_code);
                params.put("longitude", location_update.LONGITUDE);
                params.put("latitude", location_update.LATTITUDE);
                params.put("duty_status", sStatus);
                params.put("notification_id", SharedPrefrence_Login.getPnotification_id());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);

    }

    private void createNotificationON(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Hawker", NotificationManager.IMPORTANCE_HIGH);
        }
        Intent notificationIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_business)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        Intent dutyIntent = new Intent("action.cancel.notification");
        PendingIntent pendingDutyIntent = PendingIntent.getBroadcast(context, 0, dutyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        //the intent that is started when the notification is clicked (works)
        notification.contentView = notificationView;
        notification.contentIntent = pendingDutyIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationView.setOnClickPendingIntent(R.id.closeDuty, pendingDutyIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(1, notification);
        } else {
            mNotificationManager.notify(1, notification);
        }


        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("action.cancel.notification");
        registerReceiver(switchButtonListener, ifilter);
    }


    private void createNotificationOFF(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Hawker", NotificationManager.IMPORTANCE_HIGH);
        }
        Intent notificationIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_business)
                .setContentIntent(pendingIntent)
                .build();
        Intent dutyIntent = new Intent("dutyON");
        PendingIntent pendingDutyIntent = PendingIntent.getBroadcast(context, 0, dutyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.notification_layout_1);
        //the intent that is started when the notification is clicked (works)
        notification.contentView = notificationView;
        notification.contentIntent = pendingDutyIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationView.setOnClickPendingIntent(R.id.closeDuty, pendingDutyIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(1, notification);
        } else {
            mNotificationManager.notify(1, notification);
        }
        IntentFilter ifilters = new IntentFilter();
        ifilters.addAction("dutyON");
        registerReceiver(switchButtonListenerON, ifilters);


    }

    private void createNotificationOFFf(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Hawker", NotificationManager.IMPORTANCE_HIGH);
        }
        Intent notificationIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_business)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        Intent dutyIntent = new Intent("action.cancel.notification");
        PendingIntent pendingDutyIntent = PendingIntent.getBroadcast(context, 0, dutyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.notification_layout_1);
        //the intent that is started when the notification is clicked (works)
        notification.contentView = notificationView;
        notification.contentIntent = pendingDutyIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationView.setOnClickPendingIntent(R.id.closeDuty, pendingDutyIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(1, notification);
        } else {
            mNotificationManager.notify(1, notification);
        }


        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("action.cancel.notification");
        registerReceiver(switchButtonListener, ifilter);


    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void funLogout(final String hawker_code) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        // fun_progressBar();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_SELLER_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus = jsoObject.getString("status");
                            if (strStatus.equals("1")) {
                                stopService(new Intent(Home.this, LocationService.class));
                                SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                finish();
                                finishAffinity();
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    _progressDialog.dismiss();
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
                params.put("hawker_code", hawker_code);
                params.put("longitude", location_update.LONGITUDE);
                params.put("latitude", location_update.LATTITUDE);
                params.put("device_id", SharedPrefrence_Login.getPdevice_id());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }

    public void funLogoutSSS(final Context context, final String hawker_code, final String lat, final String Lon) {
        this.mcontext = context;
        final RequestQueue requestQueue = VolleySingleton.getInstance(mcontext).getRequestQueue();
        StringRequest getRequest = new StringRequest(Request.Method.POST, Urls.URL_SELLER_LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //       _progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus = jsoObject.getString("status");
                            if (strStatus.equals("1")) {
                                Toast.makeText(mcontext, "Logout Success", Toast.LENGTH_SHORT).show();
                                SharedPrefrence_Login.ClearSharedPrefrenc(mcontext);
                                Intent i = new Intent(mcontext, Login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                mcontext.startActivity(i);
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mcontext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hawker_code", hawker_code);
                params.put("longitude", lat);
                params.put("latitude", Lon);
                params.put("device_id", SharedPrefrence_Login.getPdevice_id());
                return params;
            }
        };
        requestQueue.add(getRequest);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                initValue();
                fun_Profile(SharedPrefrence_Login.getMhawker_code(), versionCode, versionName);
                fun_hindi();

            }



            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 101) {
            funRuntimePermission();
        } else if (requestCode == 102) {
        }
    }




    public void turnGPSOn() {


        //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();

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
                            //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //toast("GPS is not on");
                            try {
                                status.startResolutionForResult(Home.this, 1000);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {


//        initValue();
//        fun_Profile(SharedPrefrence_Login.getMhawker_code(), versionCode, versionName);
//        fun_hindi();


    }


    private void func_ShareLocationInfo() {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_CUSTOMERS_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && response.length() > 0) {
                            try {
                                locationModelArrayList.clear();
                                JSONObject obj = new JSONObject(response);
                                String strFav = obj.getString("data");
                                JSONArray jsonArrayFav = new JSONArray(strFav);
                                for (int i = 0; i < jsonArrayFav.length(); i++) {
                                    JSONObject jsonObjectFav = jsonArrayFav.getJSONObject(i);
                                    if (jsonObjectFav.getString("status").equals("1")) {
                                        tv_notFound.setVisibility(View.GONE);
                                        shareLocationModel = new ShareLocationModel();
                                        shareLocationModel.setCustomer_mobile_no(jsonObjectFav.getString("customer_mobile_no"));
                                        shareLocationModel.setLatitude(jsonObjectFav.getString("latitude"));
                                        shareLocationModel.setLongitude(jsonObjectFav.getString("longitude"));
                                        shareLocationModel.setLocation_name(jsonObjectFav.getString("location_name"));
                                        shareLocationModel.setCustomer_name(jsonObjectFav.getString("customer_name"));
                                        shareLocationModel.setDate_time(parseDateToddMMyyyy(jsonObjectFav.getString("date_time")));
                                        locationModelArrayList.add(shareLocationModel);
                                    } else {
                                        tv_notFound.setVisibility(View.VISIBLE);
                                    }
                                }

                                rv_upcomingLocation.setAdapter(shareLocationAdapter);
                                shareLocationAdapter.OnCallClickMethod(Home.this);
                                shareLocationAdapter.OnNavigateClickMethod(Home.this);
                                shareLocationAdapter.OnCompleteClickMethod(Home.this);
                                shareLocationAdapter.OnCancelClickMethod(Home.this);
                                shareLocationAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), StringConstant.RESPONSE_FAILURE_MORE_TIME,
                                    MessageConstant.toast_warning);
                        } else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), StringConstant.RESPONSE_FAILURE_NOT_RESPOND,
                                    MessageConstant.toast_warning);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("hawker_code", SharedPrefrence_Login.getMhawker_code());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }

    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            //log("Window has been closed");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //Toast.makeText(getApplicationContext(),"Location On",Toast.LENGTH_SHORT).show();
                if (!AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection" + "\n" + "आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
                } else {
                    // CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "You are online", MessageConstant.toast_success);
                    funRuntimePermission();
                     initValue();
                    if (SharedPrefrence_Login.getmShow_status().equals("1")) {
                        ll_recycler_layout.setVisibility(View.VISIBLE);
                        locationModelArrayList = new ArrayList<>();
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rv_upcomingLocation.setLayoutManager(linearLayoutManager);
                        shareLocationAdapter = new ShareLocationAdapter(getApplicationContext(), locationModelArrayList);
                        if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                            //func_ShareLocationInfo();
                        } else {
                            FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED, FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
                        }
                    } else {
                        //ll_recycler_layout.setVisibility(View.GONE);
                    }
                }
            } else {
                //Toast.makeText(getApplicationContext(),"Location off gf",Toast.LENGTH_SHORT).show();
                offLocationNotification();
                turnGPSOn();
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }



    @Override
    public void onCallClickListener(int position, ShareLocationModel helper) {
        dialPhoneNumber(helper.getCustomer_mobile_no());
    }

    @Override
    public void onNavigateClickListener(int position, ShareLocationModel helper) {
        navigatePath(Double.parseDouble(helper.getLatitude()), Double.parseDouble(helper.getLongitude()));

    }

    @Override
    public void onCompleteClickListener(int position, ShareLocationModel helper) {
        funopenDialog("1", "Complete", helper.getCustomer_mobile_no());
        // funCloseTrip(SharedPrefrence_Login.getMhawker_code(),"1");
    }

    @Override
    public void onCancelClickListener(int position, ShareLocationModel helper) {
        funopenDialog("2", "Cancel", helper.getCustomer_mobile_no());
    }

    private void funopenDialog(final String sStatus, final String sType, final String cus_no) {
        final BottomSheetDialog dialog = new BottomSheetDialog(Home.this);
        dialog.setContentView(R.layout.dialog_update_trip_status_layout);
        ImageView img_cancel_id = dialog.findViewById(R.id.img_cancel_id);
        Button btn_close = dialog.findViewById(R.id.btn_close);
        final EditText edt_Discription = dialog.findViewById(R.id.edt_Discription);
        btn_close.setText("Submit");

        img_cancel_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sDescription = edt_Discription.getText().toString();
                if (!TextUtils.isEmpty(sDescription)) {
                    funCloseTrip(SharedPrefrence_Login.getMhawker_code(), sStatus, dialog, sDescription, cus_no);
                } else {
                    Toast.makeText(Home.this, "अपना विवरण दर्ज करें\nEnter your description", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "+91" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void navigatePath(double dLat, double dLon) {
        String currLocation = location_update.LATTITUDE + "," + location_update.LONGITUDE;
        String desLocation = dLat + "," + dLon;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + currLocation + "&daddr=" + desLocation));
        startActivity(intent);
    }

    public void funCloseTrip(final String hawker_code, final String sclose_status, final BottomSheetDialog dialog,
                             final String sDescription, final String cus_no) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        // fun_progressBar();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_CLOSE_TRIP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus = jsoObject.getString("status");
                            if (strStatus.equals("1")) {
                                dialog.dismiss();
                                Toast.makeText(Home.this, jsoObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
                                    func_ShareLocationInfo();
                                } else {
                                    FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED, FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
                                }
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
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
                params.put("hawker_code", hawker_code);
                params.put("hawker_mobile_no", SharedPrefrence_Login.getPnumber());
                params.put("close_status", sclose_status);
                params.put("close_longitude", location_update.LONGITUDE);
                params.put("close_latitude", location_update.LATTITUDE);
                params.put("description", sDescription);
                params.put("customer_mobile_no", cus_no);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }


    public void onClickAdvertisement(View view) {
        startActivity(new Intent(getApplicationContext(), RequestAdvertisementActivity.class));
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public boolean isGPSEnabled(Context mContext){

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }



}
