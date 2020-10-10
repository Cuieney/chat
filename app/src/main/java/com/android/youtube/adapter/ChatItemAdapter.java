package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.entity.Message;
import com.android.youtube.image.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatItemAdapter extends BaseRecycerViewAdapter<Message, ChatItemAdapter.Holder>{
    public ChatItemAdapter(Context context, List<Message> list) {
        super(context, list);
    }

    @Override
    public ChatItemAdapter.Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatItemAdapter.Holder(inflater.inflate(viewType,parent,false));
    }
    public  String dateToStamp(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time_Date = sdf.format(new Date(time));
        return time_Date;
    }


    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);
        long sender_id = message.getSender_id();
        if(sender_id == App.user.getUserId()){
            return R.layout.item_chat_right;
        }
        return R.layout.item_chat_left;
    }

    @Override
    public void getBindViewHolder(ChatItemAdapter.Holder holder, int position) {
        holder.content.setText(list.get(position).getMessage_content());
        holder.time.setText(dateToStamp(list.get(position).getSend_time()));
        int itemViewType = getItemViewType(position);
        if(itemViewType == R.layout.item_chat_right){
            ImageLoader.getInstance().load(App.user.getUserImage()).into(holder.head_img);

        }
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

        private final TextView time;
        private final TextView content;
        private final ImageView head_img;

        public Holder(View itemView) {
            super(itemView);
            time = ((TextView) itemView.findViewById(R.id.time));
            content = ((TextView) itemView.findViewById(R.id.content));
            head_img = ((ImageView) itemView.findViewById(R.id.head_img));
        }
    }
}
