package com.example.jinfei.retrofittest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends BaseActivity implements Callback<Menu> {

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
    private RelativeLayout mainLayout;
    private Button retry;
    private FloatingActionButton favourite;
    private FloatingActionButton unFavourite;

    private static final String TAG = "SecondActivity";

    private int id;
    private String imagePath;

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
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        favourite = (FloatingActionButton) findViewById(R.id.favorite);
        unFavourite = (FloatingActionButton) findViewById(R.id.un_favorite);

        Intent intent = getIntent();
        id = intent.getIntExtra("menuId", 0);
        networkCall();

        check();

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(SecondActivity.this);
                et.setText(getResources().getString(R.string.my_favourite));
                et.setSelection(et.getText().toString().length());
                new AlertDialog.Builder(SecondActivity.this).setTitle(getResources().getString(R.string.enter_nickname))
                        .setView(et)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickName = et.getText().toString();
                                if(nickName.isEmpty()) {
                                    Toast.makeText(SecondActivity.this, getResources().getString(R.string.nickname_not_empty), Toast.LENGTH_SHORT).show();
                                } else {
                                    boolean r = Util.save(SecondActivity.this, id, nickName, imagePath);
                                    String msg = r ? getResources().getString(R.string.favorite_success) : getResources().getString(R.string.favorite_fail);
                                    Toast.makeText(SecondActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    favoriteLayout();
                                }
                            }
                        })
                        .show();
            }
        });

        unFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean r = Util.delete(SecondActivity.this, id);
                String msg = r ? getResources().getString(R.string.unfavorite_success) : getResources().getString(R.string.unfavorite_fail);
                Toast.makeText(SecondActivity.this, msg, Toast.LENGTH_SHORT).show();
                unFavoriteLayout();
            }
        });
    }

    private void check() {
        if(Util.exist(SecondActivity.this, id)) {
            favoriteLayout();
        } else {
            unFavoriteLayout();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        check();
    }

    @SuppressWarnings("all")
    @Override
    public void onResponse(Call<Menu> call, Response<Menu> response) {
       if(!response.isSuccessful()) {
          handleError(response.toString());
       } else {
           Menu menu = response.body();
           imagePath = menu.getImg();
           Util.setImage(SecondActivity.this, imagePath, pic);
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
        mainLayout.setVisibility(View.VISIBLE);
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
                mainLayout.setVisibility(View.GONE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkCall();
                    }
                });
            }
        });
    }

    private void favoriteLayout() {
        favourite.setVisibility(View.INVISIBLE);
        unFavourite.setVisibility(View.VISIBLE);
    }

    private void unFavoriteLayout() {
        favourite.setVisibility(View.VISIBLE);
        unFavourite.setVisibility(View.INVISIBLE);
    }

}
