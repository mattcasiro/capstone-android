package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.adapter.DirectoryAdapter;
import com.telmediq.docstorage.fragment.BottomSheetAddContentFragment;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;
import com.telmediq.docstorage.fragment.BottomSheetFolderDetailsFragment;
import com.telmediq.docstorage.helper.AppValues;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.DirectoryHolder;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;
import com.telmediq.docstorage.views.EmptyRecyclerView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

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
	public static final int GRID_LAYOUT = 0;
	public static final int LIST_LAYOUT = 1;

	//<editor-fold desc="View Initialization">
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.homeActivity_recyclerView)
	EmptyRecyclerView recyclerView;
	@BindView(R.id.listItem_empty)
	View emptyView;
	//</editor-fold>

	DirectoryAdapter adapter;
	Integer layoutMode = HomeActivity.LIST_LAYOUT;

	RealmResults<Folder> folders;
	RealmResults<File> files;

	@Nullable
	Folder parentFolder;
	int parentFolderId = -1;

	private String searchText = "";

	//<editor-fold desc="Lifecycle">
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

		layoutMode = AppValues.getDirectoryLayoutMode();
		fetchFilesAndFolders();
		getFilesAndFolders();
		setupToolbar();
		setupViews();
	}

	@Override
	protected void onPause() {
		setupRealmListeners(false);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupRealmListeners(true);
		layoutMode = AppValues.getDirectoryLayoutMode();
		setupViews();
	}
	//</editor-fold>

	//<editor-fold desc="View Setup">
	private void setupToolbar() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() == null) {
			return;
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(parentFolderId != AppValues.getRootFolderId());

		if (parentFolder != null) {
			if (parentFolder.getParent() == null) {
				getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
			} else {
				getSupportActionBar().setTitle(parentFolder.getName());
			}
		}
	}

	private void setupViews() {
		recyclerView.setEmptyView(emptyView);
		setupRecyclerView();
	}

	private void setupRecyclerView() {
		List<DirectoryHolder> directoryHolders = DirectoryHolder.generateDirectoryHolder(folders, files);

		recyclerView.setLayoutManager(getLayoutManager());

		if (recyclerView.getAdapter() == null || adapter.getLayoutMode() != layoutMode) {
			adapter = new DirectoryAdapter(directoryHolders, directoryListener, layoutMode);
			recyclerView.setAdapter(adapter);
		} else {
			adapter.updateData(directoryHolders);
		}

	}

	private RecyclerView.LayoutManager getLayoutManager() {

		LinearLayoutManager layoutManager;

		switch (layoutMode) {
			case GRID_LAYOUT:

				GridLayoutManager glayoutManager = new GridLayoutManager(this, 2);
				glayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
					@Override
					public int getSpanSize(int position) {
						switch (adapter.getItemViewType(position)) {
							case DirectoryHolder.HEADER:
								return 2;
							case DirectoryHolder.FILE:
							case DirectoryHolder.FOLDER:
							default:
								return 1;
						}
					}
				});
				layoutManager = glayoutManager;
				break;
			case LIST_LAYOUT:
			default:
				layoutManager = new LinearLayoutManager(this);
				break;
		}

		return layoutManager;
	}

	//</editor-fold>

	@DebugLog
	private void getFilesAndFolders() {
		if (searchText.isEmpty()) {
			folders = Folder.getFoldersByParent(realm, parentFolderId);
			files = File.getFilesByFolder(realm, String.valueOf(parentFolderId));
		} else {
			folders = Folder.getFolderByParent(realm, parentFolderId, searchText);
			files = File.getFilesByFolder(realm, String.valueOf(parentFolderId), searchText);
		}

		setupRealmListeners(true);
	}

	@DebugLog
	private void fetchFilesAndFolders() {
		Call<List<Folder>> userFolderCall = getTelmediqService().getFolders();
		userFolderCall.enqueue(userFolderCallback);

		Call<List<File>> userFileCall = getTelmediqService().getFiles();
		userFileCall.enqueue(userFileCallback);
	}

	private void logout() {
		AppValues.clear();

		Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
		startActivity(intent);

		finish();
	}

	private void toggleLayoutMode() {
		layoutMode = (layoutMode == LIST_LAYOUT) ? GRID_LAYOUT : LIST_LAYOUT;
		AppValues.setDirectoryLayoutMode(layoutMode);
		setupViews();
	}

	private void setLayoutModeIcon(MenuItem item) {
		Drawable icon = MaterialDrawableBuilder.with(this) // provide a context
				.setIcon(layoutMode == LIST_LAYOUT ? MaterialDrawableBuilder.IconValue.VIEW_MODULE : MaterialDrawableBuilder.IconValue.VIEW_LIST) // provide an icon
				.setColor(Color.WHITE) // set the icon color
				.setToActionbarSize() // set the icon size
				.build();
		item.setIcon(icon);
	}

	//<editor-fold desc="Option Menu">
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MaterialMenuInflater.with(this).setDefaultColorResource(android.R.color.white).inflate(R.menu.main, menu);
		((SearchView) menu.findItem(R.id.action_search).getActionView()).setOnQueryTextListener(searchListener);

		// set layoutMode icon to correct initial state
		setLayoutModeIcon(menu.findItem(R.id.action_gridview));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_gridview:
				toggleLayoutMode();
				setLayoutModeIcon(item);
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
		BottomSheetAddContentFragment.newInstance(getIntent().getIntExtra(Constants.Extras.FOLDER_ID, -1)).show(getSupportFragmentManager(), BottomSheetAddContentFragment.class.getSimpleName());
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

	SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			if (adapter != null) {
				searchText = newText;
				getFilesAndFolders();
				setupRecyclerView();
			}
			return true;
		}
	};

	private void setupRealmListeners(boolean enable) {
		if (folders != null && folders.isManaged()) {
			folders.removeAllChangeListeners();
			if (enable) {
				folders.addChangeListener(realmChangeListener);
			}
		}

		if (files != null && files.isManaged()) {
			files.removeAllChangeListeners();
			if (enable) {
				files.addChangeListener(realmChangeListener);
			}
		}
	}

	RealmChangeListener realmChangeListener = new RealmChangeListener() {
		@Override
		public void onChange(Object element) {
			setupRecyclerView();
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