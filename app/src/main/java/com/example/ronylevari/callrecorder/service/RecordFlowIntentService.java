package com.example.ronylevari.callrecorder.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.example.ronylevari.callrecorder.bl.ChildRecordItem;
import com.example.ronylevari.callrecorder.constants.AppConstants;
import com.example.ronylevari.callrecorder.database.DatabaseAdapter;
import com.example.ronylevari.callrecorder.utils.Utils;


public class RecordFlowIntentService extends IntentService {

    public static final String TAG = "RecordFlowIntentService";

    public static final String ACTION_INCOMING_RECEIVED = "com.example.ronylevari.callrecorder.service.action.ACTION_INCOMING_RECEIVED";
    public static final String ACTION_INCOMING_SMS = "com.example.ronylevari.callrecorder.service.action.ACTION_INCOMING_SMS";

    public static final String EXTRA_CALLER = "com.example.ronylevari.callrecorder.service.extra.CALLER";
    public static final String EXTRA_MESSAGE = "com.example.ronylevari.callrecorder.service.extra.MESSAGE";
    public static final String EXTRA_INCOMING_TIME = "com.example.ronylevari.callrecorder.service.extra.EXTRA_INCOMING_TIME";

    private String defaultReturnMessage = "I am unable to answer the phone at the moment, please send a message and I'll get back to you ASAP";

    public static final String SMS_BODY = "sms_body";

    public RecordFlowIntentService() {
        super(TAG);
    }

    private boolean mIsSMS;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_INCOMING_RECEIVED)) {
                mIsSMS = false;
            } else {
                mIsSMS = true;
            }
            if ((action.equals(ACTION_INCOMING_RECEIVED)) || action.equals(ACTION_INCOMING_SMS)) {
                final String caller = intent.getStringExtra(EXTRA_CALLER);
                final long time = intent.getLongExtra(EXTRA_INCOMING_TIME, 0L);
                final String message = intent.getStringExtra(EXTRA_MESSAGE);

                // get user's default return message from sharedPreference
                this.defaultReturnMessage = loadReturnMessageFromSharedPref();

                handleActionIncomingCall(caller, time, message, defaultReturnMessage, mIsSMS);
            }
        }
    }

    private void handleActionIncomingCall(String caller, long time, String message, String returnMessage, boolean isSms) {
        //ContactsContract.Contacts contact =  retrieveFromContacts();
        long parentId = loadParenIdFromSharedPrefrences();
        sendDefaultReturnMessageToCaller(caller, returnMessage);
        writeIncomingRecordToDatabase(parentId, caller, time, message, isSms);
    }

    private long loadParenIdFromSharedPrefrences() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getLong(AppConstants.SHARED_PREFS_CURRENT_PARENT_RECORDING_KEY, 1L);
    }

    private void sendDefaultReturnMessageToCaller(String destinationNumber, String smsText) {
        // Get the default instance of SmsManager
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + destinationNumber));
//        smsIntent.putExtra(SMS_BODY, smsText);
//        startActivity(smsIntent);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destinationNumber, null, smsText, null, null);
    }

    private String loadReturnMessageFromSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(AppConstants.SHARED_PREFS_RETURN_MESSAGE_KEY, defaultReturnMessage);
    }

    private void writeIncomingRecordToDatabase(long parentId, String caller, long time, String message, boolean isSms) {
        ChildRecordItem c = new ChildRecordItem();
        c.setParent(parentId);
        c.setCaller(caller);
        c.setCallTime(time);
        c.setPhoneNumber(caller);
        c.setMessage(message);
        c.setIsSMS(isSms);
        c.setIsTrashed(false);

        Intent intent = new Intent(this, DatabaseService.class);
        intent.setAction(DatabaseService.ACTION_INSERT_CHILD);
        intent.putExtra(DatabaseService.EXTRA_CHILD, c);
        startService(intent);

    }

}
