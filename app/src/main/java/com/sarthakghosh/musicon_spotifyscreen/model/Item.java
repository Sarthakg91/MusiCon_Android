package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by batman on 30/3/16.
 */

@Parcel
public class Item {
    @SerializedName("album")
    public Album mAlbum;
    @SerializedName("artists")
    public ArrayList<Artist> mArtists = new ArrayList<>();
    @SerializedName("available_markets")
    public ArrayList<String> mAvailableMarkets = new ArrayList<>();
    @SerializedName("disc_number")
    public int mDiscNumber;
    @SerializedName("duration_ms")
    public long mDurationInMS;
    @SerializedName("explicit")
    public boolean mExplicit;
    @SerializedName("external_ids")
    public ExternalIDs mExternalIDs;
    @SerializedName("external_urls")
    public ExternalURLs mExternalURLs;
    @SerializedName("href")
    public String mHref;
    @SerializedName("id")
    public String mID;
    @SerializedName("name")
    public String mName;
    @SerializedName("popularity")
    public int mPopularity;
    @SerializedName("preview_url")
    public String mPreviewURL;
    @SerializedName("track_number")
    public int mTrackNumber;
    @SerializedName("type")
    public String mType;
    @SerializedName("uri")
    public String mUri;
}
