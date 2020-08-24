package com.android.youtube.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.fragment.ContactFragment;
import com.android.youtube.fragment.MeFragment;
import com.android.youtube.model.TabEntity;
import com.android.youtube.customview.TextureVideoView;
import com.android.youtube.customview.YouTubeVideoView;
import com.android.youtube.adapter.RecommendAdapter;
import com.android.youtube.fragment.ChatFragment;
import com.android.youtube.fragment.VideoFragment;
import com.android.youtube.netty.Const;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pb.UserExtGrpc;
import pb.UserExtOuterClass;


public class MainActivity extends AppCompatActivity implements YouTubeVideoView.Callback {

    private TextureVideoView videoPlayer;

    private YouTubeVideoView mYouTubeVideoView;
    private RecyclerView recyclerView;
    private String[] mTitles = {"消息", "朋友", "视频", "我的"};
    private CommonTabLayout tabbar;

    private int[] mIconUnselectIds = {
            R.drawable.chat, R.drawable.friend,
            R.drawable.video, R.drawable.me};
    private int[] mIconSelectIds = {
            R.drawable.chat_select, R.drawable.friend_select,
            R.drawable.video_select, R.drawable.me_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private List<Fragment> fragmentList = new ArrayList<>();
    private FrameLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
        setContentView(R.layout.activity_main);
        getRootView(this).setPadding(0,getStatusBarHeight(),0,0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }

        initView();
        initData();
        initApi();

    }

    private void initApi() {

        ManagedChannel channel = ManagedChannelBuilder.forAddress(Const.USER_EXT_HOST, Const.USER_EXT_PORT).usePlaintext().build();
        UserExtGrpc.UserExtBlockingStub blockingStub = UserExtGrpc.newBlockingStub(channel);
        UserExtOuterClass.SignInReq signInReq = UserExtOuterClass.SignInReq.newBuilder().setDeviceId(9).setPhoneNumber("18365268222").build();
        UserExtOuterClass.SignInResp resp = blockingStub.signIn(signInReq);

        App.signInResp = resp;
        Log.i("oye", "run: "+resp.getToken()+"  "+ resp.getUserId());
        channel.shutdownNow();


        ManagedChannel channel1 = ManagedChannelBuilder.forAddress(Const.USER_EXT_HOST, Const.USER_EXT_PORT).usePlaintext().build();
        UserExtGrpc.UserExtBlockingStub blockingStub1 = UserExtGrpc.newBlockingStub(channel1);
        UserExtOuterClass.SignInReq signInReq1 = UserExtOuterClass.SignInReq.newBuilder().setDeviceId(8).setPhoneNumber("18312345678").build();
        UserExtOuterClass.SignInResp resp1 = blockingStub1.signIn(signInReq1);

        App.signInResp1 = resp1;
        Log.i("oye", "run1: "+resp1.getToken()+"  "+ resp1.getUserId());
        channel1.shutdownNow();



    }

    public void setStatusBarTransparent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ // 4.4
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
    private static View getRootView(Activity context)
    {
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
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


    private void initView() {
        tabbar = (CommonTabLayout) findViewById(R.id.tl_2);
        recyclerView = findViewById(R.id.recommend_list);

        videoPlayer = (TextureVideoView) findViewById(R.id.video_view);

        mYouTubeVideoView = (YouTubeVideoView) findViewById(R.id.youtube_view);
        content = (FrameLayout) findViewById(R.id.content);

        mYouTubeVideoView.setCallback(this);
    }


    private void initData() {
        fragmentList.add(ChatFragment.newInstance(""));
        fragmentList.add(ContactFragment.newInstance(""));
        fragmentList.add(VideoFragment.newInstance(""));
        fragmentList.add(MeFragment.newInstance(""));

        currentFragment = fragmentList.get(0);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, currentFragment).commit();
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            if (i == 0) {
                fragmentList.add(ChatFragment.newInstance("1"));
            } else {
                fragmentList.add(VideoFragment.newInstance(mTitles[i]));

            }

        }
        tabbar.setTabData(mTabEntities);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i + "");
        }

        recyclerView.setAdapter(new RecommendAdapter(this, list));
        tabbar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 3) {
                    content.setBackgroundColor(Color.parseColor("#ffffff"));
                }else{
                    content.setBackgroundColor(Color.parseColor("#EDEDED"));
                }
                switchFragment(fragmentList.get(position)).commit();

            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private Fragment currentFragment;

    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.container, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }


    public void playVideo(String url) {
        Log.i("oye", "playVideo: " + url);
        mYouTubeVideoView.show();
        videoPlayer.stop();
        videoPlayer.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
        videoPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/b.mp4");
        videoPlayer.play();

    }


    @Override
    public void onVideoClick() {
        Toast.makeText(this, "click to pause/start", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onVideoViewHide() {
        Toast.makeText(this, "video destroy", Toast.LENGTH_SHORT).show();
        videoPlayer.stop();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mYouTubeVideoView.getNowStateScale() == 1f) {
            mYouTubeVideoView.goMin();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
