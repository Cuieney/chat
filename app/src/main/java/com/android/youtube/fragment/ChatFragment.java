package com.android.youtube.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.youtube.R;
import com.android.youtube.activity.AddFriendActivity;
import com.android.youtube.activity.ChatActivity;
import com.android.youtube.activity.MainActivity;
import com.android.youtube.adapter.BaseRecycerViewAdapter;
import com.android.youtube.adapter.ChatAdapter;
import com.android.youtube.entity.Message;
import com.android.youtube.utils.DBUtils;
import com.android.youtube.utils.RxBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ChatFragment   extends Fragment {
    private static String ARG_PARAM = "param_key";
    private String mParam;
    private MainActivity mActivity;
    private RecyclerView view;
    private View add_friend;
    private List<Message> list;
    private ChatAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chat_fragment, container, false);
        view = root.findViewById(R.id.list);
        add_friend = root.findViewById(R.id.add_friend);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    public static ChatFragment newInstance(String str) {
        ChatFragment frag = new ChatFragment();
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

    private void updateMessageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = DBUtils.getInstance().getMessageList();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ChatAdapter(mActivity, list);
                        adapter.setOnItemClickListener(new BaseRecycerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                                Intent intent = new Intent(mActivity, AddFriendActivity.class);

                                intent.putExtra("userName", list.get(position).getSender_id());
                                startActivity(intent);
                            }
                        });
                        view.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }


    private void initData(){
        view.setLayoutManager(new LinearLayoutManager(mActivity));
        list =new ArrayList<>();

        adapter = new ChatAdapter(mActivity, list);

        view.setAdapter(adapter);

        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, AddFriendActivity.class));
            }
        });

        RxBus.getDefault().toObservable(Message.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Message>() {
            @Override
            public void call(Message mBean) {
                updateMessageData();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message mBean) {
//        updateMessageData();
    }

}