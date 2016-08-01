package com.example.ronylevari.callrecorder.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NotificationService extends IntentService {

    public static final String TAG ="alarmService";

    public NotificationService() {
        super("NotificationService");
        Log.d(TAG , "CREATING SERVICE");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG , "EXECUTING INTENT");
    }

}
