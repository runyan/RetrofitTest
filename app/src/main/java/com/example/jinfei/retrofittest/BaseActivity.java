package com.example.jinfei.retrofittest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import rx.Subscription;

public class BaseActivity extends AppCompatActivity {

    @BindView(R.id.retry)
    Button retry;
    @BindView(R.id.network_error_layout)
    RelativeLayout networkErrorLayout;

    ProgressDialog mDialog;

    protected Subscription subscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.retry)
    protected void retry() {

    }

    protected void chooseLayout(boolean networkError, View anotherView) {
        int errorVisibility = networkError ? View.VISIBLE : View.INVISIBLE;
        int viewVisibility = networkError ? View.INVISIBLE : View.VISIBLE;
        networkErrorLayout.setVisibility(errorVisibility);
        anotherView.setVisibility(viewVisibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.my_favorite: {
                Util.redirect(this, FourthActivity.class, null);
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {//isUnsubscribed 是否取消订阅
            subscription.unsubscribe();//取消网络请求
        }
    }

    protected void handleError(String tag, Throwable e, Context context, NetworkInterface networkInterface, NetworkError networkError) {
        Log.e(tag, e.toString());
        Util.showErrorDialog(context, e, networkInterface, networkError);
    }

    protected void showMessage(boolean successful, String successMsg, String failMsg) {
        if(successful) {
            Toasty.success(getApplicationContext(), successMsg, Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(getApplicationContext(), failMsg, Toast.LENGTH_SHORT, true).show();
        }
    }

    protected void showNormalMessage(String msg) {
        Toasty.info(getApplicationContext(), msg, Toast.LENGTH_LONG, false).show();
    }
}
