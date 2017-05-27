package com.telmediq.docstorage.endpoint;

import com.telmediq.docstorage.model.AuthorizationResponse;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Profile;
import com.telmediq.docstorage.model.Folder;

import java.util.Dictionary;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
}
