package io.goolean.tech.hawker.merchant.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import io.goolean.tech.hawker.merchant.Location.Location_Update;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;
import io.goolean.tech.hawker.merchant.View.Home;
import io.goolean.tech.hawker.merchant.View.Splash;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    NotificationChannel mChannel;
    public String title;
    Handler mhandler;
    Home home = new Home();
    Location_Update location_update;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPrefrence_Login.getDataLogin(MyFirebaseMessagingService.this);
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message, final String Title) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
                if(Title.equals("logout")){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MyFirebaseMessagingService.this,title,Toast.LENGTH_SHORT).show();
                            location_update = new Location_Update(getApplicationContext());
                            home.funLogoutSSS(MyFirebaseMessagingService.this,SharedPrefrence_Login.getMhawker_code(),
                                    location_update.LATTITUDE,location_update.LONGITUDE );
                            SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                        }
                    });

                    createNotificationChannel(Title, message);
                }else {
                    createNotificationChannel(Title,message);
                }

            }else{
                if(Title.equals("logout")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MyFirebaseMessagingService.this,title,Toast.LENGTH_SHORT).show();
                            location_update = new Location_Update(getApplicationContext());
                            home.funLogoutSSS(MyFirebaseMessagingService.this,SharedPrefrence_Login.getMhawker_code(),
                                    location_update.LATTITUDE,location_update.LONGITUDE );
                            SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                        }
                    });
                }
                // If the app is in background, firebase itself handles the notification
            }
        }else {
            // play notification sound
           /* NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();*/
            if(Title.equals("logout")){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MyFirebaseMessagingService.this,title,Toast.LENGTH_SHORT).show();
                        SharedPrefrence_Login.getDataLogin(MyFirebaseMessagingService.this);
                        location_update = new Location_Update(getApplicationContext());
                        home.funLogoutSSS(MyFirebaseMessagingService.this,SharedPrefrence_Login.getMhawker_code(),
                                location_update.LATTITUDE,location_update.LONGITUDE );
                        SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                    }
                });

                createNotificationChannel(Title, message);
            }else {
                createNotificationChannel(Title,message);
            }

        }

    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();

                    if(title.equals("logout")){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(MyFirebaseMessagingService.this,title,Toast.LENGTH_SHORT).show();
                                SharedPrefrence_Login.getDataLogin(MyFirebaseMessagingService.this);
                                location_update = new Location_Update(getApplicationContext());
                                home.funLogoutSSS(MyFirebaseMessagingService.this,SharedPrefrence_Login.getMhawker_code(),
                                        location_update.LATTITUDE,location_update.LONGITUDE );
                                SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                            }
                        });
                        createNotificationChannel(title, message);
                    }else {
                        createNotificationChannel(title,message);
                    }
                } else {
                    // app is in background, show the notification in notification tray
                    Intent resultIntent = new Intent(getApplicationContext(), Splash.class);
                    resultIntent.putExtra("message", message);

                    if(title.equals("logout")){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                SharedPrefrence_Login.getDataLogin(MyFirebaseMessagingService.this);
                                location_update = new Location_Update(getApplicationContext());
                                home.funLogoutSSS(MyFirebaseMessagingService.this,SharedPrefrence_Login.getMhawker_code(),
                                        location_update.LATTITUDE,location_update.LONGITUDE );
                                SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                            }
                        });
                    }else {
                        if (TextUtils.isEmpty(message)) {
                            createNotificationChannel(title,message);
                        } else {
                            createNotificationChannel(title,message);
                        }
                    }

                }
            }else {
                if(title.equals("logout")){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            SharedPrefrence_Login.getDataLogin(MyFirebaseMessagingService.this);
                            location_update = new Location_Update(getApplicationContext());
                            home.funLogoutSSS(MyFirebaseMessagingService.this,SharedPrefrence_Login.getMhawker_code(),
                                    location_update.LATTITUDE,location_update.LONGITUDE );
                            SharedPrefrence_Login.ClearSharedPrefrenc(getApplicationContext());
                        }
                    });
                }else {
                    createNotificationChannel(title, message);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    public void createNotificationChannel(String title, String Text) {
        if(!title.equals("Duty")){
            Intent intent = new Intent(this, Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, "Hawker", importance);
            }

            /*.setAutoCancel(true)*/
            Notification builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(Text)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(false)
                    .setContentIntent(pendingIntent).build();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(notifyID , builder);
                if(title.equalsIgnoreCase("Hawker Share Location")){
                    Intent intents = new Intent(this, Home.class);
                    intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intents);
                }
            }else {
                mNotificationManager.notify(0, builder);
                if(title.equalsIgnoreCase("Hawker Share Location")){
                    Intent intents = new Intent(this, Home.class);
                    intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intents);
                }
            }


        }
    }



}