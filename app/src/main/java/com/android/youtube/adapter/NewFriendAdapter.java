package com.android.youtube.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.youtube.R;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.NewFriend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewFriendAdapter extends BaseRecycerViewAdapter<NewFriend, NewFriendAdapter.Holder>{
    public NewFriendAdapter(Context context, List<NewFriend> list) {
        super(context, list);
    }

    @Override
    public NewFriendAdapter.Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewFriendAdapter.Holder(inflater.inflate(R.layout.item_new_friend,parent,false));
    }

    @Override
    public void getBindViewHolder(NewFriendAdapter.Holder holder, int position) {
        NewFriend newFriend = list.get(position);
        holder.messageContent.setText(newFriend.getDescription());
        holder.name.setText(newFriend.getNickname());
        int newFriendStatus = newFriend.getNewFriendStatus();
        String status = "同意";
        if(newFriendStatus == 0){
             status = "同意";
        }else if(newFriendStatus == 1){
             status = "已添加";
        }
        holder.friend_status.setText(status);
        holder.friend_status.setOnClickListener(new View.OnClickListener() {
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
        private final TextView friend_status;

        public Holder(View itemView) {
            super(itemView);
            messageContent = ((TextView) itemView.findViewById(R.id.msg_content));
            name = ((TextView) itemView.findViewById(R.id.name));
            friend_status = ((TextView) itemView.findViewById(R.id.friend_status));
        }
    }
}
