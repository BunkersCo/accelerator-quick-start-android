package com.skyhook.samples.acceleratorquickstart;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.skyhookwireless.accelerator.AcceleratorClient;
import com.skyhookwireless.accelerator.AcceleratorClient.OnRegisterForCampaignMonitoringResultListener;
import com.skyhookwireless.accelerator.AcceleratorClient.OnStartCampaignMonitoringResultListener;
import com.skyhookwireless.accelerator.AcceleratorClient.OnStopCampaignMonitoringResultListener;
import com.skyhookwireless.accelerator.AcceleratorStatusCodes;

import static com.skyhook.samples.acceleratorquickstart.Constants.TAG;

public class MainActivity
    extends AppCompatActivity
    implements AcceleratorClient.ConnectionCallbacks,
               AcceleratorClient.OnConnectionFailedListener,
               OnStartCampaignMonitoringResultListener,
               OnStopCampaignMonitoringResultListener,
               OnRegisterForCampaignMonitoringResultListener
{
    private AcceleratorClient accelerator;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        accelerator = new AcceleratorClient(this, Constants.API_KEY, this, this);

        checkLocationPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        accelerator.disconnect();
    }

    @Override
    public void onConnected() {
        Log.i(TAG, "connected to accelerator");

        // Before calling any of the campaign monitoring methods,
        // a pending intent first needs to be registered for campaign monitoring,
        // and before calling the isMonitoringAllCampaigns() or getMonitoredCampaigns()
        // methods, that registration must be complete.

        Intent intent = new Intent(this, AcceleratorIntentService.class);
        PendingIntent pendingIntent =
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        accelerator.registerForCampaignMonitoring(pendingIntent, this);
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "disconnected from accelerator");
    }

    @Override
    public void onConnectionFailed(int errorCode) {
        // handle connection failure...
        Log.e(TAG, "failed to connect");
        tv.setText("Connection failed :(");
    }

    @Override
    public void onRegisterForCampaignMonitoringResult(int statusCode,
                                                      PendingIntent pendingIntent) {
        if (statusCode == AcceleratorStatusCodes.SUCCESS) {
            Log.i(TAG, "registered for monitoring");

            // The isMonitoringAllCampaigns and getMonitoredCampaigns methods
            // can also be called here if desired.
            accelerator.startMonitoringForAllCampaigns(this);
        } else {
            Log.e(TAG, "failed to register: " + statusCode);
            tv.setText("Registration failed :(");
        }
    }

    @Override
    public void onStartCampaignMonitoringResult(int statusCode, String campaignName) {
        if (statusCode == AcceleratorStatusCodes.SUCCESS) {
            Log.i(TAG, "started monitoring");

            boolean isMonitoringAll = accelerator.isMonitoringAllCampaigns();
            if (isMonitoringAll) {
                tv.setText("It works!");
            } else {
                tv.setText("Something is broken :(");
            }
        } else {
            Log.e(TAG, "failed to start monitoring: " + statusCode);
        }
    }

    @Override
    public void onStopCampaignMonitoringResult(int statusCode, String campaignName) {
        if (statusCode == AcceleratorStatusCodes.SUCCESS) {
            Log.i(TAG, "stopped monitoring");
        } else {
            Log.e(TAG, "failed to stop monitoring: " + statusCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permissions,
                                           final int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            connect();
        } else {
            tv.setText("Permission denied");
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
        } else {
            connect();
        }
    }

    private void connect() {
        notifyAlwaysAllowScanning();
        notifyLocationSetting();

        accelerator.connect();
    }

    // Starting with API version 18, Android supports Wi-Fi scanning when Wi-Fi is
    // disabled for connectivity purposes.
    // To improve location accuracy we encourage users to enable this feature.
    // The code below provides a simple example where we use Android's built
    // in dialog to notify the user.
    private void notifyAlwaysAllowScanning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (!wifi.isWifiEnabled() && !wifi.isScanAlwaysAvailable()) {
                final Intent intent =
                    new Intent(WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE);
                startActivityForResult(intent, 1);
            }
        }
    }

    // Notify the user if location is disabled system wide.
    private void notifyLocationSetting() {
        final LocationManager locationManager =
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final boolean networkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        final boolean gpsEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!networkEnabled && !gpsEnabled) {
            displayLocationDialog("Location services disabled",
                                  "Location services have been disabled. Please enable location.");
        } else if (!networkEnabled) {
            displayLocationDialog("Location performance",
                                  "Location performance will be improved by enabling network based location.");
        } else if (!gpsEnabled) {
            displayLocationDialog("Location performance",
                                  "Location performance will be improved by enabling gps based location.");
        }
    }

    private void displayLocationDialog(final String title, final String message) {
        final DialogInterface.OnClickListener launchSettingsListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton("Settings", launchSettingsListener)
               .setNegativeButton("Cancel", null)
               .create()
               .show();
    }
}
