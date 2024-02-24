package com.general.files;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import ciride.driver.BuildConfig;
import ciride.driver.R;
import com.utils.Utils;

import java.util.Random;

/**
 * Created by Admin on 20/03/18.
 */

public class LocalNotification {
    static Context mContext;

    private static String CHANNEL_ID = BuildConfig.APPLICATION_ID;
    private static NotificationManager mNotificationManager = null;

    public static void dispatchLocalNotification(Context context, String message, String title, boolean onlyInBackground) {
        mContext = context;

        String title1 = title;
        boolean ForwardToChat;
        if (title == null){
            title1 = mContext.getString(R.string.app_name);
            ForwardToChat = false;
        } else  {
            ForwardToChat = true;
        }

        if (MyApp.getInstance().getCurrentAct() == null && mContext == null) {
            return;
        }
        continueDispatchNotification(message, title1, onlyInBackground, ForwardToChat);

    }

    private static void continueDispatchNotification(String message, String title, boolean onlyInBackground, Boolean ForwardToChat) {
        Intent intent = null;
        if (Utils.getPreviousIntent(mContext) != null) {
            intent = Utils.getPreviousIntent(mContext);
            intent.putExtra("ForwardToChat", ForwardToChat);
        } else {
            intent = mContext
                    .getPackageManager()
                    .getLaunchIntentForPackage(mContext.getPackageName());
            intent.putExtra("ForwardToChat", ForwardToChat);

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }

        PendingIntent contentIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else
        {
            contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }

        GeneralFunctions generalFunctions = MyApp.getInstance().getGeneralFun(mContext);
        String userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);

        Uri soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;

        if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_1.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_1);
        } else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_2.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_2);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_3.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_3);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_4.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_4);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_5.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_5);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_6.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_6);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_7.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_7);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_8.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_8);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_9.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_9);
        }
        else if (generalFunctions.getJsonValue("PROVIDER_NOTIFICATION", userProfileJson).equalsIgnoreCase("notification_10.mp3")) {
            soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.notification_10);
        }

        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }

        // Receive Notifications in >26 version devices
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // mBuilder.setChannelId(BuildConfig.APPLICATION_ID);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_HIGH
            );
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            // channel.setSound(soundUri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_driver_logo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
               // .setSound(soundUri)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        Random random = new Random();

        int randomNumber = random.nextInt();


        if (onlyInBackground && MyApp.getInstance().isMyAppInBackGround()) {
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(randomNumber, mBuilder.build());
            playNotificationSound(soundUri);
        } else if (!onlyInBackground) {
//            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(randomNumber, mBuilder.build());
            playNotificationSound(soundUri);
        }


    }

    public static void playNotificationSound(Uri nofifyUrl) {
        try {

            Ringtone r = RingtoneManager.getRingtone(mContext, nofifyUrl);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllNotifications() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
        }
    }
}
