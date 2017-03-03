package com.example.jinfei.retrofittest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.R;
import com.example.jinfei.retrofittest.SecondActivity;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.util.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.RecyclerViewHolder> {

    private List list;
    private Context context;
    private String type;


    public MyRecyclerViewAdapter(Context context, List list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    private void setLayout(RecyclerViewHolder holder, String title, String content, String imagePath, final int id) {
        Util.setImage(context, imagePath, holder.iv);
        holder.tv_title.setText(title);
        holder.tv_content.setText(content);
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != list) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("menuId", id);
                    Util.redirect(context, SecondActivity.class, params);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.loading), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyRecyclerViewAdapter.RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if("cook".equals(type)) {
            Cook cook = (Cook) list.get(position);
            setLayout(holder, cook.getName(), cook.getDescription(), cook.getImg(), cook.getId());
        } else if("favorite".equals(type)) {
            Favourite favourite = (Favourite) list.get(position);
            setLayout(holder, favourite.getNickName(), favourite.getCreateDate(), favourite.getImagePath(), favourite.getId());
        }
    }

    @Override
    public int getItemCount() {
        if(null != list) {
            return list.size();
        }
        return 0;
    }

    @SuppressWarnings("all")
    public void addAll(Collection<? extends Cook> collection) {
        list.addAll(collection);
        notifyDataSetChanged();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv_title;
        TextView tv_content;
        LinearLayout item_view;

        RecyclerViewHolder(View view) {
            super(view);
            iv = (ImageView) view.findViewById(R.id.item_iv);
            tv_title = (TextView) view.findViewById(R.id.item_title);
            tv_content = (TextView) view.findViewById(R.id.item_info);
            item_view = (LinearLayout) view.findViewById(R.id.item_view);
        }
    }
}
