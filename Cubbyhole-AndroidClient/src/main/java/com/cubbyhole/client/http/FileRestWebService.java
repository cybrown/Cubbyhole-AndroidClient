package com.cubbyhole.client.http;

import com.cubbyhole.client.model.File;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public interface FileRestWebService {
    @GET("/files/") Observable<List<File>> listRoot();
    @GET("/files/{file}") Observable<File> find(@Path("file") long fileId);
    @GET("/files/{file}/list") Observable<List<File>> list(@Path("file") long fileId);
    @PUT("/files") Observable<Void> create(@Body File file);
    @POST("/files/{file}") Observable<Void> save(@Path("file") long fileId, @Body File file);
    @DELETE("/files/{file}") Observable<Void> delete(@Path("file") long fileId);
}
