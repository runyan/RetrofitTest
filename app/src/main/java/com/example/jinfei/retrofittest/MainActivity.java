package com.example.jinfei.retrofittest;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.TngouResponse;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.myenum.Type;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.json_lv)
    RecyclerView rv;

    @BindView(R.id.normal_layout)
    RelativeLayout normalLayout;

    @BindView(R.id.search_view)
    SearchView search;

    @BindView(R.id.back)
    FloatingActionButton back;

    @BindString(R.string.first_page)
    String firstPageStr;
    @BindString(R.string.next_page)
    String nextPageStr;

    private List<Cook> list;

    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;

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

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (lastVisibleItem + 2 >= mLayoutManager.getItemCount()) {
                        View v = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                        int[] location = new int[2];
                        v.getLocationOnScreen(location); //获取在整个屏幕内的绝对坐标
                        int y = location[1];
                        if (lastVisibleItem != getLastVisiblePosition && lastVisiblePositionY != y) { //第一次拖至底部
                            showNormalMessage(nextPageStr);
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
        Service service = Util.getService(mContext);
        subscription = service.getRxList("cook", options)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mDialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())//显示Dialog在主线程中
                .observeOn(AndroidSchedulers.mainThread())//显示数据在主线程
                .subscribe(new Subscriber<TngouResponse<List<Cook>>>() {
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
                });
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
