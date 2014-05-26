package com.cubbyhole.android;

import com.cubbyhole.android.activity.MainActivity;
import com.cubbyhole.android.fragment.FileListFragment;
import com.cubbyhole.client.http.BasicAuthInterceptor;
import com.cubbyhole.client.http.ConnectionInfo;
import com.cubbyhole.client.http.FileRestWebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(
    injects = {
        MainActivity.class,
        FileListFragment.class
    }
)
public class CubbyholeAndroidClientModule {
    private CubbyholeAndroidClientApp application;

    public CubbyholeAndroidClientModule(CubbyholeAndroidClientApp app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public ConnectionInfo provideConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setUsername("user");
        connectionInfo.setPassword("pass");
        connectionInfo.setHost("37.187.46.33");
        connectionInfo.setPort(80);
        connectionInfo.setPath("/api/v1/");
        connectionInfo.setProtocol("http");
        return connectionInfo;
    }

    @Provides
    @Singleton
    public FileRestWebService provideFileService(final BasicAuthInterceptor basicAuthInterceptor, final ConnectionInfo connectionInfo) {
        URL url = null;
        try {
            url = new URL(connectionInfo.getProtocol(), connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        return new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(url.toString())
                .setRequestInterceptor(basicAuthInterceptor)
                .build()
                .create(FileRestWebService.class);
    }
}
