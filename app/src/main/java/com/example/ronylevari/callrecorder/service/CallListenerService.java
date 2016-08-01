package com.example.ronylevari.callrecorder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ronylevari.callrecorder.MainActivity;
import com.example.ronylevari.callrecorder.R;
import com.example.ronylevari.callrecorder.bl.ParentRecordingItem;
import com.example.ronylevari.callrecorder.constants.AppConstants;
import com.example.ronylevari.callrecorder.database.DatabaseAdapter;

public class CallListenerService extends Service {

    public static final String TAG = "CallListenerService";

    public static final String STOP_SERVICE_ACTION = "notify callRecorder service to stop running";
    public static final String STOP_SERVICE_BROADCAST_KEY = "stop callRecorder service intent key";
    public static final int STOP_SERVICE_REQUEST = 1;

    NotifyStopServiceReceiver mNotifyStopServiceReceiver;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private long parentRecordingId;

    private String defaultReturnMessage = "I am unable to answer the phone at the moment, please send a message and I'll get back to you ASAP";

    public class NotifyStopServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            Log.d(TAG, "on receive stop command");

            int rqs = arg1.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);

            if (rqs == STOP_SERVICE_REQUEST){
                stopSelf();
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancelAll();
            }
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d(TAG, "service onCreate");

        // To avoid cpu-blocking, we create a background handler to run our service
        //HandlerThread thread = new HandlerThread("TutorialService", Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        //thread.start();
        //mServiceLooper = thread.getLooper();
        // start the service using the background handler
        //mServiceHandler = new ServiceHandler(mServiceLooper);

        // create new parent recording
        parentRecordingId = createNewParentRecording();

        // update sharedPreferences that is recording
        updateSharedRecording(true);

        // get user's default return message from sharedPreference
        this.defaultReturnMessage = loadReturnMessageFromSharedPref();

        // set stop service receiver
        mNotifyStopServiceReceiver = new NotifyStopServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STOP_SERVICE_ACTION);
        registerReceiver(mNotifyStopServiceReceiver, intentFilter);

        // Send Notification that service is running to notification bar
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final int NOTIFICATION_IDENTIFIER = 0;
        final int PENDING_INTENT_IDENTIFIER = (int) System.currentTimeMillis();

        Intent myIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_IDENTIFIER, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTitle = "Demo of Notification!";
        String notificationText = "Course Website";

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_hearing_white_24dp)
                .setAutoCancel(false)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentText(notificationText)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setOngoing(true)
                .build();

        notificationManager.notify(NOTIFICATION_IDENTIFIER, notification);

    }

    private long createNewParentRecording() {
        long id = -1;
        DatabaseAdapter dbAdapter = new DatabaseAdapter(this);
        ParentRecordingItem parent = new ParentRecordingItem();
        parent.setStartTime(System.currentTimeMillis());
        parent.setNumTrashedChildren(0);
        parent.setRecordName("");
        parent.setNumActiveChildren(0);
        parent.setIsClosed(false);
        parent.setIsTrashed(false);
        id = dbAdapter.insertParentToDatabase(parent);
        return id;
    }

    private void updateSharedRecording(boolean isRecording) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AppConstants.SHARED_PREFS_IS_RECORDING_KEY, isRecording);
        editor.commit();
    }

    private String loadReturnMessageFromSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(AppConstants.SHARED_PREFS_RETURN_MESSAGE_KEY, defaultReturnMessage);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "on start command");

        // call a new service handler. The service ID can be used to identify the service
//        Message message = mServiceHandler.obtainMessage();
//        message.arg1 = startId;
//        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateSharedRecording(false);
        closeParentRecordingInDatabase(parentRecordingId);
        unregisterReceiver(mNotifyStopServiceReceiver);
    }

    private void closeParentRecordingInDatabase(long parentRecordingId) {


    }

    // TODO
    public void startListening() {
        // register two broadcast receivers - one for SMS one for calls
    }

    // TODO
    public void processIncomingCall() {

    }

    // TODO
    public void sendDefaultReturnMessageToCaller(String destinationNumber, String smsText) {
        // Get the default instance of SmsManager
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        smsManager.sendTextMessage(destinationNumber, null, smsText, null, null);
    }

    // TODO
    public void updateDataBase() {

    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Well calling mServiceHandler.sendMessage(message); from onStartCommand,
            // this method will be called.

            // Add your cpu-blocking activity here
            // Well calling mServiceHandler.sendMessage(message);
            // from onStartCommand this method will be called.

            // Add your cpu-blocking activity here
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            showToast("Finishing TutorialService, id: " + msg.arg1);
            // the msg.arg1 is the startId used in the onStartCommand,
            // so we can track the running sevice here.
            stopSelf(msg.arg1);
        }
    }
}
