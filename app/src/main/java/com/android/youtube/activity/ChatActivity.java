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

import com.android.youtube.R;
import com.android.youtube.adapter.MsgListAdapter;
import com.android.youtube.netty.Const;
import com.android.youtube.netty.NettyClient;
import com.android.youtube.utils.MessageObserver;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pb.LogicExtGrpc;
import pb.LogicExtOuterClass;

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


        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.connect();

                            ManagedChannel channel = ManagedChannelBuilder.forAddress(Const.LOGIC_EXT_HOST, Const.MSG_SOCKET_PORT).usePlaintext().build();
                            LogicExtGrpc.LogicExtBlockingStub blockingStub = LogicExtGrpc.newBlockingStub(channel);
                            LogicExtOuterClass.RegisterDeviceReq registerDeviceReq = LogicExtOuterClass
                                    .RegisterDeviceReq
                                    .newBuilder()
                                    .setType(2)
                                    .setBrand("xiaomi")
                                    .setModel("10")
                                    .setSystemVersion("android10")
                                    .setSdkVersion("28").build();
                            LogicExtOuterClass.RegisterDeviceResp deviceResp = blockingStub.registerDevice(registerDeviceReq);
                            Log.i("oye", "run: "+deviceResp.getDeviceId());
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
