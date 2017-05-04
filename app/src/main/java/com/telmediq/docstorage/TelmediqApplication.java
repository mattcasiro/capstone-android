package com.telmediq.docstorage;

import android.app.Application;
import android.content.Context;

import com.telmediq.docstorage.helper.AppValues;
import com.telmediq.docstorage.helper.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by sean on 2017-05-03.
 */

public class TelmediqApplication extends Application {
	private static Context context;

	private static RealmConfiguration realmConfiguration;

	private Retrofit retrofit;

	@Override
	public void onCreate() {
		super.onCreate();
		this.context = this;

		setupRealm();
		Timber.plant(new Timber.DebugTree());
	}

	public static Context getContext() {
		return TelmediqApplication.context;
	}

	//<editor-fold desc="Realm">
	public void setupRealm() {
		Realm.init(this);
		getRealmConfiguration();
		Realm.setDefaultConfiguration(realmConfiguration);
	}

	public static RealmConfiguration getRealmConfiguration() {
		if (realmConfiguration == null) {
			realmConfiguration = new RealmConfiguration.Builder()
					.name("telmediq.realm")
					.schemaVersion(1)
					.deleteRealmIfMigrationNeeded()
					.build();
		}

		return realmConfiguration;
	}
	//</editor-fold>

	//<editor-fold desc="Retrofit">
	public Retrofit getRetrofit() {
		if (retrofit == null) {
			retrofit = createRetrofitInstance();
		}

		return retrofit;
	}

	public static Retrofit createRetrofitInstance() {
		Retrofit.Builder builder = new Retrofit.Builder()
				.baseUrl(Constants.SERVER_URL + "/");

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

		clientBuilder.addInterceptor(authorizationInterceptor);
		clientBuilder.addNetworkInterceptor(loggingInterceptor);

		builder.client(clientBuilder.build());
		return builder.build();
	}

	private static Interceptor authorizationInterceptor = new Interceptor() {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request originalRequest = chain.request();
			if (!AppValues.hasAuthorization()) {
				return chain.proceed(originalRequest);
			}

			Request.Builder requestBuilder = originalRequest.newBuilder()
					.addHeader("Authorization", AppValues.getAuthorization())
					.method(originalRequest.method(), originalRequest.body());
			Request request = requestBuilder.build();
			return chain.proceed(request);
		}
	};

	private static Interceptor loggingInterceptor = new Interceptor() {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			long startNs = System.nanoTime();
			Response response;

			try {
				response = chain.proceed(request);
			} catch (Exception e) {
				throw e;
			}

			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

			ResponseBody responseBody = response.body();
			BufferedSource source = responseBody.source();
			source.request(Long.MAX_VALUE); // Buffer the entire body.
			Buffer buffer = source.buffer();

			Timber.tag("OkHttp").d("%s %s - %s ms - %s bytes", request.method(), request.url().toString(), tookMs, buffer.size());

			if ((response.code() < 200 || response.code() > 299)) {
				Timber.tag("OkHttp").d("%s %s - %s: %s", request.method(), request.url().toString(), response.code(), response.body() != null ? response.body().toString() : "No body");
			}

			return response;
		}
	};
	//</editor-fold>
}
