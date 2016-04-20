package com.sarthakghosh.musicon_spotifyscreen;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.sarthakghosh.musicon_spotifyscreen.model.ContextSongsList;
import com.sarthakghosh.musicon_spotifyscreen.model.Item;
import com.sarthakghosh.musicon_spotifyscreen.model.SearchResponse;
import com.spotify.sdk.android.player.Player;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    //private static final int REQUEST_CODE = 1337;
    private static final int PLAYER_MODE = 0x001;
    private static final int SEARCH_MODE = 0x002;

    private static final int RESTING = 0x110;
    private static final int NORMAL = 0x111;
    private static final int WARMUP = 0x112;
    private static final int JOGGING = 0x113;
    private static final int SPRINT = 0x114;

    private ContextSongsList mSongsList;
    private String playingUri = "";

    private LinearLayout mPlayerPane;
    private FrameLayout mSearchPane;

    private ImageButton play_pause;
    private ImageButton skip;
    private ImageButton back;
    private SearchView mSearchView;
    private ImageView mAlbumArt;
    private TextView mHeartRate;


    private ListView mSearchResultList;
    private SearchAdapter mAdapter;

    private Band msBandobject;
    private SpotifyClass spotifyPlayer;

    boolean isPlaying = false;
    boolean resuming = false;
    private int mMode = PLAYER_MODE;


    // TODO: Replace with your client ID
    // private static final String CLIENT_ID = "ca7918d8ada9480d8b239c5557056ff0";
    // TODO: Replace with your redirect URI
    // private static final String REDIRECT_URI = "musicon://callback";

    private Player mPlayer;


    private int mHeartRateMode = -1;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsGoogleApiConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msBandobject = new Band(this, getBaseContext());
        spotifyPlayer = new SpotifyClass(this, MainActivity.this);

        mSongsList = ContextSongsList.getDefaultInstance();

        mPlayerPane = (LinearLayout) findViewById(R.id.player_view);
        mSearchPane = (FrameLayout) findViewById(R.id.search_result_pane);

        mSearchResultList = (ListView) findViewById(R.id.search_list);
        mAdapter = new SearchAdapter();
        setUpSearchListAndAdapter();

        mAlbumArt = (ImageView) findViewById(R.id.album_art);
        play_pause = (ImageButton) findViewById(R.id.play);
        back = (ImageButton) findViewById(R.id.back);
        skip = (ImageButton) findViewById(R.id.skip);
        mHeartRate = (TextView) findViewById(R.id.heart_rate);
        setupSpotifyButtons();


        IntentFilter filter = new IntentFilter("com.sarthakghosh.musicon_spotifyscreen.Broadcast");
        registerReceiver(textReceiver, filter);

        playingUri = mSongsList.getCurrentSongUri();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMode(PLAYER_MODE);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setInputType(InputType.TYPE_CLASS_TEXT);
        mSearchView.setQueryHint("Search your song here");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mSearchView.getQuery().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "No Search Query!", Toast.LENGTH_SHORT).show();
                    (menu.findItem(R.id.search)).collapseActionView();
                    return false;
                } else {
                    String queryText = mSearchView.getQuery().toString();
                    startSearchWithQuery(queryText);
                    (menu.findItem(R.id.search)).collapseActionView();
                    setUpMode(SEARCH_MODE);
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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


    private void setUpSearchListAndAdapter() {
        mSearchResultList.setAdapter(mAdapter);
        mSearchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item track = mAdapter.getItem(position);
                spotifyPlayer.play(track.mUri);
                play_pause.setImageResource(R.drawable.ic_pause);
                isPlaying = true;
                Picasso.with(MainActivity.this).load(track.mAlbum.mImages.get(0).mUrl).into(mAlbumArt);
                setUpMode(PLAYER_MODE);
            }
        });
    }

    private void setUpMode(int mode) {
        mMode = mode;
        switch (mMode) {
            case SEARCH_MODE:
                mPlayerPane.setVisibility(View.GONE);
                mSearchPane.setVisibility(View.VISIBLE);
                break;
            case PLAYER_MODE:
                mPlayerPane.setVisibility(View.VISIBLE);
                mSearchPane.setVisibility(View.GONE);
                break;
            default:
                Log.e("MainActivity", "WTF");
        }
        KeyBoardManager.hideKeyBoard(MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (mMode == SEARCH_MODE) {
            setUpMode(PLAYER_MODE);
            return;
        }

        super.onBackPressed();
    }

    private int getHeartRateMode(int heartRate) {
        if (heartRate >= 55 && heartRate < 65) {
            return RESTING;
        } else if (heartRate >= 65 && heartRate < 75) {
            return NORMAL;
        } else if (heartRate >= 75 && heartRate < 85) {
            return WARMUP;
        } else if (heartRate >= 85 && heartRate < 95) {
            return JOGGING;
        } else if (heartRate >= 95) {
            return SPRINT;
        } else {
            return -1;
        }
    }

    private void startSearchWithQuery(String queryText) {
        Call<SearchResponse> searchResponseCall = SpotifySearchServiceManager.getService().
                query(queryText, SpotifySearchServiceManager.QUERY_TYPE);

        searchResponseCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                Log.d("MainActivity", searchResponse.toString());
                mAdapter.setData(searchResponse.mTracks.mItems);
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.e("MainActivity", t.toString());
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
        if (msBandobject.getClient() != null) {
            msBandobject.disconnect();
        }
        //Spotify.destroyPlayer(this);

        unregisterReceiver(textReceiver);
        super.onDestroy();
    }

    void setupSpotifyButtons() {
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
                        Picasso.with(MainActivity.this).load(mSongsList.mTrackList.mTracks.
                                get(mSongsList.getmCurrentSongIndex()).mAlbum.mImages.get(0).mUrl).into(mAlbumArt);
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
                spotifyPlayer.pause();
                play_pause.setImageResource(R.drawable.ic_play);

                playingUri = mSongsList.getNextSongUri(); //calling next song changes current song to next song
                Picasso.with(MainActivity.this).load(mSongsList.mTrackList.mTracks.
                        get(mSongsList.getmCurrentSongIndex()).mAlbum.mImages.get(0).mUrl).into(mAlbumArt);

                spotifyPlayer.skip(playingUri);
                play_pause.setImageResource(R.drawable.ic_pause);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (msBandobject.getClient() != null) {
            msBandobject.unregisterListener();
        }
    }

    protected BroadcastReceiver textReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String textReceived = intent.getExtras().getString("Text");
            Log.d("MainActivity", "Text Received: " + textReceived);
            mHeartRate.setText(textReceived);
            if (textReceived != null && textReceived.length() <= 3) {
                getContextSpecificSongsList(Integer.valueOf(textReceived));
            }
        }
    };

    private void getContextSpecificSongsList(int heartRate) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permission does not exist");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (getHeartRateMode(heartRate) != mHeartRateMode && currentLocation != null) {
            mHeartRateMode = getHeartRateMode(heartRate);
            Log.d("MainActivity", "Inside if. About to make request");
            Call<ContextSongsList> contextSongsListCall = ContextSongListServiceManager.getService().
                    getSongsList(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), heartRate);
            contextSongsListCall.enqueue(new Callback<ContextSongsList>() {
                @Override
                public void onResponse(Call<ContextSongsList> call, Response<ContextSongsList> response) {
                    ContextSongsList songsList = response.body();
                    Log.d("MainActivity", songsList.mSongURIs.toString());
                    songsList.populateMetaData();
                    mSongsList = songsList;
                }

                @Override
                public void onFailure(Call<ContextSongsList> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mIsGoogleApiConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mIsGoogleApiConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mIsGoogleApiConnected = false;
    }

    public static class KeyBoardManager {
        public static void hideKeyBoard(Activity currentActivity) {
            View view = currentActivity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) currentActivity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}