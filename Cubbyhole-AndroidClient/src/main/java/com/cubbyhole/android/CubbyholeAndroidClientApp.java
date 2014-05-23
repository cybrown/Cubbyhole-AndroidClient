package com.cubbyhole.android;

import android.app.Application;
import dagger.ObjectGraph;

public class CubbyholeAndroidClientApp extends Application {

    private ObjectGraph objectGraph;

    public ObjectGraph getObjectGraph() {
       return objectGraph;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new Object[]{new CubbyholeAndroidClientModule(this)});
    }
}
