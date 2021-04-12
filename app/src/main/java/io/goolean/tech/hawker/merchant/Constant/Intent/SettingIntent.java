package io.goolean.tech.hawker.merchant.Constant.Intent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.fragment.app.FragmentActivity;


public  class SettingIntent {

Context context;

    public SettingIntent(FragmentActivity activity) {
        this.context = activity;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }
}
