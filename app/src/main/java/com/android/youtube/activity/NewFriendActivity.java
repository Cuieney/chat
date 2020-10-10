package com.android.youtube.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.adapter.BaseRecycerViewAdapter;
import com.android.youtube.adapter.NewFriendAdapter;
import com.android.youtube.entity.Friend;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.NewFriend;
import com.android.youtube.netty.Const;
import com.android.youtube.utils.DBUtils;
import com.android.youtube.utils.JwtCallCredential;
import com.android.youtube.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.functions.Consumer;
import pb.LogicExtGrpc;
import pb.LogicExtOuterClass;

public class NewFriendActivity extends BaseActivity {


    private RecyclerView newFriendList;
    private ImageView back;
    private List<NewFriend> newFriends;
    private NewFriendAdapter friendAdapter;

    @Override
    public int setContentView() {
        return R.layout.activity_new_friend;
    }

    @Override
    public void initView() {
        newFriendList = ((RecyclerView) findViewById(R.id.friend_list));
        back = ((ImageView) findViewById(R.id.back));
    }

    @Override
    public void initData() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        newFriendList.setLayoutManager(new LinearLayoutManager(this));
        newFriends = new ArrayList<NewFriend>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NewFriend> newFriendList = DBUtils.getInstance().getNewFriendList();
                newFriends.clear();
                newFriends.addAll(newFriendList);
                refreshList();
            }
        }).start();

    }

    private void refreshList() {
        friendAdapter = new NewFriendAdapter(this, newFriends);
        newFriendList.setAdapter(friendAdapter);

        friendAdapter.setOnItemClickListener(new BaseRecycerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                agreeFriend(newFriends.get(position), position);
            }
        });
    }

    private void agreeFriend(NewFriend friend, int position) {
        LogicExtOuterClass.AgreeAddFriendReq friendReq = LogicExtOuterClass.AgreeAddFriendReq.newBuilder()
                .setUserId(friend.getFriend_id())
                .setRemarks("xxx")
                .build();


        NetworkUtils.getInstance().agreeAddFriend(friendReq)
                .subscribe(new Consumer<LogicExtOuterClass.AgreeAddFriendResp>() {
                    @Override
                    public void accept(LogicExtOuterClass.AgreeAddFriendResp agreeAddFriendResp) {
                        Log.i("NewFriendActivity", "run: " + agreeAddFriendResp.getSerializedSize());
                        friend.setNewFriendStatus(1);

                        DBUtils.getInstance().insertNewFriends(friend);

                        newFriends.remove(position);

                        Friend newfrined = new Friend();
                        friend.setFriend_id(friend.getFriend_id());
                        friend.setNickname(friend.getNickname());
                        DBUtils.getInstance().insertFriends(newfrined);
                        EventBus.getDefault().post(newfrined);

                        sectionRefresh(friend);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i("oye", "agreeFriend: " + throwable.getMessage());
                    }
                });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewFriend mBean) {
        sectionRefresh(mBean);
    }

    private void sectionRefresh(NewFriend mBean) {
        newFriends.add(mBean);
        refreshList();
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }
}
