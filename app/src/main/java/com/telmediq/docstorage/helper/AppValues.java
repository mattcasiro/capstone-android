package com.telmediq.docstorage.helper;

import android.content.SharedPreferences;

import com.telmediq.docstorage.TelmediqApplication;
import com.telmediq.docstorage.activity.HomeActivity;
import com.telmediq.docstorage.model.AuthorizationResponse;

import java.util.Map;

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

	public static void setRootFolderId(AuthorizationResponse authorization) {
		if (authorization == null) {
			sharedPrefs().edit().remove(Constants.Preference.ROOT_FOLDER_ID).apply();
			return;
		}
		sharedPrefs().edit().putInt(Constants.Preference.ROOT_FOLDER_ID, authorization.getRootId()).apply();
		Timber.i("Root folder set: %d", authorization.getRootId());
	}

	public static Integer getRootFolderId() {
		return sharedPrefs().getInt(Constants.Preference.ROOT_FOLDER_ID, 0);
	}

	public static void setDirectoryLayoutMode(Integer listMode){
		if (listMode == null){
			listMode = HomeActivity.LIST_LAYOUT;
		}
		sharedPrefs().edit().putInt(Constants.Preference.DIRECTORY_LAYOUT_MODE, listMode).apply();
	}

	public static Integer getDirectoryLayoutMode(){
		return sharedPrefs().getInt(Constants.Preference.DIRECTORY_LAYOUT_MODE, HomeActivity.LIST_LAYOUT);
	}


	public static void clear(){
		sharedPrefs().getAll().clear();
	}
}