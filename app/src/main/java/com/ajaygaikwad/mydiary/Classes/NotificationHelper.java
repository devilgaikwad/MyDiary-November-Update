package com.ajaygaikwad.mydiary.Classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;


import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;

public class NotificationHelper extends ContextWrapper {

    Context context;

    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "com.android.application.ONE";
    public static final String CHANNEL_ONE_NAME = "Channel One";
    public static final String CHANNEL_TWO_ID = "com.android.application.TWO";
    public static final String CHANNEL_TWO_NAME = "Channel Two";


    public  NotificationHelper(Context context){
        super(context);
        this.context = context;
        createChannels();
    }




    private void createChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = null;

            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);

            NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_TWO_ID,
                    CHANNEL_TWO_NAME, notifManager.IMPORTANCE_DEFAULT);
            notificationChannel2.enableLights(true);
            notificationChannel2.enableVibration(true);
            notificationChannel2.setLightColor(Color.RED);
            notificationChannel2.setShowBadge(false);
            notificationChannel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel2);
        }else{}
    }

    //Create the notification that’ll be posted to Channel One//
    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    public Notification.Builder getNotification1(String title, String body) {

        Intent intent = new Intent(this, MainNavActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pIntent)
                    .setAutoCancel(false)
                    .setSound(uri);
        }else {}

        return null;
    }

//Create the notification that’ll be posted to Channel Two//
    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.greetcard);
    Bitmap smallIcon = BitmapFactory.decodeResource(getResources(), R.drawable.appointment);

    Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.insight);

    public Notification.Builder getNotification2(String title, String body) {

        Intent intent = new Intent(this, MainNavActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AudioAttributes attributes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            return new Notification.Builder(getApplicationContext(), CHANNEL_TWO_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.appointment)
                    .setAutoCancel(false)
                    .setContentIntent(pIntent)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(largeIcon).bigLargeIcon(smallIcon))
                    .setSound(sound, attributes);



        }
        return null;
    }



    public void notify(int notification_two, Notification.Builder notification) {
        getManager().notify(notification_two, notification.build());
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}
