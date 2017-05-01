package com.skyhook.samples.acceleratorquickstart;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.skyhookwireless.accelerator.AcceleratorClient;
import com.skyhookwireless.accelerator.AcceleratorStatusCodes;

import static com.skyhook.samples.acceleratorquickstart.Constants.TAG;

public class BootReceiver
    extends BroadcastReceiver
    implements AcceleratorClient.ConnectionCallbacks,
               AcceleratorClient.OnConnectionFailedListener,
               AcceleratorClient.OnRegisterForCampaignMonitoringResultListener
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AcceleratorIntentService.class);
        PendingIntent pendingIntent =
            PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "resuming monitoring after reboot");

        // Note that a temporary instance of AcceleratorClient is created for the sole purpose
        // of resuming monitoring. This instance will only exist in the context of
        // the BroadcastReceiver and its onReceive method, so it should not
        // be used for making any other accelerator calls.
        AcceleratorClient accelerator = new AcceleratorClient(context, Constants.API_KEY, this, this);
        accelerator.registerForCampaignMonitoring(pendingIntent, this);
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(int errorCode) {
    }

    @Override
    public void onRegisterForCampaignMonitoringResult(int statusCode,
                                                      PendingIntent pendingIntent) {
        if (statusCode == AcceleratorStatusCodes.SUCCESS) {
            Log.i(TAG, "resumed monitoring after reboot");
        } else {
            Log.e(TAG, "failed to resume monitoring after reboot");
        }
    }
}
