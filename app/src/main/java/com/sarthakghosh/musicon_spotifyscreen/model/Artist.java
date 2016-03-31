package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by batman on 30/3/16.
 */
@Parcel
public class Artist {
    @SerializedName("external_urls")
    public ExternalURLs mExternalURLs;
    @SerializedName("href")
    public String mHref;
    @SerializedName("id")
    public String mID;
    @SerializedName("name")
    public String mName;
    @SerializedName("type")
    public String mType;
    @SerializedName("uri")
    public String mUri;
}
