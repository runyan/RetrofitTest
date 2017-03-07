package com.example.jinfei.retrofittest.util;

import com.example.jinfei.retrofittest.entity.Favourite;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBUtil {

    public static boolean save(int dishId, String nickName, String imgPath) {
        Favourite favourite = new Favourite();
        favourite.setDishId(dishId);
        favourite.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
        favourite.setNickName(nickName);
        favourite.setImagePath(imgPath);
        return favourite.save();
    }

    public static boolean delete(int dishId) {
        return DataSupport.deleteAll(Favourite.class, "dishId = ?", String.valueOf(dishId)) > 0;
    }

    public static boolean update(int dishId, String newName) {
        Favourite favourite = new Favourite();
        favourite.setNickName(newName);
        return favourite.updateAll("dishId = ?", String.valueOf(dishId)) > 0;
    }

    public static boolean exist(int dishId) {
        return !DataSupport.where("dishId = ?", String.valueOf(dishId)).find(Favourite.class).isEmpty();
    }

    public static List<Favourite> getFavouriteList() {
        return DataSupport.where().order("createDate desc").find(Favourite.class);
    }

    public static List<Favourite> searchFavorite(String name) {
        return DataSupport.where("nickName like ?", "%" + name + "%").order("createDate desc").find(Favourite.class);
    }

}
