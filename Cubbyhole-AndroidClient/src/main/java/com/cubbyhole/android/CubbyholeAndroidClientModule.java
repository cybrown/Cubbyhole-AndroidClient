package com.cubbyhole.android;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
        MainActivity.class,
        HelloWorldService.class
    }
)
public class CubbyholeAndroidClientModule {
    private CubbyholeAndroidClientApp application;

    public CubbyholeAndroidClientModule(CubbyholeAndroidClientApp app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public HelloWorldService provideHelloWorldService() {
        return new HelloWorldService();
    }
}
