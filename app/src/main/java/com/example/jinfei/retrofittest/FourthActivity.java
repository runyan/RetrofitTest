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
import com.example.jinfei.retrofittest.myInterface.UIListener;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FourthActivity extends AppCompatActivity {

    @BindView(R.id.favorite_list)
    RecyclerView lv;
    @BindView(R.id.no_favorite)
    TextView tv;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.search_favorite)
    SearchView searchFavorite;

    @BindString((R.string.not_found))
    String notFound;

    private List<Favourite> favouriteList;

    private MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        ButterKnife.bind(this);
        favouriteList = Util.getFavouriteList(FourthActivity.this);
        checkList();
        adapter = new MyRecyclerViewAdapter(FourthActivity.this, favouriteList, "favorite", new UIListener() {
            @Override
            public void onDataChange() {
                checkList();
            }
        });
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
                    Toast.makeText(FourthActivity.this, notFound,Toast.LENGTH_SHORT).show();
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

    public void checkList() {
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
