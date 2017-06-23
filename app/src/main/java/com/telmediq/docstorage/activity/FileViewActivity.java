package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.UrlHelper;
import com.telmediq.docstorage.model.File;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.ObjectChangeSet;
import io.realm.RealmObjectChangeListener;
import timber.log.Timber;

public class FileViewActivity extends TelmediqActivity {
	@BindView(R.id.fileViewActivity_fileView)
	ImageView fileView;
	@BindView(R.id.progressBar)
	ProgressBar progressBar;
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private File file;

	//<editor-fold desc="Lifecycle">
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_file_view);
		ButterKnife.bind(this);

		if (!getFile()) {
			Toast.makeText(this, R.string.unable_to_get_file_details, Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		setupToolbar();
		setupView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupRealmListener(true);
	}

	@Override
	protected void onPause() {
		setupRealmListener(false);
		super.onPause();
	}

	//</editor-fold>
	private void setupView() {
		showProgressBar(true);
		Glide.with(this)
				.load(UrlHelper.getAuthenticatedUrl(file.getUrl()))
				.listener(new RequestListener<GlideUrl, GlideDrawable>() {
					@Override
					public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
						showProgressBar(false);
						return false;
					}

					@Override
					public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
						showProgressBar(false);
						return false;
					}
				})
				.into(fileView);
	}

	private void setupToolbar() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() == null) {
			return;
		}
		getSupportActionBar().setTitle(file.getName());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void showProgressBar(boolean show) {
		if (progressBar == null) {
			return;
		}

		progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	private boolean getFile() {
		int fileId = getIntent().getIntExtra(Constants.Extras.FILE_ID, -1);
		if (fileId == -1) {
			return false;
		}

		file = File.getFile(realm, String.valueOf(fileId));
		setupRealmListener(true);
		return file != null;
	}

	//<editor-fold desc="Menu">
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MaterialMenuInflater.with(this).setDefaultColorResource(android.R.color.white).inflate(R.menu.file_view_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.fileView_menu:
				BottomSheetFileDetailsFragment.newInstance(file.getId()).show(getSupportFragmentManager(), BottomSheetFileDetailsFragment.class.getSimpleName());
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	//</editor-fold>

	//<editor-fold desc="Listeners">
	private void setupRealmListener(boolean enable) {
		if (file != null && file.isManaged()) {
			file.removeAllChangeListeners();
			if (enable) {
				file.addChangeListener(realmChangeListener);
			}
		}
	}

	RealmObjectChangeListener<File> realmChangeListener = new RealmObjectChangeListener<File>() {
		@Override
		public void onChange(File file, ObjectChangeSet objectChangeSet) {
			Timber.d("file changed");
			if (objectChangeSet.isDeleted()) {
				Timber.d("File deleted");
				Intent intent = new Intent();
				intent.setAction(Constants.Actions.FILE_DELETED);
				setResult(RESULT_OK, intent);
				finish();
				return;
			}
			setupView();
		}
	};

	//</editor-fold>
}
