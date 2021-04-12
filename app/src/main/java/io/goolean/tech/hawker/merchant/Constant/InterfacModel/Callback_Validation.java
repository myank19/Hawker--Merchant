package io.goolean.tech.hawker.merchant.Constant.InterfacModel;

import android.content.Context;
import android.widget.EditText;

public interface Callback_Validation {


 boolean checkNumber(Context context, EditText number, String msg);

 boolean checkPassword(Context context, EditText password, String msg);

 boolean checkEditText(Context context, EditText edt, String msg);

 boolean checkEditPostal(Context context, EditText edt, String msg);

 boolean checkTime(Context context, EditText edt, String msg);

 boolean stringCompare(Context context, EditText edt, String stringValue, String msg);

 boolean stringEqual(Context context, String str1, String str2, String msg);
}
