package com.android.youtube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.entity.User;
import com.android.youtube.utils.DBUtils;
import com.android.youtube.utils.NetworkUtils;
import com.android.youtube.utils.SystemUtil;

import java.util.List;

import io.reactivex.functions.Consumer;
import pb.LogicExtOuterClass;
import pb.UserExtOuterClass;


public class SplashActivity extends AppCompatActivity {
    private String TAG = "SplashActivity";

    private EditText userName;
    private EditText userPhone;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
        setContentView(R.layout.activity_splash);

        initView();
        initData();
        initApi();

    }

    private void initData() {

    }


    private void registerDevice() {
        LogicExtOuterClass.RegisterDeviceReq registerDeviceReq = LogicExtOuterClass
                .RegisterDeviceReq
                .newBuilder()
                .setType(2)
                .setBrand(SystemUtil.getDeviceBrand())
                .setModel(SystemUtil.getSystemModel())
                .setSystemVersion(SystemUtil.getSystemVersion())
                .setSdkVersion("28")
                .build();


        NetworkUtils
                .getInstance().registerDevice(registerDeviceReq)
                .subscribe(new Consumer<LogicExtOuterClass.RegisterDeviceResp>() {
                    @Override
                    public void accept(LogicExtOuterClass.RegisterDeviceResp registerDeviceResp) {
                        Log.i(TAG, "run: " + registerDeviceResp.getDeviceId());
                        signIn(registerDeviceResp);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i(TAG, "register Deives: "+throwable.getMessage());
                    }
                });



    }

    private void signIn(LogicExtOuterClass.RegisterDeviceResp registerDeviceResp) {
        UserExtOuterClass.SignInReq signInReq = UserExtOuterClass
                .SignInReq
                .newBuilder()
                .setDeviceId(registerDeviceResp.getDeviceId())
                .setPhoneNumber(userPhone.getText().toString())
                .build();
        NetworkUtils.getInstance().signIn(signInReq)
                .subscribe(new Consumer<UserExtOuterClass.SignInResp>() {
                    @Override
                    public void accept(UserExtOuterClass.SignInResp loginResp) {
                        Log.i(TAG, "run: "+loginResp.getToken()+" "+loginResp.getUserId());

                        User user = new User();

                        user.setDeviceId((int) registerDeviceResp.getDeviceId());
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
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i(TAG, "sign: "+throwable.getMessage());
                    }
                });
    }

    private void initView() {
        userName = ((EditText) findViewById(R.id.user_name));
        userPhone = ((EditText) findViewById(R.id.user_phone));
        register = ((TextView) findViewById(R.id.reigister));
        findViewById(R.id.closePage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
