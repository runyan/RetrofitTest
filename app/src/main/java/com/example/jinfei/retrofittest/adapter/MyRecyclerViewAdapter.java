package com.example.jinfei.retrofittest.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jinfei.retrofittest.R;
import com.example.jinfei.retrofittest.SecondActivity;
import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.myInterface.UIListener;
import com.example.jinfei.retrofittest.myenum.Type;
import com.example.jinfei.retrofittest.util.DBUtil;
import com.example.jinfei.retrofittest.util.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.RecyclerViewHolder> {

    private List list;
    private Context context;
    private Type type;
    private UIListener listener;

    public MyRecyclerViewAdapter(Context context, List list, Type type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    public MyRecyclerViewAdapter(Context context, List list, Type type, UIListener listener) {
        this.context = context;
        this.list = list;
        this.type = type;
        this.listener = listener;
    }

    private void setLayout(RecyclerViewHolder holder, String title, String content, String imagePath, final int id) {
        Util.setImage(context, imagePath, holder.iv);
        holder.tv_title.setText(title);
        holder.tv_content.setText(content);
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != list) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("menuId", id);
                    Util.redirect(context, SecondActivity.class, params);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.loading), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyRecyclerViewAdapter.RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        switch (type) {
            case cook: {
                Cook cook = (Cook) list.get(position);
                setLayout(holder, cook.getName(), cook.getDescription(), cook.getImg(), cook.getId());
                break;
            }
            case favorite: {
                Favourite favourite = (Favourite) list.get(position);
                final int tempPosition = position;
                final int dishId = favourite.getDishId();
                final String nickName = favourite.getNickName();
                setLayout(holder, nickName, favourite.getCreateDate(), favourite.getImagePath(), dishId);
                holder.item_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final EditText et = new EditText(context);
                        et.setText(nickName);
                        et.setSelection(et.getText().toString().length());
                        new AlertDialog.Builder(context).setTitle(context.getString(R.string.enter_nickname))
                                .setView(et)
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newName = et.getText().toString();
                                        if (newName.isEmpty()) {
                                            Toast.makeText(context, context.getString(R.string.nickname_not_empty), Toast.LENGTH_SHORT).show();
                                        } else {
                                            boolean updateResult = DBUtil.update(dishId, newName);
                                            String msg = updateResult ? context.getString(R.string.update_success) : context.getString(R.string.update_fail);
                                            if (updateResult) {
                                                ((Favourite) list.get(tempPosition)).setNickName(newName);
                                                notifyDataSetChanged();
                                            }
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                        }
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNeutralButton(context.getString(R.string.un_favorite), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        boolean deleteResult = DBUtil.delete(dishId);
                                        String msg = deleteResult ? context.getString(R.string.unfavorite_success) : context.getString(R.string.unfavorite_fail);
                                        if (deleteResult) {
                                            list.remove(tempPosition);
                                            notifyItemRemoved(tempPosition);
                                            notifyItemRangeChanged(tempPosition, getItemCount());
                                            if (null != listener) {
                                                listener.onDataChange();
                                            }
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                        return false;
                    }
                });
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressWarnings("unchecked")
    public void addAll(Collection<? extends Cook> collection) {
        list.addAll(collection);
        notifyDataSetChanged();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_iv)
        ImageView iv;
        @BindView(R.id.item_title)
        TextView tv_title;
        @BindView(R.id.item_info)
        TextView tv_content;
        @BindView(R.id.item_view)
        LinearLayout item_view;

        RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
