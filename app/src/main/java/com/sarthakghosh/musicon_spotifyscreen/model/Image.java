package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by batman on 30/3/16.
 */
@Parcel
public class Image {
    @SerializedName("height")
    public int mHeight;
    @SerializedName("width")
    public int mWidth;
    @SerializedName("url")
    public String mUrl;
}
