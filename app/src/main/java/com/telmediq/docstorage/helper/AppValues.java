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
	public static void setAuthorization(AuthorizationResponse authorization) {
		if (authorization == null) {
			sharedPrefs().edit().remove(Constants.Preference.ACCESS_KEY).apply();
			sharedPrefs().edit().remove(Constants.Preference.SECRET_KEY).apply();
			return;
		}
		sharedPrefs().edit().putString(Constants.Preference.ACCESS_KEY, authorization.getAccess_key_id()).apply();
		Timber.i("Access key set: %s", authorization.getAccess_key_id());

		sharedPrefs().edit().putString(Constants.Preference.SECRET_KEY, authorization.getSecret_access_key()).apply();
		Timber.i("Secret key set: %s", authorization.getSecret_access_key());
	}

	private static AuthorizationResponse getAuthorizationResponse() {
		AuthorizationResponse authorization = new AuthorizationResponse();
		authorization.setAccess_key_id(sharedPrefs().getString(Constants.Preference.ACCESS_KEY, ""));
		authorization.setSecret_access_key(sharedPrefs().getString(Constants.Preference.SECRET_KEY, ""));
		return authorization;
	}

	public static boolean hasAuthorization() {
		return !getAuthorizationResponse().getAccess_key_id().isEmpty() && !getAuthorizationResponse().getSecret_access_key().isEmpty();
	}

	public static String getAuthorization() {
		AuthorizationResponse response = getAuthorizationResponse();
		try {
			return "Token " + response.getAccess_key_id();
		} catch (Exception exception) {
			return "";
		}
	}
	//</editor-fold>
}