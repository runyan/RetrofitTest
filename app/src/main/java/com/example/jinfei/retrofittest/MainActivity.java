package com.example.jinfei.retrofittest;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.TngouResponse;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myenum.Type;
import com.example.jinfei.retrofittest.util.HttpMethods;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class MainActivity extends BaseActivity {

    @BindView(R.id.json_lv)
    RecyclerView rv;

    @BindView(R.id.normal_layout)
    RelativeLayout normalLayout;

    @BindView(R.id.search_view)
    SearchView search;

    @BindView(R.id.back)
    FloatingActionButton back;

    @BindView(R.id.swipe_refresh)
    SHSwipeRefreshLayout swipeRefreshLayout;

    @BindString(R.string.first_page)
    String firstPageStr;
    @BindString(R.string.finifsh_refresh)
    String finishRefreshing;
    @BindString(R.string.pull_refresh)
    String pullToRefresh;
    @BindString(R.string.release_to_refresh)
    String releaseToRefresh;
    @BindString(R.string.refreshing)
    String refreshing;

    private List<Cook> list;

    private MyRecyclerViewAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    private int pageNum = 1;

    private static final String TAG = "MainActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = MainActivity.this;
        mDialog = Util.getLoadingDialog(mContext);
        mLayoutManager = new LinearLayoutManager(mContext);

        rv.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        networkCall();

        search.setIconifiedByDefault(false);
        search.setFocusable(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", query);
                Util.redirect(mContext, ThirdActivity.class, params);
                overridePendingTransition(0, 0);
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                networkCall();
                showNormalMessage(finishRefreshing);
                swipeRefreshLayout.finishRefresh();
            }

            @Override
            public void onLoading() {
                move(true);
                swipeRefreshLayout.finishLoadmore();
            }

            @Override
            public void onRefreshPulStateChange(float percent, int state) {
                stateChange(percent, state);
            }

            @Override
            public void onLoadmorePullStateChange(float percent, int state) {

            }
        });

    }

    void stateChange(float percent, int state) {
        if(percent > 0.2f) {
            switch (state) {
                case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                    swipeRefreshLayout.setLoaderViewText(pullToRefresh);
                    break;
                case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                    swipeRefreshLayout.setLoaderViewText(releaseToRefresh);
                    break;
                case SHSwipeRefreshLayout.START:
                    swipeRefreshLayout.setLoaderViewText(refreshing);
                    break;
            }
        }
    }

    @OnClick(R.id.back)
    void onBackClick() {
        if (pageNum > 1) {
            move(false);
        } else {
            showNormalMessage(firstPageStr);
        }
    }

    public void retry() {
        networkCall();
    }

    private void networkCall() {
        Map<String, Integer> options = new HashMap<>();
        options.put("id", 0);
        options.put("page", pageNum);
        options.put("rows", 20);
        chooseLayout(false, normalLayout);
        Subscriber<TngouResponse<List<Cook>>> subscriber = new Subscriber<TngouResponse<List<Cook>>>() {
            @Override
            public void onCompleted() {
                mDialog.cancel();
            }

            @Override
            public void onError(Throwable e) {
                mDialog.cancel();
                handleError(TAG, e, mContext, new NetworkInterface() {
                    @Override
                    public void call() {
                        networkCall();
                    }
                }, new NetworkError() {
                    @Override
                    public void onError() {
                        chooseLayout(true, normalLayout);
                    }
                });
            }

            @Override
            public void onNext(TngouResponse<List<Cook>> response) {
                list = response.tngou;
                adapter = new MyRecyclerViewAdapter(mContext, list, Type.cook);
                rv.setAdapter(adapter);
                rv.setLayoutManager(mLayoutManager);
            }
        };
        HttpMethods.getInstance(mContext).getList(subscriber, mDialog, "cook", options);
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
