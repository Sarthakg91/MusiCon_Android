package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by batman on 30/3/16.
 */
@Parcel
public class SearchResponse {
    @SerializedName("tracks")
    public Tracks mTracks;
}
