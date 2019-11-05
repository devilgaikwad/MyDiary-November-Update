package com.ajaygaikwad.mydiary.Classes;

import android.app.Application;
import android.content.Context;


import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.ajaygaikwad.mydiary.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * Created by Shri on 24/11/2016.
 */
@ReportsCrashes(formUri = "",
        mailTo = "ajaygaikwad945@gmail.com",
        mode = ReportingInteractionMode.SILENT,
        resToastText = R.string.crash_toast_text)

public class CrashReport extends Application {
    private static CrashReport myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        ACRA.init(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}