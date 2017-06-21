package com.telmediq.docstorage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqApplication;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.Folder;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by sean1 on 5/11/2017.
 */

public class BottomSheetFolderDetailsFragment extends BottomSheetDialogFragment {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.fileTypeImage)
	MaterialIconView fileTypeImage;
	@BindView(R.id.fileNameTextView)
	TextView fileNameTextView;
	@BindView(R.id.starSwitch)
	SwitchCompat starSwitch;
	@BindView(R.id.contentFileDetails_rootView)
	View rootView;
	//</editor-fold>

	private Folder folder;
	private TelmediqApplication app;
	private boolean isUserInteraction = true; // boolean to make sure programmatic changes don't trigger listeners

	public static BottomSheetFolderDetailsFragment newInstance(Integer folderId) {
		BottomSheetFolderDetailsFragment messagesFragment = new BottomSheetFolderDetailsFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(Constants.Extras.FOLDER_ID, folderId);
		messagesFragment.setArguments(arguments);

		return messagesFragment;
	}

	@Override
	public void setupDialog(Dialog dialog, int style) {
		View contentView = View.inflate(getContext(), R.layout.content_file_details, null);
		dialog.setContentView(contentView);
		ButterKnife.bind(this, contentView);
		app = (TelmediqApplication) getActivity().getApplication();
		setupBehavior(contentView);

		if (!getFolder()) {
			Toast.makeText(getContext(), R.string.unable_to_get_folder_details, Toast.LENGTH_LONG).show();
			dismiss();
			return;
		}

		setupView();
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
		isUserInteraction = false;

		fileNameTextView.setText(folder.getName());
		starSwitch.setChecked(false); //ToDo: pull value from settings somewhere

		isUserInteraction = true;
	}

	private boolean getFolder() {
		if (getArguments() == null) {
			return false;
		}

		Integer folderId = getArguments().getInt(Constants.Extras.FOLDER_ID);

		Realm realm = Realm.getDefaultInstance();
		folder = Folder.getFolder(realm, folderId);

		return folder != null;
	}

	//<editor-fold desc="Listeners">
	@OnClick({R.id.fileInfo, R.id.addPeopleListItem, R.id.shareLinkListItem, R.id.moveListItem,
			R.id.starListItem, R.id.renameListItem, R.id.removeListItem})
	public void onOptionClicked(View view) {

		switch (view.getId()) {
			case R.id.fileInfo:

				break;
			case R.id.addPeopleListItem:

				break;
			case R.id.shareLinkListItem:

				break;
			case R.id.moveListItem:

				break;
			case R.id.starListItem:
				starSwitch.toggle();
				break;
			case R.id.renameListItem:
				Timber.d("renaming file");

				final EditText folderName = new EditText(getContext());

				DialogInterface.OnClickListener renameListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Call<Folder> call = app.getTelmediqService().renameFolder(folder.getId(), folderName.getText().toString());
						call.enqueue(renameFolderCallback);
					}
				};

				new AlertDialog.Builder(getContext())
						.setTitle(("Rename File"))
						.setView(folderName)
						.setPositiveButton("OK", renameListener)
						.setNegativeButton("Cancel", null)
						.create()
						.show();
				break;
			case R.id.removeListItem:
				Timber.d("removing folder");

				Utils.buildAlertDialog(
						view.getContext(),
						R.string.confirm_delete_title,
						R.string.confirm_delete_folder_message,
						R.drawable.ic_warning_black,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Call<Folder> call = app.getTelmediqService().deleteFolder(folder.getId());
								call.enqueue(userDeleteFolderCallback);
							}
						})
						.show();
				break;
		}

		Timber.d("Clicked id# %s", view.getId());
	}


	@OnCheckedChanged({R.id.starSwitch})
	public void onOptionSwitchToggled(CompoundButton button, boolean isChecked) {
		if (!isUserInteraction) {
			return;
		}

		switch (button.getId()) {
			case R.id.starSwitch:

				break;
		}

		Timber.d("Toggled id# %s to %s", button.getId(), isChecked);
	}
	//</editor-fold>

	//<editor-fold desc="Network Callbacks">
	Callback<Folder> userDeleteFolderCallback = new Callback<Folder>() {
		@Override
		public void onResponse(Call<Folder> call, Response<Folder> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}
			Realm realm = Realm.getDefaultInstance();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					folder.delete(realm);
					CoordinatorLayout rootLayout = (CoordinatorLayout) getActivity().findViewById(R.id.activityMain_coordinatorLayout);
					Snackbar.make(rootLayout, R.string.delete_folder_notification, Snackbar.LENGTH_LONG).show();
				}
			});
			dismiss();
		}

		@Override
		public void onFailure(Call<Folder> call, Throwable t) {
			Timber.d("failed to delete folder");
		}
	};

	Callback<Folder> renameFolderCallback = new Callback<Folder>() {
		@Override
		public void onResponse(Call<Folder> call, final Response<Folder> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}
			Realm realm = Realm.getDefaultInstance();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(response.body());
				}
			});
			dismiss();
		}

		@Override
		public void onFailure(Call<Folder> call, Throwable t) {
			Timber.d("failed to rename file");
		}
	};
	//</editor-fold>


}
