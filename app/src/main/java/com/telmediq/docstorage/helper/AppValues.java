package com.telmediq.docstorage.helper;

import android.content.SharedPreferences;

import com.telmediq.docstorage.TelmediqApplication;
import com.telmediq.docstorage.model.AuthorizationResponse;

import timber.log.Timber;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sean on 2017-05-03.
 */

public class AppValues {
	private static SharedPreferences sharedPrefs() {
		return getDefaultSharedPreferences(TelmediqApplication.getContext());
	}

	//<editor-fold desc="Endpoints">
	public static void setAccessToken(AuthorizationResponse authorization) {
		if (authorization == null) {
			sharedPrefs().edit().remove(Constants.Preference.ACCESS_TOKEN).apply();
			return;
		}
		sharedPrefs().edit().putString(Constants.Preference.ACCESS_TOKEN, authorization.getToken()).apply();
		Timber.i("Access token set: %s", authorization.getToken());
	}

	public static String getAccessToken() {
		return sharedPrefs().getString(Constants.Preference.ACCESS_TOKEN, "");
	}

	public static boolean hasAuthorization() {
		return !getAccessToken().isEmpty();
	}
	//</editor-fold>
}