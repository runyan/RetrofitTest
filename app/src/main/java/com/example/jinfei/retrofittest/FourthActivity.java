package com.example.jinfei.retrofittest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.adapter.MyRecyclerViewAdapter;
import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.myInterface.UIListener;
import com.example.jinfei.retrofittest.myenum.Type;
import com.example.jinfei.retrofittest.util.DBUtil;
import com.example.jinfei.retrofittest.util.Util;
import com.example.jinfei.retrofittest.widget.RecyclerViewDivider;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FourthActivity extends AppCompatActivity {

    @BindView(R.id.favorite_list)
    RecyclerView rv;
    @BindView(R.id.no_favorite)
    TextView tv;
    @BindView(R.id.favorite_list_size)
    TextView tv_favorite_list_size;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.search_favorite)
    SearchView searchFavorite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString((R.string.not_found))
    String notFound;
    @BindString(R.string.my_favourite)
    String myFavourite;
    @BindString(R.string.favorite_size)
    String favoriteSizeStr;
    @BindString(R.string.load_data_success)
    String loadSuccess;
    @BindString(R.string.load_data_fail)
    String loadFail;

    private List<Favourite> favouriteList;

    private MyRecyclerViewAdapter adapter;

    private Context mContext;

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        ButterKnife.bind(this);
        mContext = FourthActivity.this;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle(null);
            titleText.setText(myFavourite);
        }
        subscription = populateList(getSubscriber(true));
        rv.addItemDecoration(new RecyclerViewDivider(mContext, LinearLayout.HORIZONTAL, R.drawable.divider));

        searchFavorite.setIconifiedByDefault(false);
        searchFavorite.setFocusable(false);
        searchFavorite.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final List<Favourite> searchResult = DBUtil.searchFavorite(query);
                tv_favorite_list_size.setText(String.format(favoriteSizeStr, searchResult.size()));
                if (searchResult.isEmpty()) {
                    Toasty.info(mContext, notFound, Toast.LENGTH_SHORT, false).show();
                } else {
                    searchFavorite.clearFocus();
                    adapter = new MyRecyclerViewAdapter(mContext, searchResult, Type.favorite, new UIListener() {
                        @Override
                        public void onDataChange() {
                            rv.invalidate();
                            tv_favorite_list_size.setText(String.format(favoriteSizeStr, searchResult.size()));
                            if (searchResult.isEmpty()) {
                                onEmptyText();
                            }
                        }
                    });
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(mContext));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    searchFavorite.clearFocus();
                    onEmptyText();
                }
                return true;
            }
        });
    }

    private void onEmptyText() {
        Util.redirect(mContext, FourthActivity.class, null);
        overridePendingTransition(0, 0);
        finish();
    }

    private void loadDataFail() {
        Toasty.error(mContext, loadFail).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               finish();
            }
        }, 1000);
    }

    private void checkListForLayout(List<Favourite> list) {
        boolean isEmpty = list.isEmpty();
        int tvVisibility = isEmpty ? View.VISIBLE : View.INVISIBLE;
        int mainLayoutVisibility = isEmpty ? View.INVISIBLE : View.VISIBLE;
        tv.setVisibility(tvVisibility);
        mainLayout.setVisibility(mainLayoutVisibility);
    }

    private Subscription populateList(Subscriber<List<Favourite>> subscriber) {
        return Observable.just(DBUtil.getFavouriteList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private Subscriber<List<Favourite>> getSubscriber(final boolean needMessage) {
        return new Subscriber<List<Favourite>>() {
            @Override
            public void onCompleted() {
                checkListForLayout(favouriteList);
                if(needMessage && !favouriteList.isEmpty()) {
                    Toasty.success(mContext, loadSuccess).show();
                }
                tv_favorite_list_size.setText(String.format(favoriteSizeStr, favouriteList.size()));
                adapter = new MyRecyclerViewAdapter(mContext, favouriteList, Type.favorite, new UIListener() {
                    @Override
                    public void onDataChange() {
                        checkListForLayout(favouriteList);
                        tv_favorite_list_size.setText(String.format(favoriteSizeStr, favouriteList.size()));
                    }
                });
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(mContext));
            }

            @Override
            public void onError(Throwable e) {
                loadDataFail();
            }

            @Override
            public void onNext(List<Favourite> list) {
                favouriteList = list;
            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        subscription = populateList(getSubscriber(false));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
