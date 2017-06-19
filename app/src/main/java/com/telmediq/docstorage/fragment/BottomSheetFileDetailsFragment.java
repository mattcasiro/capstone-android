package com.telmediq.docstorage.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqApplication;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.File;

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

public class BottomSheetFileDetailsFragment extends BottomSheetDialogFragment {
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

	private File file;
	private TelmediqApplication app;
	private boolean isUserInteraction = true; // boolean to make sure programmatic changes don't trigger listeners

	public static BottomSheetFileDetailsFragment newInstance(Integer fileId) {
		BottomSheetFileDetailsFragment messagesFragment = new BottomSheetFileDetailsFragment();

		Bundle arguments = new Bundle();
		arguments.putInt(Constants.Extras.FILE_ID, fileId);
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

		if (!getFile()) {
			Toast.makeText(getContext(), R.string.unable_to_get_file_details, Toast.LENGTH_SHORT).show();
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

		fileNameTextView.setText(file.getName());
		starSwitch.setChecked(false); //ToDo: pull value from settings somewhere

		isUserInteraction = true;
	}

	private boolean getFile() {
		if (getArguments() == null) {
			return false;
		}

		Integer fileId = getArguments().getInt(Constants.Extras.FILE_ID);

		Realm realm = Realm.getDefaultInstance();
		file = File.getFile(realm, fileId.toString());

		return file != null;
	}

	//<editor-fold desc="Listeners">
	@OnClick({R.id.fileInfo, R.id.addPeopleListItem, R.id.shareLinkListItem, R.id.moveListItem,
			R.id.starListItem, R.id.renameListItem, R.id.removeListItem})
	public void onOptionClicked(View view) {

		switch (view.getId()) {
			case R.id.fileInfo:
				Toast.makeText(getContext(), R.string.implement_later, Toast.LENGTH_SHORT).show();
				break;
			case R.id.addPeopleListItem:
				Toast.makeText(getContext(), R.string.implement_later, Toast.LENGTH_SHORT).show();
				break;
			case R.id.shareLinkListItem:
				Toast.makeText(getContext(), R.string.implement_later, Toast.LENGTH_SHORT).show();
				break;
			case R.id.moveListItem:
				Toast.makeText(getContext(), R.string.implement_later, Toast.LENGTH_SHORT).show();
				break;
			case R.id.starListItem:
				starSwitch.toggle();
				break;
			case R.id.renameListItem:

				break;
			case R.id.removeListItem:
				Timber.d("removing file");

				Utils.buildAlertDialog(
						view.getContext(),
						R.string.confirm_delete_title,
						R.string.confirm_delete_file_message,
						R.drawable.ic_warning_black,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Call<File> call = app.getTelmediqService().deleteFile(file.getFolder(), file.getId());
								call.enqueue(userDeleteFileCallback);
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
				Toast.makeText(getContext(), R.string.implement_later, Toast.LENGTH_SHORT).show();
				break;
		}

		Timber.d("Toggled id# %s to %s", button.getId(), isChecked);
	}
	//</editor-fold>

	//<editor-fold desc="Network Callbacks">
	Callback<File> userDeleteFileCallback = new Callback<File>() {
		@Override
		public void onResponse(Call<File> call, Response<File> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}
			Realm realm = Realm.getDefaultInstance();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					file.delete(realm);

					// Need to account for case when we do not change activity after file deletion
					// occurs (eg. deleting a file from HomeActivity). Otherwise snackbar will be
					// shown onActivityResult. This could be completely wrong, but it works. Should
					// probably find a better way.
					if (getActivity().findViewById(R.id.activityMain_coordinatorLayout) != null) {
						CoordinatorLayout rootLayout = (CoordinatorLayout) getActivity().findViewById(R.id.activityMain_coordinatorLayout);
						Snackbar.make(rootLayout, R.string.delete_file_notification, Snackbar.LENGTH_LONG).show();
					}
				}
			});
			dismiss();
		}

		@Override
		public void onFailure(Call<File> call, Throwable t) {
			Timber.d("failed to delete file");
		}
	};
	//</editor-fold>


}