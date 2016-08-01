package com.example.ronylevari.callrecorder;

import android.content.Context;

import com.example.ronylevari.callrecorder.bl.ParentRecordingItem;

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

    public void setIsRecording(boolean isRecording) {
        this.mIsRecording = isRecording;
    }




}
