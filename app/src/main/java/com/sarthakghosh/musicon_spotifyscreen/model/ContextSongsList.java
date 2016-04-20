package com.sarthakghosh.musicon_spotifyscreen.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.sarthakghosh.musicon_spotifyscreen.SpotifySearchServiceManager;

import org.parceler.Parcel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by batman on 20/4/16.
 */
@Parcel
public class ContextSongsList {
    private static final int START_INDEX = 14;

    @SerializedName("uris")
    public ArrayList<String> mSongURIs = new ArrayList<>();

    public ArrayList<String> mIDs = new ArrayList<>();
    public TrackList mTrackList;

    private String mIDParameter = "";

    private int mCurrentSongIndex = 0;

    private void getIDs() {
        for (String uri : mSongURIs) {
            String id = uri.substring(START_INDEX);
            mIDs.add(id);
            mIDParameter = mIDParameter + id + ",";
        }
        mIDParameter = mIDParameter.substring(0, (mIDParameter.length() - 1));
        Log.d("MainActivity", "ID Parameters: " + mIDParameter);
    }

    public void populateMetaData() {
        getIDs();
        Call<TrackList> trackListCall = SpotifySearchServiceManager.getService().getTracks(mIDParameter);
        trackListCall.enqueue(new Callback<TrackList>() {
            @Override
            public void onResponse(Call<TrackList> call, Response<TrackList> response) {
                mTrackList = response.body();
                Log.d("MainActivity", "Received Tracks: " + System.identityHashCode(mTrackList));
            }

            @Override
            public void onFailure(Call<TrackList> call, Throwable t) {

            }
        });
    }

    public String getCurrentSongUri() {
        return mSongURIs.get(mCurrentSongIndex);
    }

    public String getNextSongUri() {
        mCurrentSongIndex = ((mCurrentSongIndex + 1) % mSongURIs.size());
        return mSongURIs.get(mCurrentSongIndex);
    }

    public String getPreviousSongUri() {
        mCurrentSongIndex = (mCurrentSongIndex - 1) < 0 ? mSongURIs.size() - 1 : (mCurrentSongIndex - 1);
        return mSongURIs.get(mCurrentSongIndex);
    }

    public int getmCurrentSongIndex() {
        return mCurrentSongIndex;
    }

    public static ContextSongsList getDefaultInstance() {
        ContextSongsList songsList = new ContextSongsList();
        songsList.mSongURIs.add("spotify:track:33ytgjBqchYHhhRfKWMCvH");
        songsList.mSongURIs.add("spotify:track:6J2rMdXyBOi9Pp3NWaMZFk");
        songsList.mSongURIs.add("spotify:track:4BdyNseqOFVFUsXJRkpn4X");
        songsList.mSongURIs.add("spotify:track:1kBM7AURqQTIXFePNVpEwP");
        songsList.mSongURIs.add("spotify:track:2zk1COXk7NaAaDW1KWMuio");
        songsList.mSongURIs.add("spotify:track:0ydgFPcYrxaHgVuLd17mjh");

        songsList.populateMetaData();

        return songsList;
    }
}
