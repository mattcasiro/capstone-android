package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;

import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.AppValues;

public class MainActivity extends TelmediqActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent;

		if (AppValues.hasAuthorization()) {
			intent = new Intent(this, HomeActivity.class);
		} else {
			intent = new Intent(this, LoginActivity.class);
		}

		startActivity(intent);
		finish();
	}
}
