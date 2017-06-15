package com.telmediq.docstorage.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.EditText;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqApplication;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Matt Casiro on 2017-06-05.
 */

public class BottomSheetAddContentFragment extends BottomSheetDialogFragment {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.addFolder)
	MaterialIconView addFolder;
	@BindView(R.id.addFromFile)
	MaterialIconView addFromFile;
	@BindView(R.id.addFromCamera)
	MaterialIconView addFromCamera;
	//</editor-fold>

	private Folder folder;
	private TelmediqApplication app;
	private static final int REQUEST_FILE_GET = 1;
	private static final int REQUEST_FILE_FROM_CAMERA = 2;

	public static BottomSheetAddContentFragment newInstance(Integer folderId) {
		BottomSheetAddContentFragment messagesFragment = new BottomSheetAddContentFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(Constants.Extras.FOLDER_ID, folderId);
		messagesFragment.setArguments(arguments);

		return messagesFragment;
	}

	@Override
	public void setupDialog(Dialog dialog, int style) {
		View contentView = View.inflate(getContext(), R.layout.content_main_details, null);
		dialog.setContentView(contentView);
		ButterKnife.bind(this, contentView);
		app = (TelmediqApplication) getActivity().getApplication();
		setupBehavior(contentView);

		// Get active folder
		Realm realm = Realm.getDefaultInstance();
		Integer folderId = getArguments().getInt(Constants.Extras.FOLDER_ID);
		folder = Folder.getFolder(realm, folderId);

		//setupView();
	}

	//<editor-fold desc="Bottom Sheet Behavior">
	private void setupBehavior(View contentView) {
		CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
		CoordinatorLayout.Behavior behavior = params.getBehavior();

		if (behavior != null && behavior instanceof BottomSheetBehavior) {
			((BottomSheetBehavior) behavior).setBottomSheetCallback(bottomSheetCallback);
		}
	}

	BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
		@Override
		public void onStateChanged(@NonNull View bottomSheet, int newState) {
			switch (newState) {
				case BottomSheetBehavior.STATE_HIDDEN:
					dismiss();
					break;
			}
		}

		@Override
		public void onSlide(@NonNull View bottomSheet, float slideOffset) {

		}
	};
	//</editor-fold>

	private void setupView() {
		throw new UnsupportedOperationException("setupView() has no content, is this needed?");
	}

	//<editor-fold desc="Listeners">
	@OnClick({R.id.addFolder, R.id.addFromFile, R.id.addFromCamera})
	public void onOptionClicked(View view) {
		switch (view.getId()) {
			case R.id.addFolder:
				createFolder();
				break;
			case R.id.addFromFile:
				Timber.d("addFromFile");
				addFromFile();
				break;
			case R.id.addFromCamera:
				Timber.d("addFromCamera");
				break;
		}
		Timber.d("Clicked id# %s", view.getId());
	}

	public void onClick(View view) {
		throw new UnsupportedOperationException("setupView() has no content, is this needed?");
	}
	//</editor-fold>

	//<editor-fold desc="Button Actions">
	private void createFolder() {
		final EditText folderName = new EditText(getContext());

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Call<Folder> call = app.getTelmediqService().addFolder(folder.getId(), folderName.getText().toString());
				call.enqueue(addFolderCallback);
			}
		};

		new AlertDialog.Builder(getContext())
				.setTitle(("New Folder"))
				.setView(folderName)
				.setPositiveButton("OK", listener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	private void addFromFile () {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*")
				.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_FILE_GET);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent response) {
		if (requestCode == REQUEST_FILE_GET && resultCode == RESULT_OK) {
			Timber.i("Got FILE_GET response");
			Timber.d(response.getDataString());
			File file = getFile(response.getData());
			Call<File> call = app.getTelmediqService().addFile(
					file.getFolder(),
					file.getName(),
					file.getFolder(),
					file,
					file.getOwner()
			);
			call.enqueue(addFileCallback);
			Timber.d("temp");
		}

		if (requestCode == REQUEST_FILE_FROM_CAMERA && resultCode == RESULT_OK) {

		}
	}
	//</editor-fold>

	private File getFile(Uri uri) {
		File file = new File();
		Cursor cursor = getActivity().getContentResolver()
				.query(uri, null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			file.setName(cursor.getString( cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
		}
		file.setFolder(folder.getId());
		file.setFile(uri.getPath());
		file.setOwner(folder.getOwner());

		return file;
	}
	//<editor-fold desc="Network Callbacks">
	Callback<Folder> addFolderCallback = new Callback<Folder>() {
		@Override
		public void onResponse(Call<Folder> call, final Response<Folder> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}

			Realm realm = Realm.getDefaultInstance();
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealm(response.body());
				}
			});
			dismiss();
		}

		@Override
		public void onFailure(Call<Folder> call, Throwable t) {

		}
	};

	Callback<File> addFileCallback = new Callback<File>() {
		@Override
		public void onResponse(Call<File> call, final Response<File> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}

			Realm realm = Realm.getDefaultInstance();
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealm(response.body());
				}
			});
			dismiss();
		}

		@Override
		public void onFailure(Call<File> call, Throwable t) {
			Timber.e(t.getMessage());
		}
	};
	//</editor-fold>
}
