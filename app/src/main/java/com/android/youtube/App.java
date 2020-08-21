package com.android.youtube;

import android.app.Application;

import com.android.youtube.utils.DBUtils;

public class App extends Application {
    public static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        init();
    }

    private void init(){
        DBUtils.getInstance().init(this);
    }
}
