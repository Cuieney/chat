package com.android.youtube.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.adapter.MsgListAdapter;
import com.android.youtube.netty.Const;
import com.android.youtube.netty.NettyClient;
import com.android.youtube.utils.JwtCallCredential;
import com.android.youtube.utils.MessageObserver;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pb.ConnExt;
import pb.LogicExtGrpc;
import pb.LogicExtOuterClass;
import pb.UserExtGrpc;
import pb.UserExtOuterClass;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView msgList;
    private Button back;
    private Button sendMsg;
    private TextView userName;
    private EditText inputMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
        initData();
    }

    private void initData() {
        String userNameTxt = getIntent().getStringExtra("userName");
        String userId = getIntent().getStringExtra("userId");
        msgList.setLayoutManager(new LinearLayoutManager(this));
        MsgListAdapter adapter = new MsgListAdapter(this, new ArrayList<String>());
        msgList.setAdapter(adapter);
        userName.setText(userNameTxt);
        NettyClient client = NettyClient.getInstance();
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
//                            ReceiverType receiver_type = 1; // 接收者类型，1：user;2:group
//                            int64 receiver_id = 2; // 用户id或者群组id
//                            repeated int64 to_user_ids = 3; // 需要@的用户id列表
//                            MessageType message_type = 4; // 消息类型
//                            bytes message_content = 5; // 消息内容
//                            int64 send_time = 6; // 消息发送时间戳，精确到毫秒
//                            bool is_persist = 7; // 是否将消息持久化到数据库
                            ConnExt.Text xxxx = ConnExt.Text.newBuilder().setText("xxxx").build();


//                            QLEMOZTXVXHKNDYCOVEAJDIXXZAHNDQSGFKVTHGQ  4

                            ManagedChannel loginChannel = ManagedChannelBuilder.forAddress(Const.LOGIC_EXT_HOST, Const.MSG_SOCKET_PORT).usePlaintext().build();

//                            LogicExtOuterClass.SendMessageReq build = LogicExtOuterClass.SendMessageReq.newBuilder().setMessageType(ConnExt.MessageType.MT_TEXT)
//                                    .setSendTime(System.currentTimeMillis())
//                                    .setReceiverId(App.signInResp.getUserId())
//                                    .setIsPersist(true)
//                                    .setMessageContent(xxxx.getTextBytes())
//                                    .setReceiverType(ConnExt.ReceiverType.RT_USER)
//                                    .build();

//                            LogicExtOuterClass.SendMessageResp resp = LogicExtGrpc.newBlockingStub(loginChannel).withCallCredentials(new JwtCallCredential()).sendMessage(build);
//                            Log.i("ChatActivity", "run: "+resp.getSeq());
                            loginChannel.shutdownNow();



                        } catch (Exception e) {
                            Log.i("oye", "run: "+e);

                        }
                    }

                }).start();
            }
        });
    }

    private void initView() {
        msgList = ((RecyclerView) findViewById(R.id.msg_list));
        back = ((Button) findViewById(R.id.back));
        sendMsg = ((Button) findViewById(R.id.send_msg));
        userName = ((TextView) findViewById(R.id.user_name));
        inputMsg = ((EditText) findViewById(R.id.msg_content));
    }
}
