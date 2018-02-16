# Context Accelerator SDK: Quick Start for Android

The Quick Start project illustrates minimal steps to integrate the SDK into your app.

## Add SDK to your project

Add Skyhook's Maven repository URL to the `repositories` section in your `build.gradle`:
```
    repositories {
        maven { url 'https://skyhookwireless.github.io/skyhook-context-android' }
    }
```
Add SDK to the `dependencies` section:
```
    dependencies {
        compile 'com.skyhook.context:accelerator:2.1.1+'
    }
```
Note that you can exclude transitive dependencies to resolve version conflicts, and include those dependencies separately:
```
    compile 'com.android.support:appcompat-v7:26.1.0'
    compile('com.skyhook.context:accelerator:2.1.1+') {
        exclude module: 'support-v4'
    }
```

## API key

Put your Skyhook API key under the `application` section in `AndroidManifest.xml`:
```
    <meta-data android:name="com.skyhook.context.API_KEY"
               android:value="Put your key here"/>
```
You can obtain the API key from [my.skyhookwireless.com](https://my.skyhookwireless.com).

## Initialize Accelerator API

Add the following call to the `onCreate` method of your activity or application class:
```
    Accelerator.init(this);
```

## Campaign Monitoring

In order to receive campaign monitoring events in background you need to perform additional steps.

### Declare RECEIVE_EVENT permission

Add the following line to your `AndroidManifest.xml`:
```
<uses-permission android:name="com.skyhook.context.RECEIVE_EVENT"/>
```

### Add a broadcast receiver of ACCELERATOR_EVENT

The receiver should be declared like this:
```
    <receiver android:name=".AcceleratorReceiver"
              android:exported="false">
        <intent-filter>
            <action android:name="com.skyhook.context.ACCELERATOR_EVENT"/>
        </intent-filter>
    </receiver>
```
You need to implement the `AcceleratorReceiver` class in your project to handle the monitoring events.

## All set

Your app should be all set at this point. Please refer to the Quick Start project source code as an example.
