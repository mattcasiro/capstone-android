package com.telmediq.docstorage.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.telmediq.docstorage.model.AuthorizationResponse;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Profile;
import com.telmediq.docstorage.model.Folder;

import java.util.Dictionary;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by MCMBP on 2017-05-03.
 */

public interface TelmediqService {
	@POST("api/login/")
	@FormUrlEncoded
	Call<AuthorizationResponse> login(
			@Field("email") String email,
			@Field("password") String password
	);

	@GET("api/files/")
	Call<List<File>> getFiles();

	@GET("api/profile/")
	Call<Profile> getProfile();

	@PUT("api/profile/")
	@FormUrlEncoded
	Call<Profile> putProfile(
			@Field("first_name") String firstName,
	        @Field("last_name") String lastName
	);
	
	@GET("api/folders/")
	Call<List<Folder>> getFolders();

	@DELETE("api/folders/{folderId}/files/{fileId}/")
	Call<File> deleteFile(
			@Path("folderId") Integer folderId,
			@Path("fileId") Integer fileId
	);

	@DELETE("api/folders/{folderId}/")
	Call<Folder> deleteFolder(
			@Path("folderId") Integer folderId
	);

	@Multipart
	@POST("/api/folders/{folderId}/files/")
	Call<File> addFile(
			@Path("folderId") Integer folderId,
			@Part MultipartBody.Part file,
			@Part("name") RequestBody fileName
	);

	@POST("/api/folders/")
	@FormUrlEncoded
	Call<Folder> addFolder(
			@Field("parent") Integer parent,
            @Field("name") String name
	);

	@PUT("/api/folders/{folderId}/files/{fileId}/")
	@FormUrlEncoded
	Call<File> renameFile(
			@Path("folderId") Integer folderId,
			@Path("fileId") Integer fileId,
	        @Field("name") String name
	);

	@PUT("api/folders/{folderId}/")
	@FormUrlEncoded
	Call<Folder> renameFolder(
			@Path("folderId") Integer folderId,
			@Field("name") String s
	);
}
