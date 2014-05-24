package com.cubbyhole.client.http;

import android.util.Base64;

import javax.inject.Inject;

import retrofit.RequestInterceptor;

public class BasicAuthInterceptor implements RequestInterceptor {

    private final ConnectionInfo connectionInfo;

    @Inject
    public BasicAuthInterceptor(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public void intercept(RequestFacade requestFacade) {
        StringBuilder sb = new StringBuilder();
        sb.append(connectionInfo.getUsername());
        sb.append(':');
        sb.append(connectionInfo.getPassword());
        String b64info = Base64.encodeToString(sb.toString().getBytes(), 0);
        requestFacade.addHeader("Authorization", "Basic " + b64info);
    }
}
