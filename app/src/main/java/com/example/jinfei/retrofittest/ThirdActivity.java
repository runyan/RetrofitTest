package com.example.jinfei.retrofittest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class ThirdActivity extends BaseActivity  {

    @BindView(R.id.dishes)
    RecyclerView rv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.normal_layout)
    CoordinatorLayout normalLayout;
    @BindView(R.id.title_text)
    TextView titleText;

    @BindString(R.string.not_found)
    String notFound;
    @BindString(R.string.search_result)
    String searchResult;

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

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle(null);
            titleText.setText(searchResult);
        }

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
                if(!response.status) {
                    handleError(TAG, new ServerException(), mContext, new NetworkInterface() {
                        @Override
                        public void call() {
                            networkCall(name);
                        }
                    }, new NetworkError() {
                        @Override
                        public void onError() {
                            chooseLayout(true, normalLayout);
                        }
                    });
                    return;
                }
                list = response.tngou;
                if(null == list || response.total == 0) {
                    showNormalMessage(notFound);
                    overridePendingTransition(0, 0);
                    finish();
                }
                rv.setAdapter(new MyRecyclerViewAdapter(mContext, list, Type.cook));
                rv.setLayoutManager(new LinearLayoutManager(mContext));
            }
        };
        subscription = HttpMethods.getInstance(mContext).getDishes(subscriber, mDialog, name);
    }
}
