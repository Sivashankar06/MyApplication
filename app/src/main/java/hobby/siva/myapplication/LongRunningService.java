package hobby.siva.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/*
 * Copyright (c) 2018 Blue Jeans Network, Inc. All rights reserved.
 * Created by sarumugam on 28/11/18
 */
public class LongRunningService extends Service implements SensorEventListener {

    private static final String TAG = LongRunningService.class.getSimpleName();
    private boolean start = true;
    private float xLast, yLast, zLast;
    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    public static void startLongRunningService(Context context) {
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(context, LongRunningService.class);
        context.startService(serviceIntent);
    }

    public static void stopLongRunningService(Context context) {
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(context, LongRunningService.class);
        context.stopService(serviceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNotificationManager = getSystemService(NotificationManager.class);
        } else {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(new NotificationChannel( "SleepTime","Notify",
                    NotificationManager.IMPORTANCE_DEFAULT));
        }
        NotificationCompat.Builder notification = Util.getNotification(this);

        startForeground(1988, notification.build());

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float xCurrent = event.values[0]; // Get current x
        float yCurrent = event.values[1]; // Get current y
        float zCurrent = event.values[2]; // Get current y
        if (start) {
            // Initialize last x and y
            xLast = xCurrent;
            yLast = yCurrent;
            zLast = zCurrent;
            start = false;
        } else {
            // Calculate variation between last x and current x, last y and current y
            float xDelta = xLast - xCurrent;
            float yDelta = yLast - yCurrent;
            float zDelta = zLast - zCurrent;
            if ((Math.sqrt(xDelta * xDelta / 2) > 1) || (Math.sqrt(yDelta * yDelta / 2) > 1) || (Math.sqrt(zDelta * zDelta / 2) > 1)) {
                Util.onDeviceMoved(LongRunningService.this, mNotificationManager);
            } else {
                Util.onDeviceStoppedMoving();
            }

            // Update last x and y
            xLast = xCurrent;
            yLast = yCurrent;
            zLast = zCurrent;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
