package com.example.jinfei.retrofittest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThirdActivity extends BaseActivity implements Callback<Tngou> {

    private RecyclerView rv;
    private RelativeLayout networkErrorLayout;
    private Button retry;

    private String name;

    private static final String TAG = "ThirdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        rv = (RecyclerView) findViewById(R.id.dishes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new RecyclerViewDivider(ThirdActivity.this, LinearLayout.HORIZONTAL, 4, Color.BLUE));

        networkErrorLayout = (RelativeLayout) findViewById(R.id.network_error_layout);
        retry = (Button) findViewById(R.id.retry);

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
               Toast.makeText(ThirdActivity.this, getResources().getString(R.string.not_found), Toast.LENGTH_SHORT).show();
               finish();
           }
           rv.setAdapter(new MyRecyclerViewAdapter(ThirdActivity.this, list));
       }
    }

    @Override
    public void onFailure(Call<Tngou> call, Throwable t) {
       handleError(t.toString());
    }

    private void handleError(String reason) {
        Log.e(TAG, reason);
        Util.showErrorDialog(ThirdActivity.this, new NetworkInterface() {
            @Override
            public void call() {
                networkCall(name);
            }
        }, new NetworkError() {
            @Override
            public void onError() {
                rv.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkCall(name);
                    }
                });
            }
        });
    }

    private void networkCall(String name) {
        rv.setVisibility(View.VISIBLE);
        networkErrorLayout.setVisibility(View.GONE);
        Service service = Util.getService(this);
        Call<Tngou> call = service.getDishes(name);
        call.enqueue(this);
    }

}
