package com.cubbyhole.client.http;

import com.cubbyhole.client.model.Account;

import retrofit.http.GET;
import rx.Observable;

public interface AccountRestWebService {
    @GET("/whoami") Observable<Account> whoami();
}
