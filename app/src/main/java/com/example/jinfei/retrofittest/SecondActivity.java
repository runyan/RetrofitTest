package com.example.jinfei.retrofittest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.example.jinfei.retrofittest.util.DBUtil;
import com.example.jinfei.retrofittest.util.Util;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends BaseActivity implements Callback<Menu> {

    @BindView(R.id.menu_pic)
    ImageView pic;
    @BindView(R.id.menu_name)
    TextView name;
    @BindView(R.id.menu_food)
    TextView food;
    @BindView(R.id.menu_keywords)
    TextView keywords;
    @BindView(R.id.menu_description)
    TextView description;
    @BindView(R.id.menu_message)
    TextView message;
    @BindView(R.id.menu_count)
    TextView count;
    @BindView(R.id.menu_fcount)
    TextView fcount;
    @BindView(R.id.menu_rcount)
    TextView rcount;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.favorite)
    FloatingActionButton favourite;
    @BindView(R.id.un_favorite)
    FloatingActionButton unFavourite;

    @BindString(R.string.my_favourite)
    String myFavorite;
    @BindString(R.string.enter_nickname)
    String title;
    @BindString(R.string.confirm)
    String confirm;
    @BindString(R.string.nickname_not_empty)
    String nickNameNotEmpty;
    @BindString(R.string.favorite_success)
    String favoriteSuccess;
    @BindString(R.string.favorite_fail)
    String favoriteFail;
    @BindString(R.string.unfavorite_success)
    String unFavoriteSuccess;
    @BindString(R.string.unfavorite_fail)
    String unFavoriteFail;
    @BindString(R.string.food)
    String foodStr;
    @BindString(R.string.keywords)
    String keywordsStr;
    @BindString(R.string.description)
    String descriptionStr;
    @BindString(R.string.message)
    String messageStr;
    @BindString(R.string.count)
    String countStr;
    @BindString(R.string.fcount)
    String fcountStr;
    @BindString(R.string.rcount)
    String rcountStr;

    private static final String TAG = "SecondActivity";

    private int id;
    private String imagePath;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
        mContext = SecondActivity.this;

        Intent intent = getIntent();
        id = intent.getIntExtra("menuId", 0);
        networkCall();

        check();
    }

    private void check() {
        favoriteLayout(DBUtil.exist(id));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        check();
    }

    @SuppressWarnings("all")
    @Override
    public void onResponse(Call<Menu> call, Response<Menu> response) {
        if (!response.isSuccessful()) {
            handleError(response.toString());
        } else {
            Menu menu = response.body();
            imagePath = menu.getImg();
            Util.setImage(mContext, imagePath, pic);
            name.setText(menu.getName());
            food.setText(Html.fromHtml("<b>" + foodStr + "</b>" + menu.getFood()));
            keywords.setText(Html.fromHtml("<b>" + keywordsStr + "</b>" + menu.getKeywords()));
            description.setText(Html.fromHtml("<b>" + descriptionStr + "</b>" + menu.getDescription()));
            message.setText(Html.fromHtml("<b>" + messageStr + "</b>" + menu.getMessage()));
            count.setText(Html.fromHtml("<b>" + countStr + "</b>" + String.valueOf(menu.getCount())));
            fcount.setText(Html.fromHtml("<b>" + fcountStr + "</b>" + String.valueOf(menu.getFcount())));
            rcount.setText(Html.fromHtml("<b>" + rcountStr + "</b>" + String.valueOf(menu.getRcount())));
        }
    }

    @Override
    public void onFailure(Call<Menu> call, Throwable t) {
        handleError(t.toString());
    }

    private void networkCall() {
        chooseLayout(false, mainLayout);
        Service service = Util.getService(mContext);
        Call<Menu> call = service.getMenu(id);
        call.enqueue(this);
    }

    private void handleError(String reason) {
        Log.e(TAG, reason);
        Util.showErrorDialog(mContext, new NetworkInterface() {
            @Override
            public void call() {
                networkCall();
            }
        }, new NetworkError() {
            @Override
            public void onError() {
                chooseLayout(true, mainLayout);
            }
        });
    }

    public void retry() {
        networkCall();
    }

    @OnClick(R.id.favorite)
    void favorite() {
        final EditText et = new EditText(mContext);
        et.setText(myFavorite);
        et.setSelection(et.getText().toString().length());
        new AlertDialog.Builder(mContext).setTitle(title)
                .setView(et)
                .setCancelable(false)
                .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickName = et.getText().toString();
                        if (nickName.isEmpty()) {
                            Toast.makeText(mContext, nickNameNotEmpty, Toast.LENGTH_SHORT).show();
                        } else {
                            boolean saveResult = DBUtil.save(id, nickName, imagePath);
                            String msg = saveResult ? favoriteSuccess : favoriteFail;
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            favoriteLayout(saveResult);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.un_favorite)
    void unFavorite() {
        boolean deleteResult = DBUtil.delete(id);
        String msg = deleteResult ? unFavoriteSuccess : unFavoriteFail;
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        favoriteLayout(!deleteResult);
    }

    private void favoriteLayout(boolean favorite) {
        int favoriteVisibility = favorite ? View.INVISIBLE : View.VISIBLE;
        int unFavoriteVisibility = favorite ? View.VISIBLE : View.INVISIBLE;
        favourite.setVisibility(favoriteVisibility);
        unFavourite.setVisibility(unFavoriteVisibility);
    }

}
