package com.example.jinfei.retrofittest.entity;

import com.google.gson.annotations.SerializedName;

public class Menu {

    @SerializedName("status")
    private boolean status;
    @SerializedName("url")
    private String url;
    @SerializedName("count")
    private int count;
    @SerializedName("description")
    private String description;
    @SerializedName("fcount")
    private int fcount;
    @SerializedName("food")
    private String food;
    @SerializedName("id")
    private int id;
    @SerializedName("images")
    private String images;
    @SerializedName("img")
    private String img;
    @SerializedName("keywords")
    private String keywords;
    @SerializedName("message")
    private String message;
    @SerializedName("name")
    private String name;
    @SerializedName("rcount")
    private int rcount;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFcount() {
        return fcount;
    }

    public void setFcount(int fcount) {
        this.fcount = fcount;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRcount() {
        return rcount;
    }

    public void setRcount(int rcount) {
        this.rcount = rcount;
    }
}
