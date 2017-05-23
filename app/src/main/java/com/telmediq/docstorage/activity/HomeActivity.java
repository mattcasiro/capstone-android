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

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.adapter.DirectoryAdapter;
import com.telmediq.docstorage.adapter.FileAdapter;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.DirectoryHolder;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;

import java.util.List;

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
	DirectoryAdapter adapter; // TODO change to DirectoryAdapter

	RealmResults<Folder> folders;
	RealmResults<File> files;

	Folder parentFolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		parentFolder = Folder.getRootFolder(realm);

		getFolderList();
		getFileList();
		setupToolbar();
		setupViews();
	}

	private void setupToolbar() {
		setSupportActionBar(toolbar);
	}

	private void setupViews() {
		setupRecyclerView();
	}

	private void setupRecyclerView(){
		List<DirectoryHolder> directoryHolders = DirectoryHolder.generateDirectoryHolder(folders, files);
		recyclerView.setHasFixedSize(true);

		if(recyclerView.getAdapter() == null){
			adapter = new DirectoryAdapter(directoryHolders, directoryListener);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setAdapter(adapter);
		} else {
			adapter.updateData(directoryHolders);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void getFolderList() {
		Timber.d("Fetch folder list");

		// Handle case when no folders are in realm yet
		if (parentFolder == null) {
			folders = Folder.getEmptyFolderList(realm);
		} else {
			folders = Folder.getFoldersByParent(realm, parentFolder.getId());
		}
		folders.addChangeListener(realmChangeListener);

		Call<List<Folder>> userFolderCall = getTelmediqService().getFolders();
		userFolderCall.enqueue(userFolderCallback);
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
		Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
		getFolderList();
	}

	DirectoryAdapter.Listener directoryListener = new DirectoryAdapter.Listener() {

		@Override
		public void onFolderClicked(Integer folderId) {
			Timber.d("clicked folder " + folderId.toString());
		}

		@Override
		public void onFileClicked(Integer fileId) {
			Intent intent = new Intent(HomeActivity.this, FileViewActivity.class);
			intent.putExtra(Constants.Extras.FILE_ID, fileId);
			startActivity(intent);
		}

		@Override
		public void onFileOptionClicked(Integer fileId){
			Timber.i("File option clicked");
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

	Callback<List<Folder>> userFolderCallback = new Callback<List<Folder>>() {
		@Override
		public void onResponse(Call<List<Folder>> call, final Response<List<Folder>> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm realm){
					realm.copyToRealmOrUpdate(response.body());
				}
			}, new Realm.Transaction.OnSuccess(){
				@Override
				public void onSuccess(){
					Timber.d("Saved Folder to DB");
				}
			});
		}

		@Override
		public void onFailure(Call<List<Folder>> call, Throwable t) {

		}
	};
	//</editor-fold>
}