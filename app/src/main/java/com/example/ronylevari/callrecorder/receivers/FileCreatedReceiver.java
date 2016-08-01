package com.example.ronylevari.callrecorder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileCreatedReceiver extends BroadcastReceiver {

    public static final String ACTION_EXPORT_TO_MAIL = "com.example.ronylevari.callrecorder.service.action.ACTION_EXPORT_TO_MAIL";
    public static final String EXTRA_PATH = "com.example.ronylevari.callrecorder.service.action.PATH";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_EXPORT_TO_MAIL)) {
            String path = intent.getStringExtra(EXTRA_PATH);
            sentAttachedMail(context, path);
        }
    }

    private void sentAttachedMail(Context context, String path) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Phone Recordings");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "Sending email..."));
        }
    }
}
