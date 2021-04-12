package io.goolean.tech.hawker.merchant.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public  class SettingIntent {

private Context context;

    public SettingIntent(Activity activity) {
        this.context = activity;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }
}
