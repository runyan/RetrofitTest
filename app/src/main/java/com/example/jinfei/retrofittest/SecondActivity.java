package com.example.jinfei.retrofittest;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.exception.ServerException;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.util.DBUtil;
import com.example.jinfei.retrofittest.util.HttpMethods;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerImageView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import rx.Subscriber;

public class SecondActivity extends BaseActivity {

    @BindView(R.id.menu_pic)
    RecyclerImageView pic;
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
    CoordinatorLayout mainLayout;
    @BindView(R.id.favorite)
    FloatingActionButton favourite;
    @BindView(R.id.un_favorite)
    FloatingActionButton unFavourite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.my_favourite)
    String myFavorite;
    @BindString(R.string.enter_nickname)
    String title;
    @BindString(R.string.confirm)
    String confirm;
    @BindString(R.string.cancel)
    String cancel;
    @BindString(R.string.go_to_my_favorite)
    String goToMyFavorite;
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
        mDialog = Util.getLoadingDialog(mContext);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle(appName);
        }

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
    private void networkCall() {
        chooseLayout(false, mainLayout);
        Subscriber<Menu> subscriber = new Subscriber<Menu>() {
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
                        chooseLayout(true, mainLayout);
                    }
                });
            }

            @Override
            public void onNext(Menu menu) {
                if(!menu.isStatus()) {
                    handleError(TAG, new ServerException(), mContext, new NetworkInterface() {
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
                imagePath = menu.getImg();
                Util.setImage(mContext, 250f, 200f, imagePath, pic);
                name.setText(menu.getName());
                food.setText(Html.fromHtml("<b>" + foodStr + "</b>" + menu.getFood()));
                keywords.setText(Html.fromHtml("<b>" + keywordsStr + "</b>" + menu.getKeywords()));
                description.setText(Html.fromHtml("<b>" + descriptionStr + "</b>" + menu.getDescription()));
                message.setText(Html.fromHtml("<b>" + messageStr + "</b>" + menu.getMessage()));
                count.setText(Html.fromHtml("<b>" + countStr + "</b>" + String.valueOf(menu.getCount())));
                fcount.setText(Html.fromHtml("<b>" + fcountStr + "</b>" + String.valueOf(menu.getFcount())));
                rcount.setText(Html.fromHtml("<b>" + rcountStr + "</b>" + String.valueOf(menu.getRcount())));
            }
        };
       subscription = HttpMethods.getInstance(mContext).getMenu(subscriber, mDialog, id);
    }

    public void retry() {
        networkCall();
    }

    @OnClick(R.id.favorite)
    void favorite() {
        final EditText et = Util.getEditText(mContext, myFavorite);
        AlertDialog dialog = Util.getBasicDialog(mContext, title);
        dialog.setView(et);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nickName = et.getText().toString();
                if (nickName.isEmpty()) {
                    Toasty.info(mContext, nickNameNotEmpty, Toast.LENGTH_SHORT, false).show();
                } else {
                    boolean saveResult = DBUtil.save(id, nickName, imagePath);
                    showMessage(saveResult, favoriteSuccess, favoriteFail);
                    favoriteLayout(saveResult);
                }
                dialog.dismiss();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(null != dialog) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, goToMyFavorite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Util.redirect(mContext, FourthActivity.class, null);
            }
        });
        dialog.show();
    }

    @OnClick(R.id.un_favorite)
    void unFavorite() {
        boolean deleteResult = DBUtil.delete(id);
        showMessage(deleteResult, unFavoriteSuccess, unFavoriteFail);
        favoriteLayout(!deleteResult);
    }

    private void favoriteLayout(boolean favorite) {
        int favoriteVisibility = favorite ? View.INVISIBLE : View.VISIBLE;
        int unFavoriteVisibility = favorite ? View.VISIBLE : View.INVISIBLE;
        favourite.setVisibility(favoriteVisibility);
        unFavourite.setVisibility(unFavoriteVisibility);
    }

}
