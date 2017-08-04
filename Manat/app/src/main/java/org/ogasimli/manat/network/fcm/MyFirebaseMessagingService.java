package org.ogasimli.manat.network.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import org.joda.time.DateTime;
import org.ogasimli.manat.helper.Constants;
import org.ogasimli.manat.ui.activity.MainActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import manat.ogasimli.org.manat.R;

/**
 * A service that extends FirebaseMessagingService to handle messages in the background
 *
 * Created by Orkhan Gasimli on 30.06.2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from FCM.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().get(Constants.FCM_MESSAGE_ACTION_KEY).equals
                    (Constants.FCM_TOPIC_NAME)) {
                Log.d(LOG_TAG, "Message data payload: " + remoteMessage.getData());
                String updateDate = remoteMessage.getData().get(Constants.FCM_MESSAGE_EXTRA_KEY);
                scheduleJob(updateDate);
                SharedPreferences sharedPref = getSharedPreferences(Constants.PREFERENCE_FILE_KEY,
                        Context.MODE_PRIVATE);
                boolean showNotification =
                        sharedPref.getBoolean(Constants.NOTIFICATION_SHOW_KEY, true);
                if (showNotification) {
                    sendNotification(updateDate);
                }
            }
        }
    }

    /**
     * Schedule a job using FirebaseJobDispatcher.
     *
     * @param date String representing the message received from FCM.
     */
    private void scheduleJob(String date) {
        FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Bundle jobExtra = new Bundle();
        jobExtra.putString(Constants.FCM_MESSAGE_EXTRA_KEY, date);
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .setExtras(jobExtra)
                .build();
        dispatcher.schedule(myJob);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        DateTime date = DateTime.parse(messageBody, Constants.DATE_FORMATTER_DDMMYYYY_WITH_DOT);
        String formattedDate = Constants.DATE_FORMATTER_DMMMMYYYY.print(date);
        messageBody = getString((R.string.fcm_notification_body),formattedDate);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_app_icon))
                .setContentTitle(getString(R.string.fcm_notification_title))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build());
    }
}
