package com.sarthakghosh.musicon_spotifyscreen.pam;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sarthakghosh.musicon_spotifyscreen.R;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConfirmActivity extends AppCompatActivity {
    URL url;
    HttpURLConnection connection = null;
    OutputStreamWriter wr;
    String data="";

    String user_id="bverma";
    String update_type="mood_feature";

    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_activity);
        Intent intent=getIntent();
        pos=intent.getIntExtra("position", 0);
        int id=intent.getIntExtra("id", 0);

        Drawable d=UtilClass.getMyDrawable();
        ImageView imgview= (ImageView) findViewById(R.id.pamConfirmImageView);
        imgview.setImageDrawable(d);

        Log.d(ConfirmActivity.class.getSimpleName(),"position is: "+String.valueOf(pos));
        Log.d(ConfirmActivity.class.getSimpleName(),"id is: "+String.valueOf(id));


        Button cancel= (Button) findViewById(R.id.btnCancel);
        Button submit=(Button) findViewById(R.id.btnSubmit);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick(v);
            }
        });
        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onSubmitClick(v);
            }
        });
        //where to get the data

        //open urlconnection here

        //

    }

    public void onCancelClick(View v) {
        finish();
    }

    public void onSubmitClick(View v) {

        new Thread(){
            public void run(){
                try {
                    url = new URL(getString(R.string.url_address));
                    connection=(HttpURLConnection)url.openConnection();
                    if(connection==null)
                        Log.d(ConfirmActivity.class.getSimpleName(),"connection object is null");
                    else
                        Log.d(ConfirmActivity.class.getSimpleName(), "connection object is not null");
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    wr = new OutputStreamWriter(connection.getOutputStream());
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "="
                            + URLEncoder.encode(user_id, "UTF-8");

                    data += "&" + URLEncoder.encode("feature_id", "UTF-8")
                            + "=" + URLEncoder.encode(String.valueOf(pos), "UTF-8");


                    data += "&" + URLEncoder.encode("feature", "UTF-8")
                            + "=" + URLEncoder.encode(update_type, "UTF-8");

                    wr.write(data);
                    wr.flush();
                    Log.d(ConfirmActivity.class.getSimpleName(), "data being written: " + data);



                } catch (IOException e) {
                    e.printStackTrace();
                }

                int statusCode = 0;
                try {
                    statusCode = connection.getResponseCode();
                    Log.d(ConfirmActivity.class.getSimpleName(),String.valueOf(statusCode));
                    if(connection!=null)
                        connection.disconnect();

                    //connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // throw some exception
                    Log.d(ConfirmActivity.class.getSimpleName(),"not http_ok");
                }
            }
        }.start();





        // handle issues





        Intent data = new Intent();
        data.putExtra("MSG_PAM_IS_CONFIRMED", true);






        if(getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }

       // connection.disconnect();
        finish();
    }
}