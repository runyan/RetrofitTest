package com.example.jinfei.retrofittest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Tngou;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThirdActivity extends BaseActivity implements Callback<Tngou> {

    @BindView(R.id.dishes)
    RecyclerView rv;

    @BindString(R.string.not_found)
    String notFound;

    private String name;

    private static final String TAG = "ThirdActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        ButterKnife.bind(this);

        mContext = ThirdActivity.this;

        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL, 4, Color.BLUE));

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        networkCall(name);
    }

    @Override
    public void onResponse(Call<Tngou> call, Response<Tngou> response) {
       if(!response.isSuccessful()) {
          handleError(response.toString());
       } else {
           List<Cook> list = response.body().getList();
           if(null == list || list.isEmpty()) {
               Toast.makeText(mContext, notFound, Toast.LENGTH_SHORT).show();
               finish();
           }
           rv.setAdapter(new MyRecyclerViewAdapter(mContext, list, "cook"));
       }
    }

    @Override
    public void onFailure(Call<Tngou> call, Throwable t) {
       handleError(t.toString());
    }

    private void handleError(String reason) {
        Log.e(TAG, reason);
        Util.showErrorDialog(mContext, new NetworkInterface() {
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

    public void retry() {
        networkCall(name);
    }

    private void networkCall(String name) {
        chooseLayout(false, rv);
        Service service = Util.getService(mContext);
        Call<Tngou> call = service.getDishes(name);
        call.enqueue(this);
    }

}
