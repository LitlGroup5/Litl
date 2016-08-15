package com.litlgroup.litl.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.litlgroup.litl.BuildConfig;

import timber.log.Timber;

/**
 * Created by monusurana on 8/15/16.
 */
public class LitlApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        LitlApplication.context = this;

        Timber.plant(new Timber.DebugTree());
        Timber.d("Commit Id: " + BuildConfig.GIT_SHA);
        Timber.d("Time: " + BuildConfig.BUILD_TIME);

        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeathOnNetwork()
                        .build());
            }
        }
    }
}
