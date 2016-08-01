package com.example.ronylevari.callrecorder.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import com.example.ronylevari.callrecorder.constants.AppConstants;
import com.example.ronylevari.callrecorder.database.DatabaseAdapter;


public class RecordFlowIntentService extends IntentService {

    public static final String TAG = "RecordFlowIntentService";

    public static final String ACTION_INCOMING_RECEIVED = "com.example.ronylevari.callrecorder.service.action.ACTION_INCOMING_RECEIVED";

    public static final String EXTRA_CALLER = "com.example.ronylevari.callrecorder.service.extra.CALLER";
    public static final String EXTRA_MESSAGE = "com.example.ronylevari.callrecorder.service.extra.MESSAGE";
    public static final String EXTRA_INCOMING_TIME = "com.example.ronylevari.callrecorder.service.extra.EXTRA_INCOMING_TIME";

    private String defaultReturnMessage = "I am unable to answer the phone at the moment, please send a message and I'll get back to you ASAP";

    public RecordFlowIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action == ACTION_INCOMING_RECEIVED) {
                final String caller = intent.getStringExtra(EXTRA_CALLER);
                final long time = intent.getLongExtra(EXTRA_INCOMING_TIME, 0L);
                final String message = intent.getStringExtra(EXTRA_MESSAGE);

                // get user's default return message from sharedPreference
                this.defaultReturnMessage = loadReturnMessageFromSharedPref();

                handleActionIncomingCall(caller, time, message, defaultReturnMessage);
            }
        }
    }

    private void handleActionIncomingCall(String caller, long time, String message, String returnMessage) {
        //ContactsContract.Contacts contact =  retrieveFromContacts();
        sendDefaultReturnMessageToCaller(caller, returnMessage);
        writeIncomingRecorToDatabase(caller, time, message);
    }

    private void sendDefaultReturnMessageToCaller(String destinationNumber, String smsText) {
        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        smsManager.sendTextMessage(destinationNumber, null, smsText, null, null);
    }

    private String loadReturnMessageFromSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(AppConstants.SHARED_PREFS_RETURN_MESSAGE_KEY, defaultReturnMessage);
    }

    private void writeIncomingRecorToDatabase(String caller, long time, String message) {
        DatabaseAdapter dbAdapter = new DatabaseAdapter(this);



    }

}
