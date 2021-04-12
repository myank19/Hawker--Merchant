package io.goolean.tech.hawker.merchant.Storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefrence_Login {

    public static Context _context;
    public static SharedPreferences sharedPrefrencesLogin = null;
    public static SharedPreferences sharedPrefrencesCheckNumber = null;
    public static String number,type;
    public static String mnumber,mpassword,mdevice_id,mnotification_id,mname,mhawker_code,mtype;
    public static int sellerTimer;
    public static String muser_type,mShow_status;
    public static String sToken,sONOFF,sOTP;
    public static String UPDATE_STATUS;



    public SharedPrefrence_Login(Context context) {
        this._context=context;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_CHECK_NUMBER", Context.MODE_PRIVATE);
        sharedPrefrencesCheckNumber = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
    }






    public static void saveUpdateStatus(Context context, String sReferral) {
        _context = context;
        sharedPrefrencesLogin = _context.getSharedPreferences("CUSTOMER_LOGIN", Context.MODE_PRIVATE);
        UPDATE_STATUS=sReferral;
        SharedPreferences.Editor editor = sharedPrefrencesLogin.edit();
        editor.putString("UPDATE_STATUS", UPDATE_STATUS);
        editor.commit();

    }
    public static void getUpdateStatus(Context applicationContext) {
        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("CUSTOMER_LOGIN", Context.MODE_PRIVATE);
        UPDATE_STATUS=  sharedPrefrencesLogin.getString("UPDATE_STATUS","");
    }
    public static String getUPDATE_STATUS() { return UPDATE_STATUS; }




    //===============================================SALES LOGIN SHAREDPREFRENCES========================================================

    public static void saveNumberData(Context applicationContext, String str_number, String str_type) {
        if(sharedPrefrencesLogin == null)
        {
            _context = applicationContext;
            sharedPrefrencesCheckNumber = _context.getSharedPreferences("HAWKER_CHECK_NUMBER", Context.MODE_PRIVATE);
        }
        _context=applicationContext;
        number=str_number;
        type = str_type;
        SharedPreferences.Editor editor = sharedPrefrencesCheckNumber.edit();
        editor.putString("NUMBER", number);
        editor.putString("TYPE",type);
        editor.commit();
    }
    public static void getNumberData(Context applicationContext) {

        if(sharedPrefrencesCheckNumber == null)
        {
            _context = applicationContext;
            sharedPrefrencesCheckNumber = _context.getSharedPreferences("HAWKER_CHECK_NUMBER", Context.MODE_PRIVATE);
        }
        number=  sharedPrefrencesCheckNumber.getString("NUMBER","");
        type=  sharedPrefrencesCheckNumber.getString("TYPE","");
    }

    public static String getNumber() {
        return number;
    }

    public static String getType() { return type; }




    //===============================================SALES LOGIN SHAREDPREFRENCES========================================================

    public static void saveDataLogin(Context applicationContext, String number, String device_id, String notification_id,
                                     String name, String hawker_code, String type,String user_type,String Show_status, int seller_timer) {

        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
        mnumber=number;
        mdevice_id=device_id;
        mnotification_id=notification_id;
        mname=name;
        mhawker_code=hawker_code;
        mtype=type;
        muser_type=user_type;
        mShow_status=Show_status;
        sellerTimer = seller_timer;
        SharedPreferences.Editor editor = sharedPrefrencesLogin.edit();
        editor.putString("NUMBER", mnumber);
        editor.putString("DEVICE_ID", mdevice_id);
        editor.putString("NOTIFICATION_ID",mnotification_id);
        editor.putString("NAME", mname);
        editor.putString("HAWKER_CODE",mhawker_code);
        editor.putString("TYPE",mtype);
        editor.putString("USER_TYPE",user_type);
        editor.putString("SHOW_STATUS",Show_status);
        editor.putInt("SELLER_TIME", sellerTimer);
        editor.commit();
    }



    public static void getDataLogin(Context applicationContext) {


        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
        mnumber=  sharedPrefrencesLogin.getString("NUMBER","");
        mdevice_id=  sharedPrefrencesLogin.getString("DEVICE_ID","");
        mnotification_id=  sharedPrefrencesLogin.getString("NOTIFICATION_ID","");
        mname=  sharedPrefrencesLogin.getString("NAME","");
        mhawker_code=  sharedPrefrencesLogin.getString("HAWKER_CODE","");
        mtype=  sharedPrefrencesLogin.getString("TYPE","");
        muser_type=  sharedPrefrencesLogin.getString("USER_TYPE","");
        mShow_status=  sharedPrefrencesLogin.getString("SHOW_STATUS","");
        sellerTimer = sharedPrefrencesLogin.getInt("SELLER_TIME",0);
    }
    public static String getPnumber() { return mnumber; }


    public static String getPdevice_id() { return mdevice_id; }

    public static String getPnotification_id() { return mnotification_id; }

    public static String getPname() { return mname; }

    public static String getMhawker_code() { return mhawker_code; }

    public static String getPtype() { return mtype; }

    public static String getMuser_type() { return muser_type; }
    public static String getmShow_status() { return mShow_status; }

    public static int getSeller_time() {
        return sellerTimer;
    }







    //===============================ALL SHARED PREFRENCES CLEAR==========================================================================

    public static void ClearSharedPrefrenc(Context applicationContext)
    {

        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefrencesLogin.edit();
        editor.putString("NUMBER",null);
        editor.putString("PASSWORD",null);
        editor.putString("DEVICE_ID",null);
        editor.putString("NAME",null);
        editor.putString("HAWKER_CODE",null);
        editor.putString("TYPE",null);
        editor.putString("NOTIFICATION_ID",null);
        editor.commit();
        editor.clear();
        editor.commit();

        sharedPrefrencesCheckNumber = _context.getSharedPreferences("HAWKER_CHECK_NUMBER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPrefrencesCheckNumber.edit();
        editor1.putString("NUMBER",null);
        editor1.putString("TYPE",null);
        editor1.commit();
        editor1.clear();
        editor1.commit();

        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_OTP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPrefrencesLogin.edit();
        editor2.putString("HAWKER_OTP",null);
        editor2.commit();
        editor2.clear();
        editor2.commit();


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void saveToken(Context applicationContext, String token) {
        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
        sToken=token;
        SharedPreferences.Editor editor = sharedPrefrencesLogin.edit();
        editor.putString("FCM_TOKEN", sToken);
        editor.commit();
    }


    public static void getTokenS(Context applicationContext) {


        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        sToken=  sharedPrefrencesLogin.getString("FCM_TOKEN","");

    }
    public static String getTokenS() { return sToken; }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void saveONOFF(Context applicationContext, String sStatus) {
        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
        sONOFF=sStatus;
        SharedPreferences.Editor editor = sharedPrefrencesLogin.edit();
        editor.putString("S_ON_OFF", sONOFF);
        editor.commit();
    }


    public static void getDataONOFF(Context applicationContext) {


        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_LOGIN", Context.MODE_PRIVATE);
        sONOFF=  sharedPrefrencesLogin.getString("S_ON_OFF","");

    }
    public static String getONOFF() { return sONOFF; }



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void fun_checkOTP(Context applicationContext, String sStatus) {
        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_OTP", Context.MODE_PRIVATE);
        sOTP=sStatus;
        SharedPreferences.Editor editor = sharedPrefrencesLogin.edit();
        editor.putString("OTP", sOTP);
        editor.commit();
        editor.apply();
    }


    public static void fun_getcheckOTP(Context applicationContext) {


        _context = applicationContext;
        sharedPrefrencesLogin = _context.getSharedPreferences("HAWKER_OTP", Context.MODE_PRIVATE);
        sOTP=  sharedPrefrencesLogin.getString("OTP","");

    }
    public static String getcheckOTP() { return sOTP; }
}
