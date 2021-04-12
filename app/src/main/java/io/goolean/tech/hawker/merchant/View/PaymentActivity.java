package io.goolean.tech.hawker.merchant.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Adapter.PlanAdapter;
import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Model.PaymentPlanModel;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;
import io.goolean.tech.hawker.merchant.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultWithDataListener {
    ActivityPaymentBinding paymentBinding;
    String value = "";
    String timeDuration = "";
    String price = "";
    String paymentId;
    List<PaymentPlanModel> planList = new ArrayList<>();
    PaymentPlanModel planModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        Checkout.preload(this);
        selectPlan();
        paymentBinding.btnPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        /*if (view == paymentBinding.cdDayPlan) {
            value = "day";
            price = "10000";
            paymentBinding.cdDayPlan.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            paymentBinding.cdMonthPlan.setCardBackgroundColor(0);
            paymentBinding.cdHalfYearlyPlan.setCardBackgroundColor(0);
        } else if (view == paymentBinding.cdMonthPlan) {
            value = "month";
            price = "50000";
            paymentBinding.cdDayPlan.setCardBackgroundColor(0);
            paymentBinding.cdMonthPlan.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            paymentBinding.cdHalfYearlyPlan.setCardBackgroundColor(0);
        } else if (view == paymentBinding.cdHalfYearlyPlan) {
            value = "halfyearly";
            price = "150000";
            paymentBinding.cdDayPlan.setCardBackgroundColor(0);
            paymentBinding.cdMonthPlan.setCardBackgroundColor(0);
            paymentBinding.cdHalfYearlyPlan.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }*/
        if (view == paymentBinding.btnPay) {
            startPayment(price + "00");
        }
    }

    private void startPayment(String price) {
        Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_SR5GDCaUgJ8hhb");

        JSONObject options = new JSONObject();
        try {
            options.put("name", "Hawkers");
            options.put("description", "Service fee");
            options.put("currency", "INR");
            options.put("amount", price);

            checkout.open(activity, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        paymentId = paymentData.getPaymentId();
        paymentData();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void selectPlan() {
        final RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.URL_PLAN_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String status = jsonObject1.getString("status");
                            if (status.equals("1")) {
                                String message = jsonObject1.getString("message");
                                JSONArray jsonArray = jsonObject1.getJSONArray("plan_type");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String plan_type = jsonObject2.optString("plan_type");
                                    String day = jsonObject2.optString("value");
                                    String amount = jsonObject2.optString("amount");

                                    planModel = new PaymentPlanModel();
                                    planModel.setPlanType(plan_type);
                                    planModel.setValue(day);
                                    planModel.setPrice(amount);
                                    planList.add(planModel);
                                }
                                paymentBinding.rvPlan.setAdapter(new PlanAdapter(PaymentActivity.this, planList));
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
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "It took longer than expected to get the response from Server.", MessageConstant.toast_warning);
                        } else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Server Respond Error! Try Again Later", MessageConstant.toast_warning);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }

    private void paymentData() {
        final RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_REQUEST_HAWKER_PAID_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String status = jsonObject1.getString("status");
                            if (status.equals("1")) {
                                String message = jsonObject1.getString("message");
                                showSettingsDialog("Your Ads has been Submitted Successfully.");
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
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "It took longer than expected to get the response from Server.", MessageConstant.toast_warning);
                        } else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(getApplicationContext(), "Server Respond Error! Try Again Later", MessageConstant.toast_warning);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("payment_id", paymentId);
                params.put("payment_status", "success");
                params.put("amount", price);
                params.put("ad_type", value);
                params.put("days", timeDuration);
                params.put("max_id", getIntent().getStringExtra("max_id"));
                params.put("device_id", SharedPrefrence_Login.getPdevice_id());
                params.put("hawker_code", SharedPrefrence_Login.getMhawker_code());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQue(stringRequest);
    }

    private void showSettingsDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status");
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    public void SelectPlan(PaymentPlanModel paymentPlanModel) {
        timeDuration = paymentPlanModel.getValue();
        price = paymentPlanModel.getPrice();
        value = paymentPlanModel.getPlanType();
        paymentBinding.btnPay.setVisibility(View.VISIBLE);
        paymentBinding.btnPay.setEnabled(true);
        paymentBinding.tvTerms.setVisibility(View.VISIBLE);
    }
}
