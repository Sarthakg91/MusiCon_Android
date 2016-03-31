package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by batman on 30/3/16.
 */
@Parcel
public class Album {
    @SerializedName("album_type")
    public String mAlbumType;
    @SerializedName("available_markets")
    public ArrayList<String> mAvailableMarkets;
    @SerializedName("external_urls")
    public ExternalURLs mExternalURLs;
    @SerializedName("href")
    public String mHref;
    @SerializedName("id")
    public String mID;
    @SerializedName("images")
    public ArrayList<Image> mImages = new ArrayList<>();
    @SerializedName("name")
    public String mName;
    @SerializedName("type")
    public String mType;
    @SerializedName("uri")
    public String mUri;
}
