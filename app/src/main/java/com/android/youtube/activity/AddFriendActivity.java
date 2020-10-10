package com.android.youtube.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.youtube.R;
import com.android.youtube.utils.NetworkUtils;

import io.reactivex.functions.Consumer;
import pb.LogicExtOuterClass;

public class AddFriendActivity extends BaseActivity {

    private ImageView back;
    private TextView addFriend;
    private EditText firendsId;
    private int userID;


    @Override
    public int setContentView() {
        return R.layout.activity_add_friend;
    }

    public void initData() {


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firendID = firendsId.getText().toString();
                if (TextUtils.isEmpty(firendID) && userID == 0) {

                    Toast.makeText(AddFriendActivity.this, "请输入朋友ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                LogicExtOuterClass.AddFriendReq friendReq = LogicExtOuterClass.AddFriendReq.newBuilder()
                        .setDescription("test"+(userID == 0 ? Long.parseLong(firendID) : userID))
                        .setFriendId(userID == 0 ? Long.parseLong(firendID) : userID)
                        .setRemarks("hhhhh")
                        .build();

                NetworkUtils.getInstance().addFriend(friendReq)
                        .subscribe(new Consumer<LogicExtOuterClass.AddFriendResp>() {
                            @Override
                            public void accept(LogicExtOuterClass.AddFriendResp addFriendResp) {
                                Log.i("ChatActivity", "run: " + addFriendResp.getSerializedSize());
                                Toast.makeText(AddFriendActivity.this, "添加朋友成功", Toast.LENGTH_SHORT).show();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Log.i("oye", "run: " + throwable);
                                Toast.makeText(AddFriendActivity.this, "不能重复添加朋友", Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        });
    }

    public void initView() {
        back = ((ImageView) findViewById(R.id.back));
        addFriend = ((TextView) findViewById(R.id.add_friend));
        firendsId = ((EditText) findViewById(R.id.friend_ids));
    }
}
