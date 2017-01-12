package org.ris3.zc.hello;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
/**
 * Created by zhisun on 1/11/17.
 */

public class MyApplication extends Application {
    final static String TAG="MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Log.d(TAG, " is called!");

    }
}
