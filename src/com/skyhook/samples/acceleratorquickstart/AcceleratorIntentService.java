package com.skyhook.samples.acceleratorquickstart;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.skyhookwireless.accelerator.AcceleratorClient;
import com.skyhookwireless.accelerator.CampaignVenue;

import static com.skyhook.samples.acceleratorquickstart.Constants.TAG;

public class AcceleratorIntentService
    extends IntentService {

    private final static int NOTIFICATION_ENTER = 1;
    private final static int NOTIFICATION_EXIT = 2;
    private final static int NOTIFICATION_ERROR = 3;

    public AcceleratorIntentService() {
        super("AcceleratorIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        CampaignVenue venue = AcceleratorClient.getTriggeringCampaignVenue(intent);
        if (venue != null) {
            switch (AcceleratorClient.getCampaignVenueTransition(intent)) {
                case CampaignVenue.CAMPAIGN_VENUE_TRANSITION_ENTER:
                    Log.i(TAG, "CAMPAIGN_VENUE_TRANSITION_ENTER: " + venue);
                    showNotification(NOTIFICATION_ENTER, "Entered Venue", venue.toString());
                    break;
                case CampaignVenue.CAMPAIGN_VENUE_TRANSITION_EXIT:
                    Log.i(TAG, "CAMPAIGN_VENUE_TRANSITION_EXIT: " + venue);
                    showNotification(NOTIFICATION_EXIT, "Exited Venue", venue.toString());
                    break;
                default:
                    Log.e(TAG, "unknown trigger type: " + venue);
            }
        } else if (AcceleratorClient.hasError(intent)) {
            int errorCode = AcceleratorClient.getErrorCode(intent);
            Log.e(TAG, "accelerator error: " + errorCode);
            showNotification(NOTIFICATION_ERROR, "Accelerator Error", "Error Code: " + errorCode);
        } else {
            Log.i(TAG, "unknown intent type from accelerator");
        }
    }

    private void showNotification(final int id, final String title, final String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification n =
            builder.setContentIntent(getActivityPendingIntent())
                   .setSmallIcon(R.drawable.skyhook_logo)
                   .setTicker(title)
                   .setWhen(System.currentTimeMillis())
                   .setLights(0xffff00ff, 300, 1000)
                   .setAutoCancel(true)
                   .setContentTitle(title)
                   .setContentText(message)
                   .build();

        n.defaults |= Notification.DEFAULT_SOUND;
        n.defaults |= Notification.DEFAULT_VIBRATE;
        n.flags |=  Notification.FLAG_SHOW_LIGHTS;

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(id, n);
    }

    private PendingIntent getActivityPendingIntent()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
