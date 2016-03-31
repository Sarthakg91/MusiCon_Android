package com.sarthakghosh.musicon_spotifyscreen;

/**
 * Created by Sarthak Ghosh on 30-03-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

public class SpotifyClass implements PlayerNotificationCallback, ConnectionStateCallback{

    private static final int REQUEST_CODE = 1337;
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "ca7918d8ada9480d8b239c5557056ff0";
    private Player mPlayer;
    private Activity mActivity;
    private MainActivity mainActivityObject;

    public SpotifyClass(Activity activity, MainActivity mainActivity)
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        mActivity=activity;
        AuthenticationClient.openLoginActivity(mActivity, REQUEST_CODE, request);
        mainActivityObject=mainActivity;
    }
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "musicon://callback";
    @Override
    public void onLoggedIn() {

            Log.d(SpotifyClass.class.getSimpleName(), "User logged in");

    }

    @Override
    public void onLoggedOut() {
        Log.d(SpotifyClass.class.getSimpleName(), "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d(SpotifyClass.class.getSimpleName(), "Login Failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(SpotifyClass.class.getSimpleName(), "Temporary Error Thrown");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(SpotifyClass.class.getSimpleName(), "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(SpotifyClass.class.getSimpleName(), "Playback event received: " + eventType.name());

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d(SpotifyClass.class.getSimpleName(), "Playback error received: " + errorType.name());
    }
    public void destroyPlayer()
    {
    Spotify.destroyPlayer(this);
    }
    public void play(String uri)
    {
        mPlayer.play(uri);
    }

    public void pause()
    {
        mPlayer.pause();
    }

    public void queueSong(String uri)
    {
        mPlayer.queue(uri);
    }

    public void clearQueue()
    {
        mPlayer.clearQueue();
    }

    public void skip()
    {
        mPlayer.skipToNext();
    }

    public void previous()
    {
        mPlayer.skipToPrevious();
    }
    public void skip(String uri)
    {
        mPlayer.play(uri);
    }
    public void back(String uri)
    {
        mPlayer.play(uri);
    }
    public void queue(String uri) {
        mPlayer.queue(uri);
    }
    public void skipToNext() {
        mPlayer.skipToNext();
    }
    public void resume()
    {
        mPlayer.resume();
    }
    public void checkAuthentication(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if(response.getError()!=null)
                Log.d("error is ", response.getError());

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(mActivity, response.getAccessToken(), CLIENT_ID);


                Spotify.getPlayer(playerConfig, mActivity, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(SpotifyClass.this);
                        mPlayer.addPlayerNotificationCallback(SpotifyClass.this);
                        //mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }
}
