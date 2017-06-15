package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectChangeSet;
import io.realm.RealmObjectChangeListener;
import timber.log.Timber;

public class FileViewActivity extends TelmediqActivity {
	@BindView(R.id.fileViewActivity_fileView)
	ImageView fileView;
	@BindView(R.id.back_arrow)
	MaterialIconView backArrow;
	@BindView(R.id.fileName)
	TextView fileName;
	@BindView(R.id.file_options)
	MaterialIconView fileOptions;
	@BindView(R.id.progressBar)
	ProgressBar progressBar;

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
		fileName.setText(file.getName());
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

	private void showProgressBar(boolean show) {
		if (progressBar == null) {
			return;
		}

		progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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

	//<editor-fold desc="Listeners">
	@OnClick(R.id.file_options)
	void onFileOptionClicked(View view) {
		BottomSheetFileDetailsFragment.newInstance(file.getId()).show(getSupportFragmentManager(), BottomSheetFileDetailsFragment.class.getSimpleName());
	}

	@OnClick(R.id.back_arrow)
	void onBackArrowClicked(View view) {
		finish();
	}

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
