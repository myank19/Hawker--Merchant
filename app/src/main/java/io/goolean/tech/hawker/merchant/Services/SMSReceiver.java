package io.goolean.tech.hawker.merchant.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;


public class SMSReceiver extends BroadcastReceiver {
    private static final String Job_Tag = "my_job_tag";
    FirebaseJobDispatcher jobDispatcher;
    private String android_id;
    private static String uniqueID = null;
    private static String sUUID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static final boolean REGISTER_DUPLICATE_RECEIVER = Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            /*jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            Job job = jobDispatcher.newJobBuilder().
                    setService(MyJobService.class).
                    setLifetime(Lifetime.FOREVER).
                    setRecurring(true).
                    setTag(Job_Tag).
                    setTrigger(Trigger.executionWindow(0,5)).
                    // setTrigger(JobDispatcherUtils.periodicTrigger(20,1)).
                            setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                            setReplaceCurrent(false)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build();
            jobDispatcher.mustSchedule(job);*/
        }
    }



}
