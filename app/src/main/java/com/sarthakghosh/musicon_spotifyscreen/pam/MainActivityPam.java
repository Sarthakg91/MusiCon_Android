package com.sarthakghosh.musicon_spotifyscreen.pam;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.sarthakghosh.musicon_spotifyscreen.R;


public class MainActivityPam extends AppCompatActivity {
    private GridView mPictureGrid;
    private ImageAdapter mImageAdapter;
    public static final String[] IMAGE_FOLDERS = new String[]{
            "1_afraid",
            "2_tense",
            "3_excited",
            "4_delighted",
            "5_frustrated",
            "6_angry",
            "7_happy",
            "8_glad",
            "9_miserable",
            "10_sad",
            "11_calm",
            "12_satisfied",
            "13_gloomy",
            "14_tired",
            "15_sleepy",
            "16_serene"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pam);
        mPictureGrid = (GridView) findViewById(R.id.pamGridView);

        mImageAdapter= new ImageAdapter(this,mPictureGrid);
        mPictureGrid.setAdapter(mImageAdapter);

        mPictureGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent confirmLauncher=new Intent(MainActivityPam.this,ConfirmActivity.class);
                confirmLauncher.putExtra("position", position);
                ImageView nv=(ImageView)v;
                Drawable dr = nv.getDrawable();
                UtilClass.setMyDrawable(dr);

                confirmLauncher.putExtra("id", id);
                confirmLauncher.putExtra("pos", position);

                startActivityForResult(confirmLauncher, 0);
                Toast.makeText(MainActivityPam.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                finish();
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }

    }

    @Override
    protected void onDestroy() {
        Log.d(MainActivityPam.class.getSimpleName(),"Activity being destroyed");
        super.onDestroy();
    }
}
