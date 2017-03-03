package com.example.jinfei.retrofittest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity implements Callback<Menu> {

    private ImageView pic;
    private TextView name;
    private TextView food;
    private TextView keywords;
    private TextView description;
    private TextView message;
    private TextView count;
    private TextView fcount;
    private TextView rcount;
    private RelativeLayout networkErrorLayout;
    private ScrollView scrollView;
    private Button retry;

    private static final String TAG = "SecondActivity";

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        pic = (ImageView) findViewById(R.id.menu_pic);
        name = (TextView) findViewById(R.id.menu_name);
        keywords = (TextView) findViewById(R.id.menu_keywords);
        food = (TextView) findViewById(R.id.menu_food);
        description = (TextView) findViewById(R.id.menu_description);
        message = (TextView) findViewById(R.id.menu_message);
        count = (TextView) findViewById(R.id.menu_count);
        fcount = (TextView) findViewById(R.id.menu_fcount);
        rcount = (TextView) findViewById(R.id.menu_rcount);
        networkErrorLayout = (RelativeLayout) findViewById(R.id.network_error_layout);
        retry = (Button) findViewById(R.id.retry);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        Intent intent = getIntent();
        id = intent.getIntExtra("menuId", 0);
        networkCall();
    }

    @SuppressWarnings("all")
    @Override
    public void onResponse(Call<Menu> call, Response<Menu> response) {
       if(!response.isSuccessful()) {
          handleError(response.toString());
       } else {
           Menu menu = response.body();
           Util.setImage(SecondActivity.this, menu.getImg(), pic);
           name.setText(menu.getName());
           food.setText(Html.fromHtml("<b>" + getResources().getString(R.string.food) + "</b>" + menu.getFood()));
           keywords.setText(Html.fromHtml("<b>" + getResources().getString(R.string.keywords) + "</b>" + menu.getKeywords()));
           description.setText(Html.fromHtml("<b>" + getResources().getString(R.string.description) + "</b>" + menu.getDescription()));
           message.setText(Html.fromHtml("<b>" + getResources().getString(R.string.message) + "</b>" + menu.getMessage()));
           count.setText(Html.fromHtml("<b>" + getResources().getString(R.string.count) + "</b>" + String.valueOf(menu.getCount())));
           fcount.setText(Html.fromHtml("<b>" + getResources().getString(R.string.fcount) + "</b>" + String.valueOf(menu.getFcount())));
           rcount.setText(Html.fromHtml("<b>" + getResources().getString(R.string.rcount) + "</b>" + String.valueOf(menu.getRcount())));
       }
    }

    @Override
    public void onFailure(Call<Menu> call, Throwable t) {
       handleError(t.toString());
    }

    private void networkCall() {
        networkErrorLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        Service service = Util.getService(SecondActivity.this);
        Call<Menu> call = service.getMenu(id);
        call.enqueue(this);
    }

    private void handleError(String reason) {
        Log.e(TAG, reason);
        Util.showErrorDialog(SecondActivity.this, new NetworkInterface() {
            @Override
            public void call() {
                networkCall();
            }
        }, new NetworkError() {
            @Override
            public void onError() {
                networkErrorLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkCall();
                    }
                });
            }
        });

    }
}
