package com.example.jinfei.retrofittest;

import android.content.Context;
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
    RecyclerView rv;
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

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        ButterKnife.bind(this);
        mContext = FourthActivity.this;
        favouriteList = Util.getFavouriteList(mContext);
        checkList(favouriteList);
        adapter = new MyRecyclerViewAdapter(mContext, favouriteList, "favorite", new UIListener() {
            @Override
            public void onDataChange() {
                if(null != favouriteList) {
                    checkList(favouriteList);
                }
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL, R.drawable.divider));

        searchFavorite.setIconifiedByDefault(false);
        searchFavorite.setFocusable(false);
        searchFavorite.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final List<Favourite> searchResult = Util.searchFavorite(mContext, query);
                if(searchResult.isEmpty()) {
                    Toast.makeText(mContext, notFound, Toast.LENGTH_SHORT).show();
                } else {
                    searchFavorite.clearFocus();
                    adapter = new MyRecyclerViewAdapter(mContext, searchResult, "favorite", new UIListener() {
                        @Override
                        public void onDataChange() {
                            if(searchResult.isEmpty()) {
                                onRestart();
                            }
                        }
                    });
                    rv.setAdapter(adapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    searchFavorite.clearFocus();
                    adapter = new MyRecyclerViewAdapter(mContext, favouriteList, "favorite");
                    rv.setAdapter(adapter);
                }
                return true;
            }
        });
    }

    public void checkList(List<Favourite> list) {
        boolean isEmpty = list.isEmpty();
        int tvVisibility = isEmpty ? View.VISIBLE : View.INVISIBLE;
        int mainLayoutVisibility = isEmpty ? View.INVISIBLE : View.VISIBLE;
        tv.setVisibility(tvVisibility);
        mainLayout.setVisibility(mainLayoutVisibility);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        favouriteList = Util.getFavouriteList(mContext);
        adapter = new MyRecyclerViewAdapter(mContext, favouriteList, "favorite");
        rv.setAdapter(adapter);
        checkList(favouriteList);
    }

}
