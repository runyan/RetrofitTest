package com.example.jinfei.retrofittest.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinfei.retrofittest.R;
import com.example.jinfei.retrofittest.exception.ServerException;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

public class Util {

    private static final String IMG_URL = "http://tnfs.tngou.net/img";

    public static void setImage(Context context, String imgSrc, ImageView imageView) {
        Picasso.with(context).load(IMG_URL + imgSrc).placeholder(context.getDrawable(R.drawable.loading)).error(R.mipmap.ic_launcher).into(imageView);
    }

    public static void showErrorDialog(Context context, Throwable e, final NetworkInterface network, final NetworkError networkError) {
        String msg;
        if(e instanceof IOException) {
             msg = context.getResources().getString(R.string.network_error);
        } else if(e instanceof ServerException) {
            msg = context.getResources().getString(R.string.server_error);
        } else {
            msg = e.toString();
        }
        AlertDialog dialog = getBasicDialog(context, context.getResources().getString(R.string.error));
        dialog.setMessage(msg);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.reconnect), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                network.call();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                networkError.onError();
            }
        });
        dialog.show();
    }

    public static AlertDialog getBasicDialog(Context context, String title) {
        AlertDialog dialog = new android.app.AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public static void redirect(Context context, Class<? extends Activity> targetClass, Map<String, Object> params) {
        Intent intent = new Intent(context, targetClass);
        if(null != params && !params.isEmpty()) {
            for(Map.Entry<String, Object> entry : params.entrySet()) {
                Object param = entry.getValue();
                if(param instanceof Serializable) {
                    intent.putExtra(entry.getKey(), (Serializable) param);
                }
            }
        }
        context.startActivity(intent);
    }

    public static ProgressDialog getLoadingDialog(Context context) {
        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage(context.getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public static EditText getEditText(Context context, String text) {
        EditText et = new EditText(context);
        et.setText(text);
        et.setBackground(null);
        Field f;
        try {
            f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(et, R.drawable.editcursor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        et.setSelection(et.getText().toString().length());
        return et;
    }

}
