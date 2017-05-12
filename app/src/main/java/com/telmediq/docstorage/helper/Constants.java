package com.telmediq.docstorage.helper;

import com.telmediq.docstorage.BuildConfig;

/**
 * Created by sean on 2017-05-03.
 */

public class Constants {
	public static final String SERVER_URL = "app.telmediq.com";

	public interface Preference {
		String ACCESS_KEY = BuildConfig.APPLICATION_ID + ".accessKey";
		String SECRET_KEY = BuildConfig.APPLICATION_ID + ".secretKey";
	}

	public interface Extras {
		String FILE_ID = BuildConfig.APPLICATION_ID + ".fileId";
	}
}
