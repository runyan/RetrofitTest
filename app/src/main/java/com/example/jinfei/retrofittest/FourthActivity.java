package com.example.jinfei.retrofittest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FourthActivity extends AppCompatActivity {

    private RecyclerView lv;
    private TextView tv;

    private List<Favourite> favouriteList;

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        lv = (RecyclerView) findViewById(R.id.favorite_list);
        tv = (TextView) findViewById(R.id.no_favorite);
        favouriteList = Util.getFavouriteList(FourthActivity.this);
        checkList();
        adapter = new MyAdapter(favouriteList, FourthActivity.this);
        lv.setAdapter(adapter);
        lv.setLayoutManager(new LinearLayoutManager(FourthActivity.this));
        lv.addItemDecoration(new RecyclerViewDivider(FourthActivity.this, LinearLayout.HORIZONTAL, R.drawable.divider));
    }

    private void checkList() {
        if(favouriteList.isEmpty()) {
            tv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.INVISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
            lv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        favouriteList = Util.getFavouriteList(FourthActivity.this);
        adapter = new MyAdapter(favouriteList, FourthActivity.this);
        lv.setAdapter(adapter);
        lv.setLayoutManager(new LinearLayoutManager(FourthActivity.this));
        lv.addItemDecoration(new RecyclerViewDivider(FourthActivity.this, LinearLayout.HORIZONTAL, R.drawable.divider));
        checkList();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<Favourite> list;
        private Context context;

        MyAdapter(List<Favourite> list, Context context) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Favourite favourite = list.get(position);
            Util.setImage(context, favourite.getImagePath(), holder.iv);
            holder.tv_title.setText(favourite.getNickName());
            holder.tv_content.setText(favourite.getCreateDate());
            holder.item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("menuId", favourite.getId());
                    Util.redirect(context, SecondActivity.class, params);
                }
            });
        }

        @Override
        public int getItemCount() {
            if(null != list) {
                return list.size();
            }
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv_title;
            TextView tv_content;
            LinearLayout item_view;

            MyViewHolder(View view) {
                super(view);
                iv = (ImageView) view.findViewById(R.id.item_iv);
                tv_title = (TextView) view.findViewById(R.id.item_title);
                tv_content = (TextView) view.findViewById(R.id.item_info);
                item_view = (LinearLayout) view.findViewById(R.id.item_view);
            }
        }

    }

}
