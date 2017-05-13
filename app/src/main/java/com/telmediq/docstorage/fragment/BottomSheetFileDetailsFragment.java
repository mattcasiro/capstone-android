package com.telmediq.docstorage.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.model.File;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
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
	//</editor-fold>

	private File file;
	private boolean isUserInteraction = true; // boolean to make sure programmatic changes don't trigger listeners

	public static BottomSheetFileDetailsFragment newInstance(int fileId) {
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

		setupBehavior(contentView);

		if (!getFile()) {
			Toast.makeText(getContext(), R.string.unable_to_get_file_details, Toast.LENGTH_LONG).show();
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

		String fileId = getArguments().getString(Constants.Extras.FILE_ID);
		if (fileId == null) {
			return false;
		}

		// ToDo: get file from database using id
		//file = new File(UUID.randomUUID().toString(), "AnImage", new Date(), new Date(), 2048);
		return true;
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

				break;
			case R.id.removeListItem:

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
}
