package com.android.youtube.utils;

import android.content.Context;

import com.android.youtube.entity.DaoMaster;
import com.android.youtube.entity.DaoSession;
import com.android.youtube.entity.Devices;
import com.android.youtube.entity.DevicesDao;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.MessageDao;
import com.android.youtube.entity.User;
import com.android.youtube.entity.UserDao;

import java.util.List;

public class DBUtils {
    private static DBUtils instance;
    private DaoMaster.DevOpenHelper helper;
    private DaoSession daoSession;

    public synchronized static DBUtils getInstance() {
        synchronized (DBUtils.class) {
            if (instance == null) {
                instance = new DBUtils();
            }
        }
        return instance;
    }

    public void init(Context mContext) {
        helper = new DaoMaster.DevOpenHelper(mContext, "chat-db");
        daoSession = new DaoMaster(helper.getWritableDatabase()).newSession();
    }

    private DBUtils() {
    }

    public DaoMaster.DevOpenHelper getDbHelper() {
        return helper;
    }

    public void insertDevice(Devices devices) {
        DevicesDao devicesDao = daoSession.getDevicesDao();
        devicesDao.insert(devices);
    }

    public void insertUser(User user) {
        UserDao userDao = daoSession.getUserDao();
        userDao.insert(user);
    }


    public List<Message> getMessageList() {
        return daoSession.loadAll(Message.class);
    }


    public List<User> getUser() {
        return daoSession.loadAll(User.class);
    }

    public void insertMessage(Message message) {
        MessageDao messageDao = daoSession.getMessageDao();
        messageDao.insert(message);
    }


}
