package com.android.youtube;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendHomeAdapter extends BaseRecycerViewAdapter<String,RecommendHomeAdapter.Holder>{
    public RecommendHomeAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public Holder getCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.item_home_adapter,parent,false));
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


        private final ImageView head;
        private final ImageView img;

        public Holder(View itemView) {
            super(itemView);
            head = ((ImageView) itemView.findViewById(R.id.head));
            img = ((ImageView) itemView.findViewById(R.id.img));

        }
    }
}
