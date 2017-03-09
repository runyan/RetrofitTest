package com.example.jinfei.retrofittest.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TngouResponse<T> implements Serializable {

    @SerializedName("status")
    public boolean status;

    @SerializedName("total")
    public int total;

    @SerializedName("tngou")
    public T tngou;

}
