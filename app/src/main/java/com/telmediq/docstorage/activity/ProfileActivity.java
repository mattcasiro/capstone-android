package com.telmediq.docstorage.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.RealmUtils;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.Profile;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Jared on 18/05/17.
 */

public class ProfileActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.profile_toolbar) Toolbar toolbar;
	@BindView(R.id.profile_firstNameLayout) TextInputLayout firstNameLayout;
	@BindView(R.id.profile_firstNameText) TextView firstNameEditText;
	@BindView(R.id.profile_lastNameLayout) TextInputLayout lastNameLayout;
	@BindView(R.id.profile_lastNameText) TextView lastNameEditText;
	@BindView(R.id.profile_emailLayout) TextInputLayout emailLayout;
	@BindView(R.id.profile_emailText) TextView emailEditText;
	//</editor-fold>

	Profile profile;
	boolean isInEditMode = false;
	Dialog progressDialog;

	//<editor-fold desc="Lifecycle">
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ButterKnife.bind(this);

		getProfile();
		setupToolbar();
		setupViews();
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

	//<editor-fold desc="View Setup">
	private void setupToolbar() {
		setSupportActionBar(toolbar);
		if (getSupportActionBar() == null) {
			return;
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (profile != null) {
			getSupportActionBar().setTitle(String.format("%s %s", profile.getFirstName(), profile.getLastName()));
		} else {
			getSupportActionBar().setTitle("Profile");
		}
	}

	private void setupViews() {
		if (RealmUtils.isManaged(profile)) {
			firstNameEditText.setText(profile.getFirstName());
			lastNameEditText.setText(profile.getLastName());
			emailEditText.setText(profile.getEmail());
		}

		setupEditMode(false);
	}

	void setupEditMode(boolean isEditMode) {
		isInEditMode = isEditMode;
		firstNameLayout.setEnabled(isEditMode);
		lastNameLayout.setEnabled(isEditMode);

		invalidateOptionsMenu();
	}

	void showProgressDialog(boolean show) {
		if (show) {
			progressDialog = ProgressDialog.show(this, "Updating Profile", "", true);
		} else {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}
	//</editor-fold>

	void getProfile() {
		profile = Profile.getProfile(realm);
		setupRealmListener(true);

		Call<Profile> profileCall = getTelmediqService().getProfile();
		profileCall.enqueue(profileCallback);
	}

	void updateProfile() {
		String firstName = firstNameEditText.getText().toString();
		String lastName = lastNameEditText.getText().toString();

		Timber.d("set profile contents");
		showProgressDialog(true);
		Call<Profile> profileUpdateCall = getTelmediqService().putProfile(firstName, lastName);
		profileUpdateCall.enqueue(profileUpdateCallback);
	}

	//<editor-fold desc="Listeners">
	private void setupRealmListener(boolean enabled) {
		if (RealmUtils.isManaged(profile)) {
			profile.removeAllChangeListeners();
			if (enabled) {
				profile.addChangeListener(realmChangeListener);
			}
		}
	}

	RealmChangeListener realmChangeListener = new RealmChangeListener() {
		@Override
		public void onChange(Object element) {
			setupToolbar();
			setupViews();
		}
	};
	//</editor-fold>

	//<editor-fold desc="Menu">
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MaterialMenuInflater.with(this).setDefaultColorResource(android.R.color.white).inflate(R.menu.profile_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.profile_confirm).setVisible(isInEditMode);
		menu.findItem(R.id.profile_cancel).setVisible(isInEditMode);
		menu.findItem(R.id.profile_edit).setVisible(!isInEditMode);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.profile_edit:
				setupEditMode(true);
				break;
			case R.id.profile_confirm:
				updateProfile();
				break;
			case R.id.profile_cancel:
				if (!firstNameEditText.getText().toString().equals(profile.getFirstName()) || !lastNameEditText.getText().toString().equals(profile.getLastName())) {
					Utils.buildAlertDialog(
							findViewById(R.id.profile_view).getContext(),
							R.string.confirm_cancel_edit_title,
							R.string.confirm_cancel_edit_message,
							R.drawable.ic_warning_black,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									firstNameEditText.setText(profile.getFirstName());
									lastNameEditText.setText(profile.getLastName());
									setupEditMode(false);
								}
							}
					).show();
				} else {
					firstNameEditText.setText(profile.getFirstName());
					lastNameEditText.setText(profile.getLastName());
					setupEditMode(false);
				}
				break;
			case android.R.id.home:
				finish();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	//</editor-fold>

	//<editor-fold desc="Network Callbacks">
	Callback<Profile> profileCallback = new Callback<Profile>() {
		@Override
		public void onResponse(Call<Profile> call, final Response<Profile> response) {
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
			}, new Realm.Transaction.OnSuccess() {
				@Override
				public void onSuccess() {
					if (!RealmUtils.isManaged(profile)) {
						profile = Profile.getProfile(realm);
						setupRealmListener(true);
						setupToolbar();
						setupViews();
					}
				}
			});
		}

		@Override
		public void onFailure(Call<Profile> call, Throwable t) {

		}
	};

	Callback<Profile> profileUpdateCallback = new Callback<Profile>() {
		@Override
		public void onResponse(Call<Profile> call, final Response<Profile> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}

			showProgressDialog(false);
			setupEditMode(false);
			realm.executeTransactionAsync(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(response.body());
				}
			});
		}

		@Override
		public void onFailure(Call<Profile> call, Throwable t) {
			showProgressDialog(false);
			Toast.makeText(getApplicationContext(), String.format("Oops!"), Toast.LENGTH_SHORT).show();
		}
	};
	//</editor-fold>
}
