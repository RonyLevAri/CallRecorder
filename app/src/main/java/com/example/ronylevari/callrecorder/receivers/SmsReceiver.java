package com.example.ronylevari.callrecorder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    private String TAG = SmsReceiver.class.getSimpleName();

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String str = "";
        String form = "";

        if (intent.getAction().equals(SMS_RECEIVED)) {

            SmsMessage[] allMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

            for (int i = 0; i < allMessages.length; i++) {
                SmsMessage sms = allMessages[i];
                form = sms.getOriginatingAddress();
                str += sms.getMessageBody();
            }
        }
    }
}
