package com.android.youtube;

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
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        initData();


    }


    private void initView() {
        tabbar = (CommonTabLayout) findViewById(R.id.tl_2);
        recyclerView = findViewById(R.id.recommend_list);

        videoPlayer = (TextureVideoView) findViewById(R.id.video_view);

        mYouTubeVideoView = (YouTubeVideoView) findViewById(R.id.youtube_view);

        mYouTubeVideoView.setCallback(this);
    }


    private void initData() {
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            fragmentList.add(VideoFragment.newInstance(mTitles[i]));
        }
        tabbar.setTabData(mTabEntities);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i + "");
        }
        recyclerView.setAdapter(new RecommendAdapter(this, list));
        currentFragment = fragmentList.get(0);
        switchFragment(currentFragment).commit();
        tabbar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
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
        Log.i("oye", "playVideo: "+url);
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
        Toast.makeTexgit initt(this, "video destroy", Toast.LENGTH_SHORT).show();
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
