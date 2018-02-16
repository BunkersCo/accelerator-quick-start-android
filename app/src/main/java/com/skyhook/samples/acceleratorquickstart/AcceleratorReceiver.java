package com.skyhook.samples.acceleratorquickstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.skyhook.context.Accelerator;
import com.skyhook.context.CampaignVenue;

public class AcceleratorReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Accelerator.hasError(intent)) {
            showMessage(context, "Accelerator error: " + Accelerator.getErrorCode(intent));
        } else {
            final int transition = Accelerator.getCampaignVenueTransition(intent);
            if (transition > 0) {
                final CampaignVenue venue = Accelerator.getTriggeringCampaignVenue(intent);
                if (venue != null) {
                    showMessage(context, String.format(
                        "%s %s",
                        transition == CampaignVenue.CAMPAIGN_VENUE_TRANSITION_ENTER
                            ? "Entered" : "Exited",
                        venue));
                    // TODO: add more cowbell
                }
            }
        }
    }

    private void showMessage(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
             .show();
    }
}
