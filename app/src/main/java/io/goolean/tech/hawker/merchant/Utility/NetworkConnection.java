package io.goolean.tech.hawker.merchant.Utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Synergy on 11/10/2016.
 */
public class NetworkConnection extends Activity {
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;


    public  boolean getConnection( Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;

                } else
                {
                    haveConnectedWifi =false ;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;

                } else
                {
                    haveConnectedMobile = false ;
                }
            }
        }

        if(haveConnectedWifi==true ||haveConnectedMobile==true )
            return true;
        else
            return false ;
    }


}
