package io.goolean.tech.hawker.merchant.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Constant.AppStatus;
import io.goolean.tech.hawker.merchant.Constant.ConnectionDetector;
import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;
import io.goolean.tech.hawker.merchant.Constant.StringConstant;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Lib.FancyToast.FancyToast;
import io.goolean.tech.hawker.merchant.Model.ShareLocationModel;
import io.goolean.tech.hawker.merchant.Model.ShareLocationModelHistory;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;

public class ShareLocationHistory extends AppCompatActivity {

    private ArrayList<ShareLocationModelHistory> locationModelArrayList;
    private RequestQueue requestQueue;
    private ShareLocationModelHistory shareLocationModel;
    private RecyclerView recyclerView;
    private ShareLocationHistoryAdapter shareLocationAdapter;
    private ImageView ivBack;
    private TextView tv_notFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location_history);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        if (savedInstanceState == null) {
            SharedPrefrence_Login.getDataLogin(ShareLocationHistory.this);
            recyclerView = (RecyclerView) findViewById(R.id.rvLocationHistory);
            ivBack = findViewById(R.id.ivBack);
            tv_notFound = findViewById(R.id.tv_notFound);

            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            locationModelArrayList = new ArrayList<>();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            shareLocationAdapter = new ShareLocationHistoryAdapter(getApplicationContext(), locationModelArrayList);
            if(ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet()==true){
                func_Notification();
            }else {
                FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED,FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
        } else {
            if (AppStatus.getInstance(this).isOnline()) {
                func_Notification();
            } else {
                CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Please check your internet connection"+"\n"+"आप किसी भी नेटवर्क से नहीं जुड़े हैं", MessageConstant.toast_warning);
            }
        }
        super.onWindowFocusChanged(hasFocus);

    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void func_Notification() {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_TRIP_HISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && response.length() > 0) {
                            try {
                                locationModelArrayList.clear();
                                JSONObject obj = new JSONObject(response);
                                String strFav=  obj.getString("data");
                                JSONArray jsonArrayFav = new JSONArray(strFav);
                                for(int i = 0;i<jsonArrayFav.length();i++) {
                                    JSONObject jsonObjectFav = jsonArrayFav.getJSONObject(i);
                                    if(jsonObjectFav.getString("status").equals("1")) {
                                        tv_notFound.setVisibility(View.GONE);
                                        shareLocationModel = new ShareLocationModelHistory();
                                        shareLocationModel.setCustomer_name( jsonObjectFav.getString("customer_name"));
                                        shareLocationModel.setCustomer_mobile_no( jsonObjectFav.getString("customer_mobile_no"));
                                        shareLocationModel.setDate_time(parseDateToddMMyyyy(jsonObjectFav.getString("date_time")));
                                        shareLocationModel.setLatitude( jsonObjectFav.getString("latitude"));
                                        shareLocationModel.setLongitude( jsonObjectFav.getString("longitude"));
                                        shareLocationModel.setLocation_name( jsonObjectFav.getString("location_name"));
                                        shareLocationModel.setDescription( jsonObjectFav.getString("description"));
                                        shareLocationModel.setClose_status( jsonObjectFav.getString("close_status"));
                                        locationModelArrayList.add(shareLocationModel);
                                    }else {
                                        tv_notFound.setVisibility(View.VISIBLE);
                                    }
                                }
                                recyclerView.setAdapter(shareLocationAdapter);
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
                        }else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), StringConstant.RESPONSE_FAILURE_NOT_RESPOND,
                                    MessageConstant.toast_warning);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams()  {
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

}
