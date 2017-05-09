package com.telmediq.docstorage.endpoint;

import com.telmediq.docstorage.model.AuthorizationResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
}
