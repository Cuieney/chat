package com.android.youtube.utils;

import android.content.Context;
import android.util.Log;

import com.android.youtube.App;
import com.android.youtube.entity.DaoMaster;
import com.android.youtube.entity.DaoSession;
import com.android.youtube.entity.Devices;
import com.android.youtube.entity.DevicesDao;
import com.android.youtube.entity.Friend;
import com.android.youtube.entity.FriendDao;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.MessageDao;
import com.android.youtube.entity.NewFriend;
import com.android.youtube.entity.NewFriendDao;
import com.android.youtube.entity.User;
import com.android.youtube.entity.UserDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DBUtils {
    private static DBUtils instance;
    private DaoMaster.DevOpenHelper helper;
    private DaoSession daoSession;
    private String TAG = "DBUtils";

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
        userDao.insertOrReplace(user);
    }


    public List<Message> getMessageList() {
        List<Message> list = new ArrayList<Message>();
        try {
            MessageDao messageDao = daoSession.getMessageDao();
            List<Message> senderList = messageDao.queryBuilder()
                    .where(MessageDao.Properties.Sender_id.notEq(App.user.getUserId())).list();
            list.addAll(senderList);

            Collections.sort(list, new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    long t1 = o2.getSend_time() - o1.getSend_time();
                    return (int) t1;
                }
            });
        }catch (Exception e){
            Log.i(TAG, "getMessageList: "+e.getMessage());
        }
        return list;
    }

    public List<Message> getMessageFromUser() {
        List<Message> list = new ArrayList<Message>();
        try {
            List<Friend> friends = daoSession.loadAll(Friend.class);
            Friend me = new Friend();
            me.setUser_id((long) App.user.getUserId());
            friends.add(me);
            MessageDao messageDao = daoSession.getMessageDao();
            for (Friend friend : friends) {
                long user_id = friend.getUser_id();
                List<Message> senderList = messageDao.queryBuilder()
                        .where(MessageDao.Properties.Sender_id.eq(user_id)).list();

                Collections.sort(senderList, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        long t1 = o2.getSend_time() - o1.getSend_time();
                        return (int) t1;
                    }
                });
                if (senderList.size() > 0) {
                    list.add(senderList.get(0));
                }
            }
        }catch (Exception e){
            Log.i(TAG, "getMessageList: "+e.getMessage());
        }
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                long t1 = o2.getSend_time() - o1.getSend_time();
                return (int) t1;
            }
        });
        return list;
    }


    public List<Message> getMessageListByUserId(long senderID,long receiverID) {
        List<Message> list = new ArrayList<Message>();
        try {
            MessageDao messageDao = daoSession.getMessageDao();
            List<Message> senderList = messageDao.queryBuilder()
                    .where(MessageDao.Properties.Sender_id.eq(senderID)).list();
            List<Message> receiverList = messageDao.queryBuilder()
                    .where(MessageDao.Properties.Sender_id.eq(receiverID)).list();
            list.addAll(senderList);
            list.addAll(receiverList);

        }catch (Exception e){
            Log.i(TAG, "getMessageList: "+e.getMessage());
        }
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                long t1 = o1.getSend_time() - o2.getSend_time();
                return (int) t1;
            }
        });
        return list;
    }

    public List<NewFriend> getNewFriendList(){
        return  daoSession.loadAll(NewFriend.class);
    }

    public List<Friend> getFriendList(){
        return  daoSession.loadAll(Friend.class);
    }

    public List<User> getUser() {
        return daoSession.loadAll(User.class);
    }

    public void insertMessage(Message message) {
        MessageDao messageDao = daoSession.getMessageDao();
        messageDao.insert(message);
    }

    public void insertFriends(Friend friend) {
        FriendDao dao = daoSession.getFriendDao();
        dao.insertOrReplace(friend);
    }

    public void insertNewFriends(NewFriend friend) {
        NewFriendDao dao = daoSession.getNewFriendDao();
        dao.insertOrReplace(friend);
    }


}
