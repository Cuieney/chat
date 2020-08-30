package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.youtube.R;
import com.android.youtube.entity.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends BaseRecycerViewAdapter<Message,ChatAdapter.Holder>{
    public ChatAdapter(Context context, List<Message> list) {
        super(context, list);
    }

    @Override
    public ChatAdapter.Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatAdapter.Holder(inflater.inflate(R.layout.item_chat,parent,false));
    }
    public  String dateToStamp(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_Date = sdf.format(new Date(time));
        return time_Date;
    }

    @Override
    public void getBindViewHolder(ChatAdapter.Holder holder, int position) {
        holder.messageContent.setText(list.get(position).getMessage_content());
        holder.name.setText(list.get(position).getSender_id()+"");
        holder.time.setText(dateToStamp(list.get(position).getSend_time()));
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

        private final TextView messageContent;
        private final TextView name;
        private final TextView time;

        public Holder(View itemView) {
            super(itemView);
            messageContent = ((TextView) itemView.findViewById(R.id.msg_content));
            name = ((TextView) itemView.findViewById(R.id.name));
            time = ((TextView) itemView.findViewById(R.id.time));
        }
    }
}
