package com.telmediq.docstorage.endpoint;

import com.telmediq.docstorage.model.AuthorizationResponse;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

	/* You shouldn't need everything when using the API
	@POST("/api/files/")
	Call<Response> addFile(
			@Field("name") String name,
			@Field("original_name") String original_name,
			@Field("size") Integer size,
			@Field("mime_type") String mime_type,
			@Field("folder") Integer folder,
			@Field("file") File file,
			@Field("owner") Integer owner
	);
	*/
	@POST("/api/folders/{folderId}/files/")
	@FormUrlEncoded
	Call<File> addFile(
			@Path("folderId") Integer folderId,
			@Field("name") String name,
			@Field("folder") Integer folder,
			@Field("file") File file,
			@Field("owner") Integer owner
	);

	@POST("/api/folders/")
	@FormUrlEncoded
	Call<Folder> addFolder(
			@Field("parent") Integer parent,
            @Field("name") String name
	);
}
