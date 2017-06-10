package com.telmediq.docstorage.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.helper.Constants;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

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
		setupBehavior(contentView);

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
		throw new UnsupportedOperationException("setupView() has no content, is this needed?");
	}

	//<editor-fold desc="Listeners">
	@OnClick({R.id.addFolder, R.id.addFromFile, R.id.addFromCamera})
	public void onOptionClicked(View view) {
		switch (view.getId()) {
			case R.id.addFolder:

				break;
			case R.id.addFromFile:

				break;
			case R.id.addFromCamera:

				break;
		}
		Timber.d("Clicked id# %s", view.getId());
	}
	//</editor-fold>
}
