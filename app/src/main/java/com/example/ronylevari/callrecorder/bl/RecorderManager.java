package com.example.ronylevari.callrecorder.bl;

import android.content.Context;

import java.util.ArrayList;

public class RecorderManager {

    private static RecorderManager sManager;
    private ArrayList<ParentRecordingItem> mItems = new ArrayList<>();
    private Context mAppContext;
    private boolean mIsRecording;

    private RecorderManager(Context appContext, boolean isRecording) {
        this.mAppContext = appContext;
        this.mIsRecording = isRecording;
    }

    public static RecorderManager get(Context context, boolean isRecording) {
        if (sManager == null) {
           sManager = new RecorderManager(context.getApplicationContext(), isRecording);
        }
        return sManager;
    }

    public void setmIsRecording(boolean isRecording) {
        this.mIsRecording = isRecording;
    }




}
