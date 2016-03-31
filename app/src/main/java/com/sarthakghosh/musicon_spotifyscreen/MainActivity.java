package com.sarthakghosh.musicon_spotifyscreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.sarthakghosh.musicon_spotifyscreen.model.Item;
import com.sarthakghosh.musicon_spotifyscreen.model.SearchResponse;
import com.spotify.sdk.android.player.Player;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    //private static final int REQUEST_CODE = 1337;
    private static final int PLAYER_MODE = 0x001;
    private static final int SEARCH_MODE = 0x002;


    private String mediumTempoSong = "spotify:track:55qBw1900pZKfXJ6Q9A2Lc";
    private String goodTempoSong = "spotify:track:3w3y8KPTfNeOKPiqUTakBh";
    private String warmUpSong = "spotify:track:6nmVeODcBpsGKx5RPv003D";
    private String firstSongURI = "spotify:track:2TpxZ7JUBn3uw46aR7qd6V";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msBandobject = new Band(this, getBaseContext());
        spotifyPlayer = new SpotifyClass(this, MainActivity.this);

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

        playingUri = firstSongURI;
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
                int h = msBandobject.getHeartRate();
                spotifyPlayer.pause();
                play_pause.setImageResource(R.drawable.ic_play);

                if (h > 78) {
                    playingUri = goodTempoSong;

                } else if (h > 75) {

                    playingUri = mediumTempoSong;
                } else {
                    playingUri = warmUpSong;
                    //spotifyPlayer.queueSong(warmUpSong);
                }

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
            mHeartRate.setText(textReceived);
        }
    };

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