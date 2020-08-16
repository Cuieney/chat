package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.youtube.R;

import java.util.List;

public class ChatAdapter extends BaseRecycerViewAdapter<String,ChatAdapter.Holder>{
    public ChatAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public ChatAdapter.Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatAdapter.Holder(inflater.inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void getBindViewHolder(ChatAdapter.Holder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position,v,holder);
                }
            }
        });
    }

    public class Holder extends RecyclerView.ViewHolder {


        public Holder(View itemView) {
            super(itemView);

        }
    }
}
