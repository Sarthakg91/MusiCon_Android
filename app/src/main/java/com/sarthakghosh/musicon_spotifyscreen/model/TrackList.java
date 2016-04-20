package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by batman on 20/4/16.
 */
@Parcel
public class TrackList {
    @SerializedName("tracks")
    public ArrayList<Item> mTracks = new ArrayList<>();
}
