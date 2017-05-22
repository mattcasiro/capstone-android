package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.adapter.FileAdapter;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class HomeActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.homeActivity_recyclerView)
	RecyclerView recyclerView;
	//</editor-fold>

	RecyclerView.LayoutManager layoutManager;
	FileAdapter adapter;

	RealmResults<File> files;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		getFileList();
		setupToolbar();
		setupViews();
	}

	private void setupToolbar() {
		setSupportActionBar(toolbar);
	}

	private void setupViews() {
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);

		recyclerView.setLayoutManager(layoutManager);

		adapter = new FileAdapter(files, fileListener);
		recyclerView.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void getFileList() {
		Timber.d("Fetch file list");
		files = File.getFiles(realm);
		files.addChangeListener(realmChangeListener);

		Call<List<File>> userFileCall = getTelmediqService().getFiles();
		userFileCall.enqueue(userFileCallback);
	}

	//<editor-fold desc="Menu">
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_settings:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	//</editor-fold>

	//<editor-fold desc="Listeners">
	@OnClick(R.id.fab)
	public void onFabClicked(View view) {
		//Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
		Intent intent = new Intent(HomeActivity.this, ProfileViewActivity.class);
		//intent.putExtra(Constants.Extras.USER_ID, userID);
		startActivity(intent);
	}

	FileAdapter.Listener fileListener = new FileAdapter.Listener() {

		@Override
		public void onItemClicked(int fileId) {
			//Toast.makeText(getApplicationContext(), String.format("Selected: %s", fileId), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(HomeActivity.this, FileViewActivity.class);
			intent.putExtra(Constants.Extras.FILE_ID, fileId);
			startActivity(intent);
		}

		@Override
		public void onItemOptionSelected(int fileId) {
			BottomSheetFileDetailsFragment.newInstance(fileId).show(getSupportFragmentManager(), BottomSheetFileDetailsFragment.class.getSimpleName());
		}
	};

	RealmChangeListener realmChangeListener = new RealmChangeListener() {
		@Override
		public void onChange(Object element) {
			setupViews();
		}
	};
	//</editor-fold>

	//<editor-fold desc="Network Callbacks">
	Callback<List<File>> userFileCallback = new Callback<List<File>>() {
		@Override
		public void onResponse(Call<List<File>> call, final Response<List<File>> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(response.body());
				}
			}, new Realm.Transaction.OnSuccess() {
				@Override
				public void onSuccess() {
					Timber.d("WOWOW (saved to db)");
				}
			});
		}

		@Override
		public void onFailure(Call<List<File>> call, Throwable t) {

		}
	};
	//</editor-fold>
}