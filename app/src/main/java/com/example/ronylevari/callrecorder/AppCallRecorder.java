package com.example.ronylevari.callrecorder;

import android.app.Application;

import com.example.ronylevari.callrecorder.database.DatabaseAdapter;

public class AppCallRecorder extends Application {

    private DatabaseAdapter mDBAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mDBAdapter = new DatabaseAdapter(this);
    }

    public DatabaseAdapter getDatabaseAdapter() {
        return mDBAdapter;
    }
}
