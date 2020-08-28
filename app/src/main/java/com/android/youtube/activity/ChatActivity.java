package com.android.youtube.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText firendsId;
    private int userID;

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
        userID = getIntent().getIntExtra("userName", 0);
        msgList.setLayoutManager(new LinearLayoutManager(this));
        MsgListAdapter adapter = new MsgListAdapter(this, new ArrayList<String>());
        msgList.setAdapter(adapter);
        userName.setText(userID+"");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firendID = firendsId.getText().toString();
                if (TextUtils.isEmpty(firendID) && userID == 0) {

                    Toast.makeText(ChatActivity.this, "请输入朋友ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ConnExt.Text xxxx = ConnExt.Text.newBuilder().setText(inputMsg.getText().toString()).build();
                            ManagedChannel loginChannel = ManagedChannelBuilder.forAddress(Const.LOGIC_EXT_HOST, Const.MSG_SOCKET_PORT).usePlaintext().build();
                            LogicExtOuterClass.SendMessageReq build = LogicExtOuterClass.SendMessageReq.newBuilder().setMessageType(ConnExt.MessageType.MT_TEXT)
                                    .setSendTime(System.currentTimeMillis())
                                    .setReceiverId(userID == 0 ? Integer.parseInt(firendID) : userID)
                                    .setIsPersist(true)
                                    .setMessageContent(xxxx.getTextBytes())
                                    .setReceiverType(ConnExt.ReceiverType.RT_USER)
                                    .build();
                            Log.i("ChatActivity", "run: "+(userID == 0 ? Integer.parseInt(firendID) : userID));
                            LogicExtOuterClass.SendMessageResp resp = LogicExtGrpc.newBlockingStub(loginChannel).withCallCredentials(new JwtCallCredential()).sendMessage(build);
                            Log.i("ChatActivity", "run: " + resp.getSeq());
                            loginChannel.shutdownNow();


                        } catch (Exception e) {
                            Log.i("oye", "run: " + e);

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
        firendsId = ((EditText) findViewById(R.id.friend_ids));
    }
}
