package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.youtube.R;

import java.util.List;

public class RecommendAdapter extends BaseRecycerViewAdapter<String,RecommendAdapter.Holder>{
    public RecommendAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.item_adapter,parent,false));
    }

    @Override
    public void getBindViewHolder(Holder holder, int position) {

    }

    public class Holder extends RecyclerView.ViewHolder {


        public Holder(View itemView) {
            super(itemView);

        }
    }
}
