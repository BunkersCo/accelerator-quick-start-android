package com.skyhook.samples.acceleratorquickstart;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.skyhook.context.Accelerator;
import com.skyhook.task.OnFailureListener;
import com.skyhook.task.OnSuccessListener;

public class MainActivity
    extends AppCompatActivity
{
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);

        Accelerator.init(this);

        ActivityCompat.requestPermissions(
            this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
    }

    @Override
    public void onRequestPermissionsResult(
        final int requestCode, final String[] permissions, final int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMonitoring();
        } else {
            tv.setText("Permission denied");
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void startMonitoring() {
        Accelerator.startMonitoringForAllCampaigns()
                   .setOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           tv.setText("Monitoring started");
                       }
                   })
                   .setOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(final int code) {
                           tv.setText("Error: " + code);
                       }
                   });
    }
}
