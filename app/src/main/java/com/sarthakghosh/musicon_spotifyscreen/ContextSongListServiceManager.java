package com.sarthakghosh.musicon_spotifyscreen;

import com.sarthakghosh.musicon_spotifyscreen.model.ContextSongsList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by batman on 20/4/16.
 */
public class ContextSongListServiceManager {
    static String ENDPOINT = "http://52.37.58.111/";

    public static ContextSongsService getService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ENDPOINT).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ContextSongsService.class);
    }

    public interface ContextSongsService {
        @FormUrlEncoded
        @POST("v1/user/fetch_rec/bverma")
        Call<ContextSongsList> getSongsList(@Field("lat") double latitude,
                                            @Field("lon") double longitude,
                                            @Field("bpm") int beatsPerMinute);
    }
}
