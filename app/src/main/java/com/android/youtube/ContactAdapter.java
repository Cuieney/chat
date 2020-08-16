package com.android.youtube;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.youtube.adapter.BaseRecycerViewAdapter;

import java.util.List;

public class ContactAdapter extends BaseRecycerViewAdapter<String, RecyclerView.ViewHolder> {
    public ContactAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder getCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.item_contract_title) {
            return new HolderTitle(inflater.inflate(viewType, parent, false));
        }
        return new Holder(inflater.inflate(viewType, parent, false));
    }

    @Override
    public void getBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 4) {
            ((HolderTitle) holder).setText("企业微信");
        }

        if (position == 6) {
            ((HolderTitle) holder).setText("A");
        }

        if (position == 8) {
            ((HolderTitle) holder).setText("B");
        }
        switch (position) {
            case 0:
                ((Holder) holder).setText("新的朋友");
                ((Holder) holder).img.setImageResource(R.drawable.placeholder);
                break;
            case 1:
                ((Holder) holder).setText("群聊");
                ((Holder) holder).img.setImageResource(R.drawable.placeholder);
                break;
            case 2:
                ((Holder) holder).setText("标签");
                ((Holder) holder).img.setImageResource(R.drawable.placeholder);
                break;
            case 3:
                ((Holder) holder).setText("公众号");
                ((Holder) holder).img.setImageResource(R.drawable.placeholder);
                break;
            case 5:
                ((Holder) holder).setText("上海股份公司");


                break;
            case 7:
                ((Holder) holder).setText("阿里云");
                break;
            case 9:
                ((Holder) holder).setText("百度云");
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 4 || position == 6 || position == 8) {
            return R.layout.item_contract_title;
        }

        return R.layout.item_contact;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public void setText(String text) {
            this.text.setText(text);
        }

        private TextView text;
        private ImageView img;

        public Holder(View itemView) {
            super(itemView);
            text = ((TextView) itemView.findViewById(R.id.name));
            img = ((ImageView) itemView.findViewById(R.id.img));

        }
    }

    public class HolderTitle extends RecyclerView.ViewHolder {


        public void setText(String text) {
            this.text.setText(text);
        }

        private TextView text;

        public HolderTitle(View itemView) {
            super(itemView);
            text = ((TextView) itemView.findViewById(R.id.text));
        }

    }
}
