package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtube.R;
import com.android.youtube.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MsgListAdapter extends BaseRecycerViewAdapter<String, MsgListAdapter.Holder> {
    public MsgListAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public Holder getCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(inflater.inflate(viewType, parent, false));
    }

    @Override
    public void getBindViewHolder(final Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, v, holder);
                }
            }
        });

        holder.msg.setText(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        String content = list.get(position);
        if (position % 2 == 0) {
            return R.layout.item_chat_send;
        } else {
            return R.layout.item_chat_receive;
        }
    }

    public class Holder extends RecyclerView.ViewHolder {


        private final TextView msg;

        public Holder(View itemView) {
            super(itemView);
            msg = ((TextView) itemView.findViewById(R.id.msg));
        }
    }
}
