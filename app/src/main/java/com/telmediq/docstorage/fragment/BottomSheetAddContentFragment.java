package com.telmediq.docstorage.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;

import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqApplication;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Matt Casiro on 2017-06-05.
 */

public class BottomSheetAddContentFragment extends BottomSheetDialogFragment {
	private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 3;
	private static final int PERMISSION_REQUEST_CAMERA = 4;

	//<editor-fold desc="View Initialization">
	@BindView(R.id.addFolder)
	MaterialIconView addFolder;
	@BindView(R.id.addFromFile)
	MaterialIconView addFromFile;
	@BindView(R.id.addFromCamera)
	MaterialIconView addFromCamera;
	//</editor-fold>

	private String currentPhotoPath;
	private Folder folder;
	private TelmediqApplication app;
	private ImagePicker imagePicker;
	private CameraImagePicker cameraImagePicker;

	public static BottomSheetAddContentFragment newInstance(Integer folderId) {
		BottomSheetAddContentFragment messagesFragment = new BottomSheetAddContentFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(Constants.Extras.FOLDER_ID, folderId);
		messagesFragment.setArguments(arguments);

		return messagesFragment;
	}

	//<editor-fold desc="Lifecycle">
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
	}

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

	@DebugLog
	private void addFromFile() {
		// Verify app has permission to access external storage
		if (!hasFilePermissions()) {
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
			return;
		}

		imagePicker = new ImagePicker(this);
		imagePicker.setImagePickerCallback(imagePickerCallback);
		imagePicker.shouldGenerateMetadata(false);
		imagePicker.shouldGenerateThumbnails(false);
		imagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
		imagePicker.pickImage();
	}

	@DebugLog
	private void addFromCamera() {
		if (!hasCameraPermission()) {
			requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
			return;
		}

		cameraImagePicker = new CameraImagePicker(this);
		cameraImagePicker.setImagePickerCallback(imagePickerCallback);
		cameraImagePicker.shouldGenerateMetadata(false);
		cameraImagePicker.shouldGenerateThumbnails(false);
		cameraImagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
		currentPhotoPath = cameraImagePicker.pickImage();
	}

	private void uploadFile(String fileUri) {
		// Create file part
		java.io.File file = new java.io.File(fileUri);

		RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
		MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

		// Create name part
		RequestBody fileName = RequestBody.create(MultipartBody.FORM, file.getName());

		Call<File> call = app.getTelmediqService().addFile(folder.getId(), body, fileName);
		call.enqueue(addFileCallback);
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
				addFromCamera();
				break;
		}
		Timber.d("Clicked id# %s", view.getId());
	}

	ImagePickerCallback imagePickerCallback = new ImagePickerCallback() {
		@Override
		public void onImagesChosen(List<ChosenImage> list) {
			Timber.i("test");
			uploadFile(list.get(0).getOriginalPath());
		}

		@Override
		public void onError(String s) {

		}
	};
	//</editor-fold>

	//<editor-fold desc="Permissions">
	private boolean hasCameraPermission() {
		return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	private boolean hasFilePermissions() {
		return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_EXTERNAL_STORAGE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					addFromFile();
				} else {
					//TODO: Toast error to user
					Timber.e("Don't have permission to access external storage");
					dismiss();
				}
				break;
			}
			case PERMISSION_REQUEST_CAMERA: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					addFromCamera();
				} else {
					//TODO: Toast error to user
					Timber.e("Don't have permission to access external storage");
					dismiss();
				}
			}
		}
	}
	//</editor-fold>

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent response) {
		Timber.i("WE ARE IN ACTIVITY RESULT");
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case Picker.PICK_IMAGE_DEVICE:
					if (imagePicker == null) {
						imagePicker = new ImagePicker(BottomSheetAddContentFragment.this);
						imagePicker.setImagePickerCallback(imagePickerCallback);
					}
					imagePicker.submit(response);
					break;
				case Picker.PICK_IMAGE_CAMERA:
					if (cameraImagePicker == null) {
						cameraImagePicker = new CameraImagePicker(BottomSheetAddContentFragment.this, currentPhotoPath);
						cameraImagePicker.setImagePickerCallback(imagePickerCallback);
					}
					cameraImagePicker.submit(response);
					break;
			}
		} else {
			Timber.e("onActivityResult: Activity Result Code not OK");
		}
		dismiss();
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
