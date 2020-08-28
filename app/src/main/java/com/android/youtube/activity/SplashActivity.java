package com.android.youtube.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.adapter.RecommendAdapter;
import com.android.youtube.customview.TextureVideoView;
import com.android.youtube.customview.YouTubeVideoView;
import com.android.youtube.entity.User;
import com.android.youtube.fragment.ChatFragment;
import com.android.youtube.fragment.ContactFragment;
import com.android.youtube.fragment.MeFragment;
import com.android.youtube.fragment.VideoFragment;
import com.android.youtube.model.TabEntity;
import com.android.youtube.netty.Const;
import com.android.youtube.utils.DBUtils;
import com.android.youtube.utils.SystemUtil;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pb.LogicExtGrpc;
import pb.LogicExtOuterClass;
import pb.UserExtGrpc;
import pb.UserExtOuterClass;


public class SplashActivity extends AppCompatActivity {
    private String TAG = "SplashActivity";

    private EditText userName;
    private EditText userPhone;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        initData();
        initApi();

    }

    private void initData() {

    }


    private void registerDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(Const.LOGIC_EXT_HOST, Const.MSG_SOCKET_PORT).usePlaintext().build();
                LogicExtGrpc.LogicExtBlockingStub blockingStub = LogicExtGrpc.newBlockingStub(channel);
                LogicExtOuterClass.RegisterDeviceReq registerDeviceReq = LogicExtOuterClass
                        .RegisterDeviceReq
                        .newBuilder()
                        .setType(2)
                        .setBrand(SystemUtil.getDeviceBrand())
                        .setModel(SystemUtil.getSystemModel())
                        .setSystemVersion(SystemUtil.getSystemVersion())
                        .setSdkVersion("28")
                        .build();
                LogicExtOuterClass.RegisterDeviceResp deviceResp = blockingStub.registerDevice(registerDeviceReq);
                Log.i(TAG, "run: " + deviceResp.getDeviceId());
                channel.shutdownNow();


                ManagedChannel loginChannel = ManagedChannelBuilder.forAddress(Const.USER_EXT_HOST, Const.USER_EXT_PORT).usePlaintext().build();
                UserExtGrpc.UserExtBlockingStub loginStub = UserExtGrpc.newBlockingStub(loginChannel);
                UserExtOuterClass.SignInReq signInReq = UserExtOuterClass
                        .SignInReq
                        .newBuilder()
                        .setDeviceId(deviceResp.getDeviceId())
                        .setPhoneNumber(userPhone.getText().toString())
                        .build();
                UserExtOuterClass.SignInResp loginResp = loginStub.signIn(signInReq);
                Log.i(TAG, "run: "+loginResp.getToken()+" "+loginResp.getUserId());

                loginChannel.shutdownNow();

                User user = new User();

                user.setDeviceId((int) deviceResp.getDeviceId());
                user.setUserPhone(userPhone.getText().toString());
                user.setUserId((int) loginResp.getUserId());
                user.setToken(loginResp.getToken());
                user.setUserName(userName.getText().toString());

                DBUtils.getInstance().insertUser(user);



                List<User> userList = DBUtils.getInstance().getUser();
                Log.i(TAG, "run: "+userList.size());
                if (userList.size() > 0) {
                    App.user = userList.get(0);
                }


                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }).start();


    }

    private void initView() {
        userName = ((EditText) findViewById(R.id.user_name));
        userPhone = ((EditText) findViewById(R.id.user_phone));
        register = ((Button) findViewById(R.id.reigister));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userName.getText().toString();
                String phone = userPhone.getText().toString();
                Log.i(TAG, "onClick: "+name+" "+phone);

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                    registerDevice();
                }
            }
        });

    }

    private void initApi() {


    }

    public void setStatusBarTransparent() {

    }


}
