package com.huxq17.xprefs.example;

import android.app.Application;

import com.huxq17.xprefs.XPrefs;

/**
 * Created by huxq17 on 2016/6/23.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XPrefs.bind(this);
    }
}
