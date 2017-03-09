package com.example.jinfei.retrofittest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.TngouResponse;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.myenum.Type;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class ThirdActivity extends BaseActivity  {

    @BindView(R.id.dishes)
    RecyclerView rv;

    @BindString(R.string.not_found)
    String notFound;

    private String name;

    private static final String TAG = "ThirdActivity";

    private Context mContext;

    private List<Cook> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        ButterKnife.bind(this);

        mContext = ThirdActivity.this;
        mDialog = Util.getLoadingDialog(mContext);

        rv.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL, 6, Color.BLUE));

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        networkCall(name);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rv.invalidate();
    }

    public void retry() {
        networkCall(name);
    }

    private void networkCall(final String name) {
        chooseLayout(false, rv);
        Service service = Util.getService(mContext);
        subscription = service.getRxDishes(name)
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
                                networkCall(name);
                            }
                        }, new NetworkError() {
                            @Override
                            public void onError() {
                                chooseLayout(true, rv);
                            }
                        });
                    }

                    @Override
                    public void onNext(TngouResponse<List<Cook>> response) {
                        list = response.tngou;
                        if(null == list || list.isEmpty()) {
                            showNormalMessage(notFound);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                        rv.setAdapter(new MyRecyclerViewAdapter(mContext, list, Type.cook));
                        rv.setLayoutManager(new LinearLayoutManager(mContext));
                    }
                });
    }
}
