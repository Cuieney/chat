package com.android.youtube.utils;

import android.content.Context;

import com.android.youtube.entity.DaoMaster;

public class DBUtils {
    private static DBUtils instance;
    private DaoMaster.DevOpenHelper helper;

    public synchronized static DBUtils getInstance() {
        synchronized (DBUtils.class) {
            if (instance == null) {
                instance = new DBUtils();
            }
        }
        return instance;
    }

    public void init(Context mContext){
        helper = new DaoMaster.DevOpenHelper(mContext, "chat-db");
    }
    private DBUtils(){
    }

    public DaoMaster.DevOpenHelper getDbHelper() {
        return helper;
    }

}
