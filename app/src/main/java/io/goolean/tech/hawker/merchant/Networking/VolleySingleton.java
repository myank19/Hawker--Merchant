package io.goolean.tech.hawker.merchant.Networking;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
//"http://13.233.216.129"
private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private static Context mctx;


    private VolleySingleton(Context context){
        this.mctx=context;
        this.requestQueue=getRequestQueue();

    }
    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized VolleySingleton getInstance(Context context){
        if (volleySingleton==null){
            volleySingleton=new VolleySingleton(context);
        }
        return volleySingleton;
    }
    public<T> void addToRequestQue(Request<T> request){
        requestQueue.add(request);

    }
}
