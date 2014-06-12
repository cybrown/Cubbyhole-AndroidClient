package com.cubbyhole.client.http;

import com.cubbyhole.client.model.Account;
import com.cubbyhole.client.model.PartialAccount;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface AccountRestWebService {
    @GET("/whoami") Observable<Account> whoami();
    @GET("/partial/starts-with/{begin}") Observable<List<PartialAccount>> findStartsWith(@Path("begin") String begin);
    @GET("/partial/by-id/{accountId}") Observable<PartialAccount> findPartialById(@Path("accountId") long accountId);
    @GET("/partial/by-username/{username}") Observable<PartialAccount> findPartialByUsername(@Path("username") String username);
}
