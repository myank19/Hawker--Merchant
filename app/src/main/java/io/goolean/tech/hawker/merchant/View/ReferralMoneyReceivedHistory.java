package io.goolean.tech.hawker.merchant.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Constant.ConnectionDetector;
import io.goolean.tech.hawker.merchant.Constant.StringConstant;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Lib.FancyToast.FancyToast;
import io.goolean.tech.hawker.merchant.Model.ReferralMoneyModel;
import io.goolean.tech.hawker.merchant.Networking.ImageClass;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;

public class ReferralMoneyReceivedHistory extends AppCompatActivity {
    private RecyclerView rvreferralmoney;
    private TextView tv_notFound;
    private ReferralMoneyModel referralMoneyModel;
    private ArrayList<ReferralMoneyModel> referralMoneyModelArrayList;
    private ReferralMoneyAdapter referralMoneyAdapter;
    private RequestQueue requestQueue;
    private ProgressDialog _progressDialog;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_money_received_history);
        getSupportActionBar().hide();

        rvreferralmoney = findViewById(R.id.rvreferralmoney);
        tv_notFound = findViewById(R.id.tv_notFound);
        ivBack = findViewById(R.id.ivBack);

        SharedPrefrence_Login.getDataLogin(ReferralMoneyReceivedHistory.this);

        referralMoneyModelArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReferralMoneyReceivedHistory.this);
        rvreferralmoney.setLayoutManager(linearLayoutManager);
        referralMoneyAdapter = new ReferralMoneyAdapter(ReferralMoneyReceivedHistory.this, referralMoneyModelArrayList);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (ConnectionDetector.getConnectionDetector(getApplicationContext()).isConnectingToInternet() == true) {
            funMoneyDetail(SharedPrefrence_Login.getPnumber());
        } else {
            FancyToast.makeText(getApplicationContext(), StringConstant.NETWORK_NOT_CONNECTED, FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
        }
    }

    private void funMoneyDetail(final String smobile_number) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_RECEIVED_REFERRAL_MONEY_HISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && response.length() > 0) {
                            tv_notFound.setVisibility(View.GONE);
                            parseData(response);
                        } else {
                            tv_notFound.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(ReferralMoneyReceivedHistory.this, StringConstant.RESPONSE_FAILURE_MORE_TIME, Toast.LENGTH_LONG).show();
                        } else if (error.getClass().equals(ServerError.class)) {
                            Toast.makeText(ReferralMoneyReceivedHistory.this, StringConstant.RESPONSE_FAILURE_NOT_RESPOND, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ReferralMoneyReceivedHistory.this, StringConstant.RESPONSE_FAILURE_SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", smobile_number);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(StringConstant.REPEAT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);

    }

    private void parseData(String response) {
        try {
            referralMoneyModelArrayList.clear();
            JSONObject obj = new JSONObject(response);
            String strFav = obj.getString("data");
            JSONArray jsonArrayFav = new JSONArray(strFav);
            for (int i = 0; i < jsonArrayFav.length(); i++) {
                JSONObject jsonObjectFav = jsonArrayFav.getJSONObject(i);
                if (jsonObjectFav.getString("status").equals("1")) {
                    Log.i("Count", "" + jsonArrayFav.length());
                    tv_notFound.setVisibility(View.GONE);
                    referralMoneyModel = new ReferralMoneyModel();
                    referralMoneyModel.setMoney(jsonObjectFav.getString("money"));
                    referralMoneyModel.setDate_time(jsonObjectFav.getString("date_time"));
                    referralMoneyModel.setsAll("All");
                    referralMoneyModelArrayList.add(referralMoneyModel);
                } else {
                    tv_notFound.setVisibility(View.VISIBLE);
                }
            }
            rvreferralmoney.setAdapter(referralMoneyAdapter);
            referralMoneyAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
