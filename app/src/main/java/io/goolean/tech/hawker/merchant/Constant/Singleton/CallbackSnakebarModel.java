package io.goolean.tech.hawker.merchant.Constant.Singleton;


import android.app.Activity;
import android.content.Context;

import io.goolean.tech.hawker.merchant.Lib.FancyToast.FancyToast;


public class CallbackSnakebarModel extends Activity implements Callback_Snakebar {


    private static CallbackSnakebarModel snakebarModel = null;

    public CallbackSnakebarModel() {
    }

        public static CallbackSnakebarModel getInstance(){
        if (snakebarModel==null)
        {
            snakebarModel = new CallbackSnakebarModel();
        }
        return snakebarModel;
    }

    @Override
    public void SnakebarMessage(Context applicationContext, String message, String value) {
        if(value.equals("1"))
        {
            FancyToast.makeText(applicationContext,message,FancyToast.LENGTH_LONG, FancyToast.SUCCESS,true).show();
        }else if(value.equals("2"))
        {
            FancyToast.makeText(applicationContext,message,FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }else if(value.equals("3"))
        {
            FancyToast.makeText(applicationContext,message,FancyToast.LENGTH_LONG,FancyToast.INFO,true).show();
        }else if(value.equals("4"))
        {
            FancyToast.makeText(applicationContext,message,FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
        }else if(value.equals("5"))
        {
            FancyToast.makeText(applicationContext,message,FancyToast.LENGTH_LONG,FancyToast.CONFUSING,true).show();
        }
    }
}
