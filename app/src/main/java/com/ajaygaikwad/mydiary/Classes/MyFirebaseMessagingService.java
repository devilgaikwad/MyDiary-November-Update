package com.ajaygaikwad.mydiary.Classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {


            sendNotification(remoteMessage);

        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Map<String, String> data= remoteMessage.getData();
        // String title = data.get("title");
        String title = "My Diary";
        String msg = data.get("body");

        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.insight);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), sound);
        r.play();


        int num = (int) System.currentTimeMillis();

        Intent intent = new Intent(this, MainNavActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, num /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioManager mobilemode = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mobilemode.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    mobilemode.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);


            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            String channelId = "MyDiary Operator";
            String channelName = "MyDiary Operator";

            NotificationChannel myChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//configuration

            myChannel.setSound(sound, attributes);
            myChannel.getAudioAttributes();
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(myChannel);

            /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Pinnaculum FinderOperator")
                    .setContentTitle(title)
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/wake_up_alarm"))
                    .setContentIntent(pendingIntent);

            Notification notification = notificationBuilder.build();

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(num *//* ID of notification *//*, notificationBuilder.build());
*/
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(getNotificationIcon())
                    //.setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setAutoCancel(true)
                    // .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder.setSound(sound);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }else{
/*

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"Pinnaculum FinderOperator")
                    //.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/wake_up_alarm"))
                    .setContentIntent(pendingIntent);

            Notification notification = notificationBuilder.build();

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(num */
/* ID of notification *//*
, notificationBuilder.build());
*/

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"FireBase Notification")
                    .setSmallIcon(R.drawable.appointment)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(getNotificationIcon())
                    //.setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setAutoCancel(true)
                    // .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder.setSound(sound);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        }
        }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.appointment : R.drawable.appointment;
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor=preferences.edit();

        editor.putString("firebasetoken",s).commit();

    }

}//service

