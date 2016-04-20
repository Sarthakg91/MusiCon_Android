package com.sarthakghosh.musicon_spotifyscreen;

import com.sarthakghosh.musicon_spotifyscreen.model.SearchResponse;
import com.sarthakghosh.musicon_spotifyscreen.model.TrackList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by batman on 30/3/16.
 */
public class SpotifySearchServiceManager {
    static String ENDPOINT = "https://api.spotify.com/";
    static String QUERY_TYPE = "track";

    public static SpotifySearchService getService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ENDPOINT).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(SpotifySearchService.class);
    }

    public interface SpotifySearchService {
        @GET("v1/search")
        Call<SearchResponse> query(@Query("q") String query, @Query("type") String type);

        @GET("v1/tracks")
        Call<TrackList> getTracks(@Query("ids") String ids);
    }
}
