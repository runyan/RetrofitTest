package com.example.jinfei.retrofittest;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.jinfei.retrofittest.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseActivity extends AppCompatActivity {

    @BindView(R.id.retry)
    Button retry;
    @BindView(R.id.network_error_layout)
    RelativeLayout networkErrorLayout;

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
}
