package com.cubbyhole.android;

import com.cubbyhole.android.activity.MainActivity;
import com.cubbyhole.android.service.FileService;
import com.cubbyhole.android.service.HelloWorldService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module(
    injects = {
        MainActivity.class
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

    @Provides
    @Singleton
    public FileService provideFileService() {
        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade requestFacade) {
                requestFacade.addHeader("Authorization", "Basic dXNlcjpwYXNz");
            }
        };
        return new RestAdapter.Builder()
                .setEndpoint("http://192.168.1.97:3000/")
                .setRequestInterceptor(interceptor)
                .build()
                .create(FileService.class);
    }
}
