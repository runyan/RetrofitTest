package com.example.jinfei.retrofittest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FourthActivity extends AppCompatActivity {

    private List<Favourite> favouriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        ListView lv = (ListView) findViewById(R.id.favorite_list);
        TextView tv = (TextView) findViewById(R.id.no_favorite);
        favouriteList = Util.getFavouriteList(FourthActivity.this);
        if(favouriteList.isEmpty()) {
            tv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.INVISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
            lv.setVisibility(View.VISIBLE);
        }
        lv.setAdapter(new MyAdapter(favouriteList));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> params = new HashMap<>();
                params.put("menuId", favouriteList.get(position).getId());
                Util.redirect(FourthActivity.this, SecondActivity.class, params);
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        List<Favourite> list;

        MyAdapter(List<Favourite> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(null == convertView) {
                convertView = LayoutInflater.from(FourthActivity.this).inflate(R.layout.item, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            Favourite favourite = list.get(position);
            Util.setImage(FourthActivity.this, favourite.getImagePath(), holder.iv);
            holder.tv_title.setText(favourite.getNickName());
            holder.tv_content.setText(favourite.getCreateDate());
            return convertView;
        }

        class ViewHolder {
            ImageView iv;
            TextView tv_title;
            TextView tv_content;

            ViewHolder(View view) {
                iv = (ImageView) view.findViewById(R.id.item_iv);
                tv_title = (TextView) view.findViewById(R.id.item_title);
                tv_content = (TextView) view.findViewById(R.id.item_info);
            }
        }
    }
}
