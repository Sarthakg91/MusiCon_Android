package com.sarthakghosh.musicon_spotifyscreen.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

/**
 * Created by Sarthak Ghosh on 30-03-2016.
 */
public class Band{


    private BandClient client = null;
    private int heartRate;
    private String quality;
    private Activity mainUiActivity;
    private Context mContext;


    public Band(Activity uiActivity, Context context)
    {
        mainUiActivity=uiActivity;
        mContext=context;

    }

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
            if(bandHeartRateEvent!=null)
            {
                heartRate= bandHeartRateEvent.getHeartRate();
                quality= String.valueOf(bandHeartRateEvent.getQuality());
                appendToUI(String.valueOf(heartRate),"heartrate");
                appendToUI("Connected","connecting");
            }
        }
    };

    public void checkConsent() {

        new HeartRateConsentTask().execute(mainUiActivity);
    }

    public void startSensing() {

        new HeartRateSubscriptionTask().execute();
    }

    public BandClient getClient() {
        return client;
    }

    public void unregisterListener() {
    }

    public void disconnect() {
    }

    public int getHeartRate() {
        return heartRate;
    }

    private class HeartRateConsentTask extends AsyncTask<Activity, Void, Void>{
        @Override
        protected Void doInBackground(final Activity... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.d("HeartRateConsentTask", "In doInBackground");
                    if (params[0] != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0], new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    appendToUI("Band is not paired..\n","connecting");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage,"connecting");

            } catch (Exception e) {
                appendToUI(e.getMessage(),"connecting");
            }
            return null;
        }
    }

    private class HeartRateSubscriptionTask extends
    AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    if (client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                    } else {
                        appendToUI(" Please press the Heart Rate Consent button.\n","connecting");
                    }
                } else {
                    appendToUI("Pair With bluetooth\n","connecting");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage,"connecting");

            } catch (Exception e) {
                appendToUI(e.getMessage(),"connecting");
            }
            return null;
        }
    }


    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n","connecting");
                return false;
            }
            client = BandClientManager.getInstance().create(mContext, devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Band is connecting...\n","connecting");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private void appendToUI(final String string, String extra) {
        //send an intent to Main Activity to reset the text view

        Intent intent=new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("com.sarthakghosh.musicon_spotifyscreen.Broadcast");
        Bundle passData = new Bundle();
        passData.putString(extra, string);
        intent.putExtras(passData);
        mainUiActivity.sendBroadcast(intent);
    }
}
