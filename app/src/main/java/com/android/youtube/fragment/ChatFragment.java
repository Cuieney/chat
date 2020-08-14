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

import com.android.youtube.activity.ChatActivity;
import com.android.youtube.activity.MainActivity;
import com.android.youtube.R;
import com.android.youtube.adapter.BaseRecycerViewAdapter;
import com.android.youtube.adapter.ChatListAdapter;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private static String ARG_PARAM = "param_key";

    private MainActivity mActivity;
    private String mParam;
    private RecyclerView view;

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = ((MainActivity) context);
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragmeng_video, container, false);
        view = root.findViewById(R.id.video_list);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData(){
        view.setLayoutManager(new LinearLayoutManager(mActivity));
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i+"");
        }
        ChatListAdapter adapter = new ChatListAdapter(mActivity, list);
        adapter.setOnItemClickListener(new BaseRecycerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("userName","username");
                intent.putExtra("userId","userId");
                startActivity(intent);
            }
        });
        view.setAdapter(adapter);

    }

}
