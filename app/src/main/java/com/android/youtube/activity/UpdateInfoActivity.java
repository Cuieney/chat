package com.android.youtube.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.entity.User;
import com.android.youtube.utils.NetworkUtils;


import io.reactivex.functions.Consumer;
import pb.UserExtOuterClass;

public class UpdateInfoActivity extends BaseActivity {

    private View back;
    private TextView save;
    private EditText username;

    @Override
    public int setContentView() {
        return R.layout.activity_update_information;
    }

    @Override
    public void initView() {
        back = findViewById(R.id.back);
        save = ((TextView) findViewById(R.id.save));
        username = ((EditText) findViewById(R.id.user_id));
    }

    @Override
    public void initData() {
        User user = App.user;
        username.setText(user.getUserName()+"");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(UpdateInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserExtOuterClass.UpdateUserReq userReq = UserExtOuterClass.UpdateUserReq.newBuilder()
                        .setNickname(name).build();
                NetworkUtils.getInstance().updateUserInfo(userReq)

                        .subscribe(new Consumer<UserExtOuterClass.UpdateUserResp>() {
                            @Override
                            public void accept(UserExtOuterClass.UpdateUserResp updateUserResp) {
                                Toast.makeText(UpdateInfoActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                user.setUserName(name);
                                App.user = user;
                                finish();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Log.i("updateUserInfo", "call: "+throwable);
                            }
                        });
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
