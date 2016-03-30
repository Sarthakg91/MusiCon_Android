package com.sarthakghosh.musicon_spotifyscreen;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

public class MainActivity extends Activity{

    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    //private static final int REQUEST_CODE = 1337;
    private ImageButton play_pause;
    private ImageButton skip;
    private ImageButton back;
    boolean isPlaying = false;
    boolean resuming = false;
    SpotifyClass spotifyPlayer;
    private String firstSongURI="spotify:track:2TpxZ7JUBn3uw46aR7qd6V";

    // TODO: Replace with your client ID
   // private static final String CLIENT_ID = "ca7918d8ada9480d8b239c5557056ff0";
    // TODO: Replace with your redirect URI
   // private static final String REDIRECT_URI = "musicon://callback";

    private Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spotifyPlayer= new SpotifyClass(this, MainActivity.this);


        play_pause=(ImageButton)findViewById(R.id.play);
        back=(ImageButton)findViewById(R.id.back);
        skip=(ImageButton)findViewById(R.id.skip);



        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("inside playClock", "click event handled");

                if (!isPlaying) {
                    play_pause.setImageResource(R.drawable.ic_pause);
                    if (resuming) {
                        spotifyPlayer.resume();
                    } else {
                        spotifyPlayer.play(firstSongURI);
                    }

                    isPlaying = true;
                } else {
                    resuming = true;
                    play_pause.setImageResource(R.drawable.ic_play);
                    spotifyPlayer.pause();
                    isPlaying = false;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        spotifyPlayer.checkAuthentication(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {

        spotifyPlayer.destroyPlayer();
        //Spotify.destroyPlayer(this);
        super.onDestroy();
    }


}