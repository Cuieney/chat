package com.android.youtube.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.activity.InformationActivity;
import com.android.youtube.activity.MainActivity;
import com.android.youtube.entity.User;
import com.android.youtube.image.ImageLoader;

public class MeFragment  extends Fragment {
    private static String ARG_PARAM = "param_key";
    private String mParam;
    private MainActivity mActivity;
    private TextView wechatId;
    private TextView name;
    private ImageView head;
    private View userInfoContainer;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.me_fragment, container, false);
        wechatId = ((TextView) root.findViewById(R.id.info));
        name = ((TextView) root.findViewById(R.id.name));
        head = ((ImageView) root.findViewById(R.id.head));
        userInfoContainer = root.findViewById(R.id.user_info_container);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    public static MeFragment newInstance(String str) {
        MeFragment frag = new MeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        frag.setArguments(bundle);   //设置参数
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData(){
        User user = App.user;
        wechatId.setText("微信号："+user.getUserId()+" 设备号："+user.getDeviceId());
        name.setText(user.getUserName());
        ImageLoader.getInstance().load(user.getUserImage()).placeholder(R.drawable.head).into(head);
        userInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, InformationActivity.class));
            }
        });
    }

}