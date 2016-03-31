package com.sarthakghosh.musicon_spotifyscreen.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by batman on 30/3/16.
 */
@Parcel
public class ExternalIDs {
    @SerializedName("isrc")
    public String mISRC;
}
