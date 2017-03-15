package com.example.jinfei.retrofittest.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RecyclerImageView extends AppCompatImageView {

    public RecyclerImageView(Context context) {
        super(context);
    }
    public RecyclerImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
    }
}
