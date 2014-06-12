package com.cubbyhole.client.http;

import com.cubbyhole.client.model.File;
import com.cubbyhole.client.model.Share;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.mime.TypedOutput;
import rx.Observable;

public interface FileRestWebService {
    @GET("/files/") Observable<List<File>> listRoot();
    @GET("/files/shared") Observable<List<File>> listShared();
    @GET("/files/{file}") Observable<File> find(@Path("file") long fileId);
    @GET("/files/{file}/list") Observable<List<File>> list(@Path("file") long fileId);
    @PUT("/files") Observable<File> create(@Body File file);
    @POST("/files/{file}/copy") Observable<Void> copy(@Path("file") long fileId, @Body File file);
    @POST("/files/{file}") Observable<Void> save(@Path("file") long fileId, @Body File file);
    @POST("/files/{file}/link") Observable<File> createLink(@Path("file") long fileId);
    @DELETE("/files/{file}") Observable<Void> delete(@Path("file") long fileId);
    @PUT("/files/{file}/raw") Observable<Void> write(@Path("file") long fileId, @Body TypedOutput data);
    @PUT("/files/{file}/shares/{perm}") @FormUrlEncoded Observable<Void> addPermission(@Path("file") long fileId, @Path("perm") String perm, @Field("account") long account);
    @GET("/files/{file}/shares") Observable<List<Share>> getPermissions(@Path("file") long fileId);
    @DELETE("/files/{file}/shares/{shareId}") Observable<Void> removePermission(@Path("file") long fileId, @Path("shareId") Long shareId);
}
