package com.sarthakghosh.musicon_spotifyscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sarthakghosh.musicon_spotifyscreen.model.Item;

import java.util.ArrayList;

/**
 * Created by batman on 31/3/16.
 */
public class SearchAdapter extends BaseAdapter {
    public ArrayList<Item> mTracks = new ArrayList<>();

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @Override
    public Item getItem(int position) {
        return mTracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.search_row, parent, false);
        } else {
            view = convertView;
        }
        TextView trackName, albumName, artistName;
        trackName = (TextView) view.findViewById(R.id.track_name);
        albumName = (TextView) view.findViewById(R.id.album_name);
        artistName = (TextView) view.findViewById(R.id.artist_name);
        Item trackItem = getItem(position);
        trackName.setText(trackItem.mName);
        albumName.setText(trackItem.mAlbum.mName);
        artistName.setText(trackItem.mArtists.get(0).mName);

        return view;
    }

    public void setData(ArrayList<Item> newTracks) {
        mTracks.clear();
        mTracks.addAll(newTracks);
        notifyDataSetChanged();
    }
}
