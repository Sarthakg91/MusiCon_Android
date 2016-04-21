package com.sarthakghosh.musicon_spotifyscreen.pam;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sarthakghosh.musicon_spotifyscreen.R;

/**
 * Created by Sarthak Ghosh on 06-03-2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context moContext;
    private GridView grid;

    private Integer[] mThumbIds = {
            R.drawable.pam_afraid1,R.drawable.pam_tense2,R.drawable.pam_excited1,R.drawable.pam_delighted3,
            R.drawable.pam_frustrated1,R.drawable.pam_angry1,R.drawable.pam_happy1,R.drawable.pam_glad2,
            R.drawable.pam_miserable2,R.drawable.pam_sad1,R.drawable.pam_calm1,R.drawable.pam_satisfied1,
            R.drawable.pam_gloomy1,R.drawable.pam_tired1,R.drawable.pam_sleepy1,R.drawable.pam_serene3
    };


    public ImageAdapter(Context c,GridView gridview) {
        moContext=c;
        grid=gridview;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // int imgDim = getImgDim();
        int imgDim= 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            imgDim = grid.getColumnWidth();
        }
        Log.d(MainActivityPam.class.getSimpleName(), "image dimensions are :" + String.valueOf(imgDim));
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes

                imageView = new ImageView(moContext);
            if(imgDim>0) {
                imageView.setLayoutParams(new GridView.LayoutParams(imgDim, imgDim));
                imageView.setMaxWidth(imgDim);
                imageView.setMaxHeight(imgDim);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setImageResource(mThumbIds[position]);

            }
        } else {
            imageView = (ImageView) convertView;
            imageView.setImageResource(mThumbIds[position]);
        }



       // imgDim = getImgDim();
        return imageView;

    }



}
