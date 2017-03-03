package com.example.jinfei.retrofittest.entity;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tngou {

    @SerializedName("status")
    private boolean status;
    @SerializedName("toatal")
    private int total;
    @SerializedName("tngou")
    private List<Cook> list;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Cook> getList() {
        return list;
    }

    public void setList(List<Cook> list) {
        this.list = list;
    }
}
