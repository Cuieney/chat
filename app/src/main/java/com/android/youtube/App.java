package com.android.youtube;

import android.app.Application;

import com.android.youtube.entity.User;
import com.android.youtube.utils.DBUtils;

import java.util.List;


public class App extends Application {
    public static App app;
    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        init();
    }

    private void init() {
        DBUtils.getInstance().init(this);
        List<User> userList = DBUtils.getInstance().getUser();
        if (userList.size() > 0) {
            user = userList.get(0);
        }
    }

}
