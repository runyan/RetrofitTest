package com.example.jinfei.retrofittest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.List;

public class FourthActivity extends AppCompatActivity {

    private RecyclerView lv;
    private TextView tv;
    private RelativeLayout mainLayout;

    private List<Favourite> favouriteList;

    private MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        lv = (RecyclerView) findViewById(R.id.favorite_list);
        tv = (TextView) findViewById(R.id.no_favorite);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        final SearchView searchFavorite = (SearchView) findViewById(R.id.search_favorite);
        favouriteList = Util.getFavouriteList(FourthActivity.this);
        checkList();
        adapter = new MyRecyclerViewAdapter(FourthActivity.this, favouriteList, "favorite");
        lv.setAdapter(adapter);
        lv.setLayoutManager(new LinearLayoutManager(FourthActivity.this));
        lv.addItemDecoration(new RecyclerViewDivider(FourthActivity.this, LinearLayout.HORIZONTAL, R.drawable.divider));

        searchFavorite.setIconifiedByDefault(false);
        searchFavorite.setFocusable(false);
        searchFavorite.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<Favourite> searchResult = Util.searchFavorite(FourthActivity.this, query);
                if(searchResult.isEmpty()) {
                    Toast.makeText(FourthActivity.this, getResources().getString(R.string.not_found),Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new MyRecyclerViewAdapter(FourthActivity.this, searchResult, "favorite");
                    lv.setAdapter(adapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    searchFavorite.clearFocus();
                    adapter = new MyRecyclerViewAdapter(FourthActivity.this, favouriteList, "favorite");
                    lv.setAdapter(adapter);
                }
                return true;
            }
        });
    }

    private void checkList() {
        if(favouriteList.isEmpty()) {
            tv.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.INVISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        favouriteList = Util.getFavouriteList(FourthActivity.this);
        adapter = new MyRecyclerViewAdapter(FourthActivity.this, favouriteList, "favorite");
        lv.setAdapter(adapter);
        checkList();
    }

}
