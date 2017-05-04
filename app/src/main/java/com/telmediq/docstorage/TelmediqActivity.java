package com.telmediq.docstorage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;

/**
 * Created by sean on 2017-05-03.
 */

public class TelmediqActivity extends AppCompatActivity {
	Realm realm;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TelmediqApplication application = (TelmediqApplication) getApplication();
		try {
			realm = Realm.getDefaultInstance();
		} catch (NullPointerException | IllegalStateException e) {
			application.setupRealm();
			realm = Realm.getDefaultInstance();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		realm.close();
	}
}
