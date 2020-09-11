package com.android.youtube.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.youtube.activity.ChatActivity;
import com.android.youtube.activity.NewFriendActivity;
import com.android.youtube.adapter.ChatAdapter;
import com.android.youtube.adapter.ContactAdapter;
import com.android.youtube.R;
import com.android.youtube.activity.MainActivity;
import com.android.youtube.adapter.BaseRecycerViewAdapter;
import com.android.youtube.entity.Friend;
import com.android.youtube.entity.Message;
import com.android.youtube.utils.Const;
import com.android.youtube.utils.DBUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    private static String ARG_PARAM = "param_key";
    private String mParam;
    private MainActivity mActivity;
    private RecyclerView view;
    private List<Friend> list;
    private ContactAdapter adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.contact_fragment, container, false);
        view = root.findViewById(R.id.list);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    public static ContactFragment newInstance(String str) {
        ContactFragment frag = new ContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        frag.setArguments(bundle);   //设置参数
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();


        updateMessageData();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
    private void updateMessageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = DBUtils.getInstance().getFriendList();
                list.add(0,new Friend());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ContactAdapter(mActivity, list);
                        adapter.setOnItemClickListener(new BaseRecycerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                                Log.i("ContactFragment", "onItemClick: ");
                                Friend friend = list.get(position);
                                if (position == 0) {
                                    startActivity(new Intent(mActivity, NewFriendActivity.class));
                                }else{
                                    Intent intent = new Intent(mActivity, ChatActivity.class);
                                    intent.putExtra(Const.USER_EXT_ID, friend.getUser_id());
                                    startActivity(intent);
                                }
                            }
                        });
                        view.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    private void initData() {
        view.setLayoutManager(new LinearLayoutManager(mActivity));
        ArrayList<Friend> list = new ArrayList<>();
        ContactAdapter adapter = new ContactAdapter(mActivity, list);
        adapter.setOnItemClickListener(new BaseRecycerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                Log.i("ContactFragment", "onItemClick: ");
                if (position == 0) {
                    startActivity(new Intent(mActivity, NewFriendActivity.class));

                }
            }
        });
        view.setAdapter(adapter);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Friend mBean) {
        updateMessageData();
    }
}