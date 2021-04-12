package io.goolean.tech.hawker.merchant.Constant.InterfacModel;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import io.goolean.tech.hawker.merchant.Constant.MessageConstant;
import io.goolean.tech.hawker.merchant.Constant.Singleton.CallbackSnakebarModel;


public class Validation implements Callback_Validation {

    boolean success = true;

    @Override
    public boolean checkNumber(Context context, EditText mEditNumber, String msg) {
        if(TextUtils.isEmpty(mEditNumber.getText().toString()))
        {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Please Enter Registered Number", MessageConstant.toast_warning);
            return false;
        }else if (mEditNumber.getText().length() != 10)  {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Enter 10 Digit Number", MessageConstant.toast_warning);
            return false;
        }else {
            return success;
        }

    }

    @Override
    public boolean checkPassword(Context context, EditText mEditPassword, String msg) {
        if(TextUtils.isEmpty(mEditPassword.getText().toString()))
        {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Please Enter Login", MessageConstant.toast_warning);
            return false;
        }else if (mEditPassword.getText().length() < 5)  {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Enter Login Minimum 6 Digit", MessageConstant.toast_warning);
            return false;
        }
        return success;
    }

    @Override
    public boolean checkEditText(Context context, EditText edt, String msg) {

        if(TextUtils.isEmpty(edt.getText().toString()))
        {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, msg, MessageConstant.toast_warning);
            return false;
        }
        return success;
    }
    @Override
    public boolean checkEditPostal(Context context, EditText mEditPassword, String msg) {
        if(TextUtils.isEmpty(mEditPassword.getText().toString()))
        {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Please Enter Pin", MessageConstant.toast_warning);
            return false;
        }else if (mEditPassword.getText().length()!=6)  {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Enter 6 digit Pin", MessageConstant.toast_warning);
            return false;
        }
        return success;
    }

    @Override
    public boolean checkTime(Context context, EditText edt, String msg) {
        if(TextUtils.isEmpty(edt.getText().toString()))
        {
                CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Enter Time in Hours", MessageConstant.toast_warning);
                return false;
        }else if (Integer.parseInt(msg)>=24)  {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, "Enter time between 24 hours", MessageConstant.toast_warning);
            return false;
        }
        return success;
    }

    @Override
    public boolean stringCompare(Context context, EditText edt, String stringValue, String msg) {
        if(!edt.getText().toString().equals(stringValue))
        {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, msg, MessageConstant.toast_warning);
            return false;
        }
        return success;
    }

    @Override
    public boolean stringEqual(Context context, String str1, String str2, String msg) {
        if(str1.equals(str2))
        {
            CallbackSnakebarModel.getInstance().SnakebarMessage(context, msg, MessageConstant.toast_warning);
            return false;
        }
        return success;
    }
}
