package com.android.youtube;

import android.app.Application;

import com.android.youtube.utils.DBUtils;

import pb.UserExtOuterClass;

public class App extends Application {
    public static App app;
    public static UserExtOuterClass.SignInResp signInResp;
    public static UserExtOuterClass.SignInResp signInResp1;
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
