package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtube.utils.CircleTransform;
import com.android.youtube.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatListAdapter extends BaseRecycerViewAdapter<String, ChatListAdapter.Holder>{
    public ChatListAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.item_chat,parent,false));
    }

    @Override
    public void getBindViewHolder(final Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position,v,holder);
                }
            }
        });
        Picasso.get().load(R.drawable.head).transform(new CircleTransform())
                .into(holder.head);
    }

    public class Holder extends RecyclerView.ViewHolder {


        private  ImageView head;
        private  TextView friendName,firendLastMsg,friendTime;

        public Holder(View itemView) {
            super(itemView);
//            head = ((ImageView) itemView.findViewById(R.id.friend_img));
//            friendName = ((TextView) itemView.findViewById(R.id.friend_name));
//            firendLastMsg = ((TextView) itemView.findViewById(R.id.friend_last_msg));
//            friendTime = ((TextView) itemView.findViewById(R.id.time));
        }
    }
}
