package io.goolean.tech.hawker.merchant.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Dialog.Dialog_;
import io.goolean.tech.hawker.merchant.Location.Location_Update;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;
import io.goolean.tech.hawker.merchant.View.Home;

import static io.goolean.tech.hawker.merchant.Translate.App.CHANNEL_ID;

public class SwitchButtonListener extends BroadcastReceiver {
    private RequestQueue requestQueue;
    String strStatus,strActive_status,strActive_msg;
    Location_Update location_update;
    NotificationChannel mChannel;
    SwitchButtonListenerON switchButtonListenerON = new SwitchButtonListenerON();
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("action.cancel.notification".equalsIgnoreCase(intent.getAction())) {

            // .. do what ever you want
            SharedPrefrence_Login.getDataLogin(context);
            location_update = new Location_Update(context);
            Log.d("Here", "I am here");
            Toast.makeText(context, "OFF", Toast.LENGTH_SHORT).show();
            createNotification(context);
            fun_DutyONOFF("0",SharedPrefrence_Login.getMhawker_code(),context);

        }
    }

    private void createNotification(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Hawker", NotificationManager.IMPORTANCE_HIGH);
        }
        Intent notificationIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_business)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        Intent dutyIntent = new Intent("dutyON");
        PendingIntent pendingDutyIntent = PendingIntent.getBroadcast(context,0,dutyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.notification_layout_1);
        //the intent that is started when the notification is clicked (works)
        notification.contentView = notificationView;
        notification.contentIntent = pendingDutyIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationView.setOnClickPendingIntent(R.id.closeDuty, pendingDutyIntent);
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(1 , notification);
        }else {
            mNotificationManager.notify(1, notification);
        }
    }

    private void fun_DutyONOFF(final String sStatus,final String hawker_code,final  Context context) {
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        //fun_progressBar();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_DUTY_ON_OFF_SELLER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // _progressDialog.dismiss();
                            // converting response to json object
                            JSONObject obj = new JSONObject(response);
                            String str = obj.getString("data");
                            JSONObject jsoObject = new JSONObject(str);
                            strStatus  = jsoObject.getString("status");
                            strActive_status = jsoObject.getString("active_status");
                            strActive_msg = jsoObject.getString("active_message");
                            if(strStatus.equals("1")){
                               // System.exit(0);
                               createNotification(context);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //      _progressDialog.dismiss();
                        if (error.getClass().equals(TimeoutError.class)) {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "It took longer than expected to get the response from Server.",
                                    MessageConstant.toast_warning);
                        }else {
                            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Server Respond Error! Try Again Later",
                                    MessageConstant.toast_warning);
                        }      }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hawker_code", hawker_code);
                params.put("longitude", location_update.LONGITUDE);
                params.put("latitude", location_update.LATTITUDE);
                params.put("duty_status", sStatus);
                params.put("notification_id",SharedPrefrence_Login.getPnotification_id());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQue(stringRequest);

    }
}