package com.example.jinfei.retrofittest;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.TngouResponse;
import com.example.jinfei.retrofittest.exception.ServerException;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myenum.Type;
import com.example.jinfei.retrofittest.util.HttpMethods;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

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
    CoordinatorLayout normalLayout;
    @BindView(R.id.search_view)
    SearchView search;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.back)
    FloatingActionButton back;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.title_text)
    TextView titleText;

    @BindString(R.string.first_page)
    String firstPageStr;
    @BindString(R.string.next_page)
    String nextPageStr;
    @BindString(R.string.finish_refresh)
    String finishRefreshing;
    @BindString(R.string.click_me_to_go_to_next_page)
    String clickMe;
    @BindString(R.string.app_name)
    String appName;
    @BindString(R.string.no_updates)
    String noUpdates;

    private List<Cook> list;
    private List<Cook> tempList;
    private boolean hasUpdates = false;

    private MyRecyclerViewAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    private int pageNum = 1;
    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;

    private static final String TAG = "MainActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = MainActivity.this;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle(null);
            titleText.setText(appName);
        }

        mDialog = Util.getLoadingDialog(mContext);
        mLayoutManager = new LinearLayoutManager(mContext);
        rv.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL));
        networkCall(false);

        search.setFocusable(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Map<String, Object> params = new ArrayMap<>();
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
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                networkCall(true);
                if(hasUpdates) {
                    showNormalMessage(finishRefreshing);
                    hasUpdates = false;
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Snackbar snackbar = Snackbar.make(normalLayout, nextPageStr, Snackbar.LENGTH_LONG);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisibleItem + 2 >= mLayoutManager.getItemCount()) {
                        View v = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location); //获取在整个屏幕内的绝对坐标
                        int y = location[1];
                        if (lastVisibleItem != getLastVisiblePosition && lastVisiblePositionY != y) { //第一次拖至底部
                            snackbar.setAction(clickMe, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            move(true);
                                        }
                            });
                            if(snackbar.isShown()) {
                                snackbar.dismiss();
                            }
                            snackbar.show();
                            getLastVisiblePosition = lastVisibleItem;
                            lastVisiblePositionY = y;
                            return;
                        } else if (lastVisibleItem == getLastVisiblePosition
                                && lastVisiblePositionY == y) { //第二次拖至底部
                            move(true);
                            if(snackbar.isShown()) {
                                snackbar.dismiss();
                            }
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

    }

    @OnClick(R.id.back)
    void onBackClicked() {
        if (pageNum > 1) {
            move(false);
        } else {
            showNormalMessage(firstPageStr);
        }
    }

    public void retry() {
        networkCall(false);
    }

    private void networkCall(final boolean isRefreshing) {
        chooseLayout(false, normalLayout);
        Subscriber<TngouResponse<List<Cook>>> subscriber = new Subscriber<TngouResponse<List<Cook>>>() {
            @Override
            public void onCompleted() {
                mDialog.cancel();
                if(null != list && isRefreshing) {
                    if(list.get(0).equals(tempList.get(0))) {
                        showNormalMessage(noUpdates);
                    } else {
                        hasUpdates = true;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mDialog.cancel();
                handleError(TAG, e, mContext, new NetworkInterface() {
                    @Override
                    public void call() {
                        networkCall(false);
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
                if(!response.status) {
                    handleError(TAG, new ServerException(), mContext, new NetworkInterface() {
                        @Override
                        public void call() {
                            networkCall(false);
                        }
                    }, new NetworkError() {
                        @Override
                        public void onError() {
                            chooseLayout(true, normalLayout);
                        }
                    });
                    return;
                }
                if(isRefreshing) {
                    tempList = response.tngou;
                }
                list = response.tngou;
                adapter = new MyRecyclerViewAdapter(mContext, list, Type.cook);
                rv.setAdapter(adapter);
                rv.setLayoutManager(mLayoutManager);
            }
        };
        subscription = HttpMethods.getInstance(mContext).getList(subscriber, mDialog, 0, pageNum, 20);
    }

    private void move(boolean moveUp) {
        pageNum = moveUp ? (++pageNum) : (--pageNum);
        setResultList();
    }

    private void setResultList() {
        list.clear();
        networkCall(false);
        adapter.addAll(list);
    }

}
