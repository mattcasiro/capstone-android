package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;

import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.AppValues;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.model.Folder;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends TelmediqActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent;

		if (AppValues.hasAuthorization()) {
			intent = new Intent(this, HomeActivity.class);
			intent.putExtra(Constants.Extras.FOLDER_ID, AppValues.getRootFolderId());
		} else {
			intent = new Intent(this, LoginActivity.class);
		}

		startActivity(intent);
		finish();
	}
}
