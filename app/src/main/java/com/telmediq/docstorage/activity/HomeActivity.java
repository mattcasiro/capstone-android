package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.adapter.DirectoryAdapter;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;
import com.telmediq.docstorage.fragment.BottomSheetFolderDetailsFragment;
import com.telmediq.docstorage.helper.AppValues;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.DirectoryHolder;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;
import com.telmediq.docstorage.views.EmptyRecyclerView;

import net.steamcrafted.materialiconlib.MaterialIconView;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import android.support.v7.widget.SearchView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends TelmediqActivity {
	private static final int FILE_DETAIL_REQUEST_CODE = 2432;

	//<editor-fold desc="View Initialization">
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.homeActivity_recyclerView)
	EmptyRecyclerView recyclerView;
	@BindView(R.id.listItem_empty)
	View emptyView;
	//</editor-fold>

	SearchView searchView;
	MenuItem searchItem;

	DirectoryAdapter adapter;

	RealmResults<Folder> folders;
	RealmResults<File> files;

	@Nullable
	String searchString;

	@Nullable
	Folder parentFolder;

	int parentFolderId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		parentFolderId = getIntent().getIntExtra(Constants.Extras.FOLDER_ID, -1);
		parentFolder = Folder.getFolder(realm, parentFolderId);
		if (parentFolderId == -1) {
			Toast.makeText(this, "Not passed a folder ID", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		getFolderList();
		getFileList();
		setupToolbar();
		setupViews();
	}

	private void setupToolbar() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() == null) {
			return;
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(parentFolderId != AppValues.getRootFolderId());

		if (parentFolder != null) {
			getSupportActionBar().setTitle(parentFolder.getName());
		}
	}

	private void setupViews() {
		recyclerView.setEmptyView(emptyView);
		setupRecyclerView();
	}

	private void setupRecyclerView() {
		List<DirectoryHolder> directoryHolders = DirectoryHolder.generateDirectoryHolder(folders, files);

		if (recyclerView.getAdapter() == null) {
			adapter = new DirectoryAdapter(directoryHolders, directoryListener);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setAdapter(adapter);
		} else {
			adapter.updateData(directoryHolders);
		}

		searchString = getIntent().getStringExtra(Constants.Extras.SEARCH_STRING);
		if( searchString != null) {
			search(searchString);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("\""+searchString+"\"");
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@DebugLog
	private void getFolderList() {
		folders = Folder.getFoldersByParent(realm, parentFolderId);
		folders.addChangeListener(realmChangeListener);

		Call<List<Folder>> userFolderCall = getTelmediqService().getFolders();
		userFolderCall.enqueue(userFolderCallback);
	}

	@DebugLog
	public void getFileList() {
		files = File.getFilesByFolder(realm, String.valueOf(parentFolderId));
		files.addChangeListener(realmChangeListener);

		Call<List<File>> userFileCall = getTelmediqService().getFiles();
		userFileCall.enqueue(userFileCallback);
	}

	private void logout(){
		AppValues.clear();

		Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
		startActivity(intent);

		finish();
	}

	private void setupSearch(Menu menu){
		searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(searchListener);
	}

	private void search(String searchString){
		//iterate through list of files and folders, and copy those that match into new lists to update view contents with
		if(!(searchString == null || searchString.equals("") )) {
			RealmResults<File> searchFiles = File.getFilesByName(realm, String.valueOf(parentFolderId), searchString);
			RealmResults<Folder> searchFolders = Folder.getFoldersByName(realm, parentFolderId, searchString);

			//pass in new list of files and folders which
			List<DirectoryHolder> directoryHolders = DirectoryHolder.generateDirectoryHolder(searchFolders, searchFiles);
			adapter.updateData(directoryHolders);
		} else {
			List<DirectoryHolder> directoryHolders = DirectoryHolder.generateDirectoryHolder(folders, files);
			adapter.updateData(directoryHolders);
		}
	}

	//<editor-fold desc="Menu">
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MaterialMenuInflater.with(this).setDefaultColorResource(android.R.color.white).inflate(R.menu.main, menu);
		setupSearch(menu);

		searchString = getIntent().getStringExtra(Constants.Extras.SEARCH_STRING);
		if( searchString != null) {
			menu.findItem(R.id.action_search).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_gridview:
				break;
			case R.id.action_search:
				break;
			case R.id.action_profile:
				Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
				startActivity(intent);
				break;
			case R.id.action_settings:
				break;
			case R.id.action_logout:
				logout();
				break;
			case android.R.id.home:
				finish();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	//</editor-fold>

	//<editor-fold desc="Listeners">
	@OnClick(R.id.fab)
	public void onFabClicked(View view) {
		Snackbar.make(view, "Make me do a thing", Snackbar.LENGTH_LONG).show();
	}

	DirectoryAdapter.Listener directoryListener = new DirectoryAdapter.Listener() {
		@Override
		@DebugLog
		public void onFolderClicked(Integer folderId) {
			Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
			intent.putExtra(Constants.Extras.FOLDER_ID, folderId);
			startActivity(intent);
		}

		@Override
		@DebugLog
		public void onFileClicked(Integer fileId) {
			Intent intent = new Intent(HomeActivity.this, FileViewActivity.class);
			intent.putExtra(Constants.Extras.FILE_ID, fileId);
			startActivityForResult(intent, FILE_DETAIL_REQUEST_CODE);
		}

		@Override
		@DebugLog
		public void onFileOptionClicked(Integer fileId) {
			BottomSheetFileDetailsFragment.newInstance(fileId).show(getSupportFragmentManager(), BottomSheetFileDetailsFragment.class.getSimpleName());
		}

		@Override
		@DebugLog
		public void onFolderOptionClicked(Integer folderId) {
			BottomSheetFolderDetailsFragment.newInstance(folderId).show(getSupportFragmentManager(), BottomSheetFolderDetailsFragment.class.getSimpleName());
		}
	};

	RealmChangeListener realmChangeListener = new RealmChangeListener() {
		@Override
		public void onChange(Object element) {
			setupRecyclerView();
		}
	};

	SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String query) {
			Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
			intent.putExtra(Constants.Extras.FOLDER_ID, parentFolderId);
			intent.putExtra(Constants.Extras.SEARCH_STRING, query);
			startActivity(intent);
			searchItem.collapseActionView();
			return false;
		}
		@Override
		public boolean onQueryTextChange(String s) {
			// UserFeedback.show( "SearchOnQueryTextChanged: " + s);
			return false;
		}
	};

	//</editor-fold>

	//<editor-fold desc="Activity Results">
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case FILE_DETAIL_REQUEST_CODE:
					handleFileDetailResults(data);
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleFileDetailResults(Intent data) {
		switch (data.getAction()) {
			case Constants.Actions.FILE_DELETED:
				Snackbar.make(recyclerView, R.string.delete_file_notification, Snackbar.LENGTH_SHORT).show();
				break;
		}
	}
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
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(response.body());
				}
			});
		}

		@Override
		public void onFailure(Call<List<Folder>> call, Throwable t) {

		}
	};
	//</editor-fold>
}