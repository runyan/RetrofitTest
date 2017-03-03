package com.example.jinfei.retrofittest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Tngou;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Callback<Tngou> {

    private RecyclerView rv;
    private LinearLayoutManager mLayoutManager;

    private RelativeLayout normalLayout;
    private RelativeLayout networkErrorLayout;

    private Button retry;

    private List<Cook> list;

    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;

    private MyRecyclerViewAdapter adapter;
    private int pageNum = 1;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayoutManager = new LinearLayoutManager(this);
        rv = (RecyclerView) findViewById(R.id.json_lv);
        rv.setLayoutManager(mLayoutManager);
        rv.addItemDecoration(new RecyclerViewDivider(MainActivity.this, LinearLayoutManager.HORIZONTAL));
        retry = (Button) findViewById(R.id.retry);
        normalLayout = (RelativeLayout) findViewById(R.id.normal_layout);
        networkErrorLayout = (RelativeLayout) findViewById(R.id.network_error_layout);
        final SearchView search = (SearchView) findViewById(R.id.search_view);
        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.back);
        networkCall();

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(lastVisibleItem + 2 >= mLayoutManager.getItemCount()) {
                        View v = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location); //获取在整个屏幕内的绝对坐标
                        int y = location[1];
                        if(lastVisibleItem != getLastVisiblePosition && lastVisiblePositionY != y) { //第一次拖至底部
                            Toast.makeText(recyclerView.getContext(), getResources().getString(R.string.next_page), Toast.LENGTH_SHORT).show();
                                getLastVisiblePosition = lastVisibleItem;
                                lastVisiblePositionY = y;
                                return;
                        } else if (lastVisibleItem == getLastVisiblePosition
                                && lastVisiblePositionY == y) { //第二次拖至底部
                            move(true);
                        }
                    }
                    getLastVisiblePosition = 0;
                    lastVisiblePositionY = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(pageNum > 1) {
                   move(false);
               } else {
                   Toast.makeText(MainActivity.this, getResources().getString(R.string.first_page), Toast.LENGTH_SHORT).show();
               }
            }
        });

        search.setIconifiedByDefault(false);
        search.setFocusable(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", query);
                Util.redirect(MainActivity.this, ThirdActivity.class, params);
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onResponse(Call<Tngou> call, Response<Tngou> response) {
        list = response.body().getList();
        adapter = new MyRecyclerViewAdapter(MainActivity.this, list);
        rv.setAdapter(adapter);
    }

    @Override
    public void onFailure(Call<Tngou> call, Throwable t) {
        Log.e(TAG, t.toString());
        Util.showErrorDialog(MainActivity.this, new NetworkInterface() {
            @Override
            public void call() {
                networkCall();
            }
        }, new NetworkError() {
            @Override
            public void onError() {
                normalLayout.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkCall();
                    }
                });
            }
        });
    }

    private void networkCall() {
        normalLayout.setVisibility(View.VISIBLE);
        networkErrorLayout.setVisibility(View.GONE);
        Service service = Util.getService(MainActivity.this);
        Call<Tngou> call = service.getList("cook", 0, pageNum, 20);
        call.enqueue(this);
    }

    private void move(boolean moveUp) {
        pageNum = moveUp ? (++pageNum) : (--pageNum);
        setResultList();
    }

    private void setResultList() {
        list.clear();
        networkCall();
        adapter.addAll(list);
    }

}
