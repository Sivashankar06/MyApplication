package hobby.siva.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import java.util.Date;

/*
 * Copyright (c) 2018 Blue Jeans Network, Inc. All rights reserved.
 * Created by sarumugam on 29/11/18
 */
public class Util {

    private static NotificationCompat.Builder mNotification;

    private static long mSleepTime;

    public static void onDeviceMoved(Context context, NotificationManager manager){
        if(mSleepTime != 0) {
            NotificationCompat.Builder notify = getNotification(context);
            long mills = new Date().getTime() - mSleepTime;
            int seconds = (int) (mills/1000);
            int mins = (int) (mills/(1000*60)) % 60;
            int hours = (int) (mills/(1000 * 60 * 60));

            String diff;
            if(hours > 0 && mins > 0){
                diff = hours + " hours :" + mins + " minutes : "+seconds + " seconds";
            } else if(hours > 0 && mins == 0) {
                diff = hours + " hours :"+seconds + " seconds";
            } else if(hours == 0 && mins > 0) {
                diff = mins + " minutes : "+seconds + " seconds";
            } else {
                diff = seconds + " seconds";
            }
            notify.setContentTitle("Sleep Time is : " + diff);
            manager.notify(1988, notify.build());
            mSleepTime = 0;
        }
    }

    public static void onDeviceStoppedMoving() {
        if(mSleepTime == 0) {
            mSleepTime = new Date().getTime();
        }
    }

    public static NotificationCompat.Builder getNotification(Context context){
        if(mNotification == null){
            mNotification = createNotification(context);
        }
        return mNotification;
    }

    private static NotificationCompat.Builder createNotification(Context context) {
        // Prepare intent which is triggered if the notification is selected.
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        String title = context.getResources().getString(R.string.app_name);

        // Build notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "SleepTime");

        notification.setContentTitle(title)
                // Large icon appears,when you pull down the notification panel.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                // BG for the small icon in the notification panel.
                // status bar icon, as well as shown in the bottom right corner with the large icon.
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pIntent);
        return notification;
    }
}
