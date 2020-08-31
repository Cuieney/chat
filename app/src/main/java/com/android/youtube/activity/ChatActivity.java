package com.android.youtube.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.adapter.ChatItemAdapter;
import com.android.youtube.adapter.MsgListAdapter;
import com.android.youtube.entity.Message;
import com.android.youtube.netty.Const;
import com.android.youtube.utils.DBUtils;
import com.android.youtube.utils.JwtCallCredential;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pb.ConnExt;
import pb.LogicExtGrpc;
import pb.LogicExtOuterClass;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView msgList;
    private ImageView back;
    private Button sendMsg;

    private TextView userName;
    private EditText inputMsg;

    private int userID;
    private List<Message> messageArrayList;
    private ChatItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();

        setContentView(R.layout.activity_chat);
        getRootView(this).setPadding(0, getStatusBarHeight(), 0, 0);
        findViewById(R.id.content).setBackgroundColor(Color.parseColor("#EDEDED"));
        initView();
        initData();
    }

    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 確認取消半透明設置。
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // 跟系統表示要渲染 system bar 背景。
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources()
                .getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private void initData() {
        userID = getIntent().getIntExtra("userName", 0);
        updateData();

        msgList.setLayoutManager(new LinearLayoutManager(this));
        MsgListAdapter adapter = new MsgListAdapter(this, new ArrayList<String>());
        msgList.setAdapter(adapter);
        userName.setText(userID + "");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String text = inputMsg.getText().toString();
                            ConnExt.Text xxxx = ConnExt.Text.newBuilder().setText(text).build();
                            ManagedChannel loginChannel = ManagedChannelBuilder.forAddress(Const.LOGIC_EXT_HOST, Const.MSG_SOCKET_PORT).usePlaintext().build();
                            long currentTime = System.currentTimeMillis();
                            LogicExtOuterClass.SendMessageReq build = LogicExtOuterClass
                                    .SendMessageReq
                                    .newBuilder()
                                    .setMessageType(ConnExt.MessageType.MT_TEXT)
                                    .setSendTime(currentTime)
                                    .setReceiverId(userID)
                                    .setIsPersist(true)
                                    .setMessageContent(xxxx.getTextBytes())
                                    .setReceiverType(ConnExt.ReceiverType.RT_USER)
                                    .build();

                            Message entity = new Message();
                            entity.setMessage_content(text);
                            entity.setReceiver_id(userID);
                            entity.setMessage_type(1);
                            entity.setReceiver_type(1);
                            entity.setSend_time(currentTime);
                            entity.setSender_type(2);
                            entity.setStatus(1);
                            entity.setSender_id(App.user.getUserId());
                            LogicExtOuterClass.SendMessageResp resp = LogicExtGrpc.newBlockingStub(loginChannel).withCallCredentials(new JwtCallCredential()).sendMessage(build);
                            Log.i("ChatActivity", "run: " + resp.getSeq());

                            DBUtils.getInstance().insertMessage(entity);

                            loginChannel.shutdownNow();


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inputMsg.setText("");
                                    updateData1(entity);
                                }
                            });


                        } catch (Exception e) {
                            Log.i("oye", "run: " + e);

                        }
                    }

                }).start();
            }
        });

    }

    private void updateData1(Message entity) {
//        messageArrayList.add(entity);
//        adapter = new ChatItemAdapter(ChatActivity.this, messageArrayList);
//        msgList.setAdapter(adapter);
//        msgList.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Message> messageListByUserId = DBUtils.getInstance().getMessageListByUserId(userID, App.user.getUserId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageArrayList.clear();
                        messageArrayList.addAll(messageListByUserId);

                        adapter = new ChatItemAdapter(ChatActivity.this, messageArrayList);
                        msgList.setAdapter(adapter);

                        msgList.scrollToPosition(adapter.getItemCount() - 1);
                    }

                    ;
                });

            }
        }).start();
    }

    private void initView() {
        msgList = ((RecyclerView) findViewById(R.id.msg_list));
        back = ((ImageView) findViewById(R.id.back));
        sendMsg = ((Button) findViewById(R.id.send_msg));

        userName = ((TextView) findViewById(R.id.user_name));
        inputMsg = ((EditText) findViewById(R.id.msg_content));
        msgList.setLayoutManager(new LinearLayoutManager(this));

        messageArrayList = new ArrayList<>();
        adapter = new ChatItemAdapter(ChatActivity.this, messageArrayList);

        msgList.setAdapter(adapter);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message mBean) {
//        if (mBean.getSender_id() == userID) {
//            updateData1(mBean);
//        }
    }

    ;

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
