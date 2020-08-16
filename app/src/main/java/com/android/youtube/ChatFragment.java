package com.android.youtube;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ChatFragment   extends Fragment {
    private static String ARG_PARAM = "param_key";
    private String mParam;
    private MainActivity mActivity;
    private RecyclerView view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chat_fragment, container, false);
        view = root.findViewById(R.id.list);
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

    private void initData(){
        view.setLayoutManager(new LinearLayoutManager(mActivity));
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i+"");
        }
        ChatAdapter adapter = new ChatAdapter(mActivity, list);
        adapter.setOnItemClickListener(new BaseRecycerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                mActivity.playVideo(position+" ");
            }
        });
        view.setAdapter(adapter);
    }

}