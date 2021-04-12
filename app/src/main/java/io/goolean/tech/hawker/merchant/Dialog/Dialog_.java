package io.goolean.tech.hawker.merchant.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import io.goolean.tech.hawker.merchant.Constant.ConnectionDetector;
import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.View.Otp;


public class Dialog_ extends Activity {

    public static Context _context;
    public static TextView camera,gallery,connect,tv_Info,btn_info_ok;
    public static Dialog_ dialog_ = null;
    public static Dialog dialog;

    public Dialog_(Context context) {
        this._context = context;
    }

    public static Dialog_ getInstanceDialog(Context context) {
        if(dialog_==null)
            _context = context;
        {
            dialog = new Dialog(_context);
        }
        return dialog_;
    }

    public static void dialog_Internet(){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_no_internet_connection);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        connect = (TextView)dialog.findViewById(R.id.button_connect_id);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity( intent);
                dialog.dismiss();
            }
        });

    }


        public static void dialog_Close()
    {
        if(dialog.isShowing())
        {
            dialog.dismiss();
        }
    }


            public static void dialog_MessageAppFinish(Context context, String message){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message_info);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        tv_Info =(TextView)dialog.findViewById(R.id.tv_info_id);
        tv_Info.setText(message);
        btn_info_ok = (TextView)dialog.findViewById(R.id.button_ok_id);
        btn_info_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }


    public static void dialog_MessageAppStop(Context context, String message){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_message_info);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        tv_Info =(TextView)dialog.findViewById(R.id.tv_info_id);
        tv_Info.setText(message);
        btn_info_ok = (TextView)dialog.findViewById(R.id.button_ok_id);
        btn_info_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dialog.dismiss();
            }
        });
    }

    public static void dialog_OTPInternet(final Context applicationContext, final String pdevice_id, final String pnumber){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_otp_internet_connection);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        connect = (TextView)dialog.findViewById(R.id.button_connect_id);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.getConnectionDetector(applicationContext).isConnectingToInternet() == true) {
                    new Otp().fun_OTPExpire(applicationContext,pdevice_id,pnumber);
                    dialog.dismiss();
                }else {

                    CallbackSnakebarModel.getInstance().SnakebarMessage(applicationContext, "Please check your internet connection", MessageConstant.toast_warning);

                }
            }
        });

    }



}

