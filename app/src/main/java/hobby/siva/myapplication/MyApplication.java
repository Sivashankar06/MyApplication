package hobby.siva.myapplication;

import android.app.Application;
import android.content.Intent;

/*
 * Copyright (c) 2018 Blue Jeans Network, Inc. All rights reserved.
 * Created by sarumugam on 28/11/18
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LongRunningService.startLongRunningService(getApplicationContext());
    }
}
