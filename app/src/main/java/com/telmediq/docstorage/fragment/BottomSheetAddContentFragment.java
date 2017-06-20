package com.telmediq.docstorage.fragment;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.RSInvalidStateException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.EditText;
import android.*;

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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
	//<editor-fold desc="View Initialization">
	@BindView(R.id.addFolder)
	MaterialIconView addFolder;
	@BindView(R.id.addFromFile)
	MaterialIconView addFromFile;
	@BindView(R.id.addFromCamera)
	MaterialIconView addFromCamera;
	//</editor-fold>

	private String mCurrentPhotoPath;
	private Folder folder;
	private TelmediqApplication app;
	private ImagePicker imagePicker;
	private static final int REQUEST_FILE_GET = 1;
	private static final int REQUEST_IMAGE_CAPTURE = 2;
	private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 3;
	private static final int PERMISSION_REQUEST_CAMERA = 4;

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

	private void addFromFile() {
		// Verify app has permission to access external storage
		int permissionCheck = ContextCompat.checkSelfPermission(
				getActivity(),
				Manifest.permission.READ_EXTERNAL_STORAGE);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			// Request permissions, and handle response in onRequestPermissionsResult
			requestPermissions(
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					PERMISSION_REQUEST_EXTERNAL_STORAGE);
		} else {
			/* This code was not very device agnostic (issues on Samsung S7)
			Intent addFromFileIntent = new Intent(Intent.ACTION_GET_CONTENT)
					.setType("image/*")
					.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(addFromFileIntent, REQUEST_FILE_GET);
			*/

			// Using Multipicker
			imagePicker = new ImagePicker(this);
			imagePicker.setImagePickerCallback(imagePickerCallback);
			imagePicker.shouldGenerateMetadata(false);
			imagePicker.shouldGenerateThumbnails(false);
			imagePicker.pickImage();
			Timber.i("WE PICKED AN IMAGE");
		}
	}

	private void addFromCamera() {
		Timber.i("Adding file from Camera");
		Timber.i("NOTE: External Files Dir: %s", getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
		// Verify app has permission to access camera
		int permissionCheck = ContextCompat.checkSelfPermission(
				getActivity(),
				Manifest.permission.CAMERA);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			Timber.i("Making permission request for Camera");
			// Request permissions, and handle response in onRequestPermissionsResult
			requestPermissions(
					new String[]{Manifest.permission.CAMERA},
					PERMISSION_REQUEST_CAMERA);
		} else {
			Timber.i("Camera permission OK");
			Intent addFromCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			// Ensure a valid app exists to handle the intent before starting it
			if (addFromCameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
				java.io.File imageFile = null;
				try {
					imageFile = createImageFile();
					Uri imageURI = FileProvider.getUriForFile(
							getActivity(),
							"com.telmediq.docstorage.fileprovider",
							imageFile);

					addFromCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
					startActivityForResult(addFromCameraIntent, REQUEST_IMAGE_CAPTURE);
				} catch (java.io.IOException ex) {
					Timber.e("Device was unable to create a temporary image file");
					//TODO: Toast user unable to create file
					dismiss();
				}
			} else {
				//TODO: Toast user that no camera app exists
				dismiss();
			}
		}
	}
	//</editor-fold>

	private void uploadFile(String fileUri, Integer requestCode) {
		// Create file part
		java.io.File file;
		switch (requestCode) {
			case REQUEST_FILE_GET:
				file = new java.io.File(fileUri);
				break;
			case REQUEST_IMAGE_CAPTURE:
				file = new java.io.File(mCurrentPhotoPath);
				break;
			default:
				throw new IllegalStateException("Unknown request code.");
		}

		RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
		MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

		// Create name part
		Timber.d("FILENAME: %s", file.getName());
		RequestBody fileName = RequestBody.create(MultipartBody.FORM, file.getName());

		Call<File> call = app.getTelmediqService().addFile(folder.getId(), body, fileName);
		call.enqueue(addFileCallback);
	}

	private java.io.File createImageFile() throws java.io.IOException {
		// Create collision-resistant image name
		String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imagePrefix = "JPEG_" + time + "_";
		java.io.File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		java.io.File image = java.io.File.createTempFile(
				imagePrefix,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	//<editor-fold desc="On Results">
	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       String permissions[], int[] grantResults) {
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent response) {
		Timber.i("WE ARE IN ACTIVITY RESULT");
		if (resultCode == RESULT_OK) {
			/* Old image selection code
			if (requestCode == REQUEST_FILE_GET || requestCode == REQUEST_IMAGE_CAPTURE) {
				uploadFile(response.getData(), requestCode);
			}
			*/
			if (requestCode == Picker.PICK_IMAGE_DEVICE) {
				if (imagePicker == null) {
					imagePicker = new ImagePicker(getActivity());
				}
				imagePicker.submit(response);
			}
		} else {
			Timber.e("onActivityResult: Activity Result Code not OK");
		}
		dismiss();
	}
	//</editor-fold>

	//<editor-fold desc="Content Callbacks">
	ImagePickerCallback imagePickerCallback = new ImagePickerCallback() {
		@Override
		public void onImagesChosen(List<ChosenImage> list) {
			Timber.i("test");
			uploadFile(list.get(0).getOriginalPath(), REQUEST_FILE_GET);
		}

		@Override
		public void onError(String s) {

		}
	};
	//</editor-fold>

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

	//<editor-fold desc="Path Resolver">
	// Source: https://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework/20559175#20559175

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri     The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context       The context.
	 * @param uri           The Uri to query.
	 * @param selection     (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	                                   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	//</editor-fold>
}
