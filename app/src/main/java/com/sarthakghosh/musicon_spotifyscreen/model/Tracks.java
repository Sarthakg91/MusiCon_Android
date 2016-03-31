package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by batman on 30/3/16.
 */
@Parcel
public class Tracks {
    @SerializedName("href")
    public String mHref;
    @SerializedName("items")
    public ArrayList<Item> mItems = new ArrayList<>();
    @SerializedName("limit")
    public int mLimit;
    @SerializedName("next")
    public String mNextHref;
    @SerializedName("offset")
    public int mOffset;
    @SerializedName("previous")
    public String mPreviousHref;
    @SerializedName("total")
    public int mTotal;
}
