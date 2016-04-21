package com.sarthakghosh.musicon_spotifyscreen.pam;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.sarthakghosh.musicon_spotifyscreen.R;

/**
 * Created by Sarthak Ghosh on 05-04-2016.
 */
public class NotificationService extends Service {
    private int mId=0x01;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Toast.makeText(this,"Service is being started", Toast.LENGTH_LONG).show();
        if(intent != null) {
            String mes = intent.getStringExtra("message");
            Toast.makeText(this, mes, Toast.LENGTH_LONG).show();
        }
        runNotificatonScheduler();
        return super.onStartCommand(intent, flags, startId);
    }

    private void runNotificatonScheduler() {

    }

    @Override

    public void onCreate()
    {
        super.onCreate();
        Toast.makeText(this, "Service is being created", Toast.LENGTH_SHORT).show();
        createNotification();
    }

    private void createNotification() {


        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pnotification)
                        .setContentTitle("PAM")
                        .setContentText("How are you feeling now?")
                .setColor(Color.argb(255,230,81,0));
     // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivityPam.class);

     // The stack builder object will contain an artificial back stack for the
     // started Activity.
     // This ensures that navigating backward from the Activity leads out of
     // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
     // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivityPam.class);
     // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     // mId allows you to update the notification later on.
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(this,"Service is being stopped", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

}
