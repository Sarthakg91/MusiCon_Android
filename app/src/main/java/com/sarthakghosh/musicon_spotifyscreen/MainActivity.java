package com.sarthakghosh.musicon_spotifyscreen;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.microsoft.band.sensors.HeartRateConsentListener;
import com.spotify.sdk.android.player.Player;


public class MainActivity extends AppCompatActivity{

    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    //private static final int REQUEST_CODE = 1337;


    private ImageButton play_pause;
    private ImageButton skip;
    private ImageButton back;
    boolean isPlaying = false;
    boolean resuming = false;



    private Button btnConnectBand;
    private Button btnStartHRS;
    private TextView hrsTextView;
    private Band msBandobject;
    SpotifyClass spotifyPlayer;



    private String firstSongURI="spotify:track:2TpxZ7JUBn3uw46aR7qd6V";
    private String mediumTempoSong="spotify:track:55qBw1900pZKfXJ6Q9A2Lc";
    private String goodTempoSong="spotify:track:3w3y8KPTfNeOKPiqUTakBh";
    private String warmUpSong="spotify:track:6nmVeODcBpsGKx5RPv003D";

private String playingUri="";

    // TODO: Replace with your client ID
   // private static final String CLIENT_ID = "ca7918d8ada9480d8b239c5557056ff0";
   // TODO: Replace with your redirect URI
   // private static final String REDIRECT_URI = "musicon://callback";

    private Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        msBandobject= new Band(this, getBaseContext());


        spotifyPlayer= new SpotifyClass(this, MainActivity.this);




        btnConnectBand= (Button) findViewById(R.id.btnConnect);
        btnStartHRS= (Button) findViewById(R.id.btnStart);
        hrsTextView= (TextView) findViewById(R.id.hrsTextView);
        setupBandButtons();



        play_pause=(ImageButton)findViewById(R.id.play);
        back=(ImageButton)findViewById(R.id.back);
        skip=(ImageButton)findViewById(R.id.skip);
        setupSpotifyButtons();

        IntentFilter filter = new IntentFilter("com.sarthakghosh.musicon_spotifyscreen.Broadcast");
        registerReceiver(textReceiver, filter);

        playingUri=firstSongURI;





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect:
                msBandobject.checkConsent();
                return true;
            case R.id.action_start:
                msBandobject.startSensing();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        spotifyPlayer.checkAuthentication(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {

        spotifyPlayer.destroyPlayer();
        if (msBandobject.getClient() != null) {
            msBandobject.disconnect();
        }
        //Spotify.destroyPlayer(this);

        unregisterReceiver(textReceiver);
        super.onDestroy();
    }

    void setupSpotifyButtons()
    {
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("inside playClock", "click event handled");

                if (!isPlaying) {
                    play_pause.setImageResource(R.drawable.ic_pause);
                    if (resuming) {
                        spotifyPlayer.resume();
                    } else {
                        spotifyPlayer.play(playingUri);


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

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("MainActivity", "inside skip clicked");
                int h=msBandobject.getHeartRate();
                spotifyPlayer.pause();
                play_pause.setImageResource(R.drawable.ic_play);

                if(h>78)
                {
                    playingUri=goodTempoSong;

                }
                else if(h>75)
                {

                    playingUri=mediumTempoSong;
                }
                else
                {
                    playingUri=warmUpSong;
                    //spotifyPlayer.queueSong(warmUpSong);
                }

                spotifyPlayer.skip(playingUri);
                play_pause.setImageResource(R.drawable.ic_pause);

                //spotifyPlayer.play(playingUri);

//                if (!isPlaying) {
//                    play_pause.setImageResource(R.drawable.ic_pause);
//                    if (resuming) {
//                        spotifyPlayer.resume();
//                    } else {
//                        spotifyPlayer.play(playingUri);
//
//                    }
//
//                    isPlaying = true;
//                }



            }
        });

    }

    void setupBandButtons() {
        btnConnectBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            msBandobject.checkConsent();

            }
        });
        btnStartHRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d(MainActivity.class.getSimpleName(),"startSensing is being called");
                msBandobject.startSensing();
            }
        });

    }
@Override
    protected void onResume() {
        super.onResume();
        hrsTextView.setText("");
   // registerReceiver(broadcastReceiver, new IntentFilter(SmsReceiver.BROADCAST_ACTION));
}


    @Override
    protected void onPause() {
        super.onPause();

        if (msBandobject.getClient() != null) {


                msBandobject.unregisterListener();


        }
    }

protected BroadcastReceiver textReceiver= new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String textReceived=intent.getExtras().getString("Text");
        hrsTextView.setText(textReceived);
    }
};

}