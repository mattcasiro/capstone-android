package com.telmediq.docstorage.activity;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.Profile;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Jared on 18/05/17.
 */

public class ProfileActivity extends TelmediqActivity{
	//<editor-fold desc="View Initialization">
	@BindView(R.id.profile_toolbar)
    @Nullable
	Toolbar toolbar;
    /*@BindView(R.id.profile_editButton)
    Button editButton;
    @BindView(R.id.profile_cancelButton)
    Button cancelButton;
    @BindView(R.id.profile_confirmButton)
    Button confirmButton;*/
    @BindView(R.id.profile_firstNameLayout)
    TextInputLayout firstNameLayout;
    @BindView(R.id.profile_firstNameText)
    TextView textViewFirstName;
    @BindView(R.id.profile_lastNameLayout)
    TextInputLayout lastNameLayout;
    @BindView(R.id.profile_lastNameText)
    TextView textViewLastName;
    @BindView(R.id.profile_emailLayout)
    TextInputLayout emailLayout;
    @BindView(R.id.profile_emailText)
    TextView textViewEmail;
	//</editor-fold>

    RealmResults<Profile> realmProfile;
    Profile profile;
    boolean edit = false;


    RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            setupViews();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        getProfile();
        setupViews();
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        if(profile != null) {
            getSupportActionBar().setTitle(profile.getFirstName());
        } else {
            getSupportActionBar().setTitle("Profile");
        }
    }

    void getProfile(){
        realmProfile = Profile.getProfile(realm);
        realmProfile.addChangeListener(realmChangeListener);

        Timber.d("get profile contents");
        Call<Profile> profileCall = getTelmediqService().getProfile();
        profileCall.enqueue(profileCallback);
    }

    void updateProfile(){
        String firstName = textViewFirstName.getText().toString();
        String lastName = textViewLastName.getText().toString();
        profile.setFirstName(firstName);
        profile.setLastName(lastName);

        Timber.d("set profile contents");
        Call<Profile> profileUpdateCall = getTelmediqService().putProfile(firstName, lastName);
        profileUpdateCall.enqueue(profileUpdateCallback);
    }

    private void setupViews(){
        if(profile != null) {
            textViewFirstName.setText(profile.getFirstName());
            textViewLastName.setText(profile.getLastName());
            textViewEmail.setText(profile.getEmail());
        }

        firstNameLayout.setEnabled(false);
        lastNameLayout.setEnabled(false);
        emailLayout.setEnabled(false);
    }

    void swapViews(){
        if(!edit){
            edit = true;
            firstNameLayout.setEnabled(true);
            lastNameLayout.setEnabled(true);
        } else {
            edit=false;
            firstNameLayout.setEnabled(false);
            lastNameLayout.setEnabled(false);
        }
        invalidateOptionsMenu();
    }

    //<editor-fold desc="Menu">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialMenuInflater.with(this).setDefaultColorResource(android.R.color.white).inflate(R.menu.profile_menu, menu);

        if(edit){
            menu.findItem(R.id.profile_confirm).setVisible(true);
            menu.findItem(R.id.profile_cancel).setVisible(true);
            menu.findItem(R.id.profile_edit).setVisible(false);
        } else {
            menu.findItem(R.id.profile_confirm).setVisible(false);
            menu.findItem(R.id.profile_cancel).setVisible(false);
            menu.findItem(R.id.profile_edit).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_edit:
                swapViews();

                break;
            case R.id.profile_confirm:
                updateProfile();
                swapViews();
                break;
            case R.id.profile_cancel:
                if(!textViewFirstName.getText().toString().equals(profile.getFirstName()) ||
                        !textViewLastName.getText().toString().equals(profile.getLastName())){
                    Utils.buildAlertDialog(
                            findViewById(R.id.profile_view).getContext(),
                            R.string.confirm_cancel_edit_title,
                            R.string.confirm_cancel_edit_message,
                            R.drawable.ic_warning_black,
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    textViewFirstName.setText(profile.getFirstName());
                                    textViewLastName.setText(profile.getLastName());
                                    swapViews();
                                }
                            }
                    ).show();
                } else {
                    textViewFirstName.setText(profile.getFirstName());
                    textViewLastName.setText(profile.getLastName());
                    swapViews();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="Callbacks">
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
                    Timber.d("WOWOW (saved to db)");
                }
            });
            profile = response.body();
            getSupportActionBar().setTitle(profile.getFirstName());
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
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(response.body());
                }
            });
            Toast.makeText(getApplicationContext(), String.format("Profile Updated."), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Call<Profile> call, Throwable t) {
            Toast.makeText(getApplicationContext(), String.format("Oops!"), Toast.LENGTH_SHORT).show();
        }
    };
    //</editor-fold>
}
