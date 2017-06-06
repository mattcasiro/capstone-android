package com.telmediq.docstorage.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;

import com.telmediq.docstorage.R;

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
			} catch (IOException e) {
				error = response.raw().message();
			}
		}
		return error;
	}


	public static AlertDialog buildAlertDialog(Context context, int title, int message, int icon, DialogInterface.OnClickListener listener){
		return new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(message)
				.setIcon(icon)
				.setPositiveButton(R.string.yes, listener)
				.setNegativeButton(R.string.no, null)
				.create();
	}
}
