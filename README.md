# Context Accelerator SDK: Quick Start for Android

The Quick Start project illustrates minimal steps to integrate the SDK into your app.

## Add SDK to your project

Add Skyhook's Maven repository URL to the `repositories` section in your `build.gradle`:
```gradle
repositories {
    maven { url 'https://skyhookwireless.github.io/skyhook-context-android' }
}
```
Add SDK to the `dependencies` section:
```gradle
dependencies {
    compile 'com.skyhook.context:accelerator:2.1.1+'
}
```
Note that you can exclude transitive dependencies to resolve version conflicts, and include those dependencies separately:
```gradle
compile 'com.android.support:appcompat-v7:26.1.0'
compile('com.skyhook.context:accelerator:2.1.1+') {
    exclude module: 'support-v4'
}
```

## API key

Put your Skyhook API key under the `application` section in `AndroidManifest.xml`:
```xml
<meta-data android:name="com.skyhook.context.API_KEY"
           android:value="Put your key here"/>
```
You can obtain the API key from [my.skyhookwireless.com](https://my.skyhookwireless.com).

## Initialize Accelerator API

Add the following call in the `onCreate` method of your activity or application class:
```java
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Accelerator.init(this);
    ...
}
```
## Request location permission

In order to be able to start campaign monitoring, your app must first obtain
the `ACCESS_FINE_LOCATION` permission from the user:
```java
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Accelerator.init(this);
    ...
    ActivityCompat.requestPermissions(
        this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
}
```

## Start campaign monitoring

Once the location permission has been granted, you can start monitoring campaigns:
```java
public void onRequestPermissionsResult(
    final int requestCode, final String[] permissions, final int[] grantResults)
{
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Accelerator.startMonitoringForAllCampaigns();
    }
}
```

## Register for campaign monitoring events

In order to get notified of campaign events during monitoring, you need to
add a broadcast receiver to your application.

Add the following line in your `AndroidManifest.xml`:
```xml
<uses-permission android:name="com.skyhook.context.RECEIVE_EVENT"/>
```

Declare a receiver with the intent filter set to the `ACCELERATOR_EVENT` action:
```xml
<receiver android:name=".MyAcceleratorReceiver"
          android:exported="false">
    <intent-filter>
        <action android:name="com.skyhook.context.ACCELERATOR_EVENT"/>
    </intent-filter>
</receiver>
```

Implement the broadcast receiver:
```java
public void onReceive(Context context, Intent intent) {
    if (Accelerator.hasError(intent)) {
        int errorCode = Accelerator.getErrorCode(intent));
        // handle error
    } else {
        CampaignVenue venue = Accelerator.getTriggeringCampaignVenue(intent);
        if (venue != null) {
            int transition = Accelerator.getCampaignVenueTransition(intent);
            // handle transition
        }
    }
}
```
