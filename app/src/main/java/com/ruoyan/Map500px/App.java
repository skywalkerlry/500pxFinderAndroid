package com.ruoyan.map500px;

import android.app.Application;
import android.content.Context;

/**
 * Created by ruoyan on 2/13/15.
 */
public class App extends Application{
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
