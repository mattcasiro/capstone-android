package com.telmediq.docstorage.helper;

import android.content.Context;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by MCMBP on 2017-05-03.
 */

public class Utils {
	public static String checkResponseForError(Response<?> response) {
		String error = null;
		if (response.raw().code() < 200 || response.raw().code() > 299) {
			try {
				error = response.errorBody().string();
				if (!error.startsWith("{")) {
					if (error.toLowerCase().contains("No address associated with hostname")) {
						error = "Not connected to the internet";
					}
				}
			} catch (IOException e) {
				error = response.raw().message();
			}
		}
		return error;
	}
}
