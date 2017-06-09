package com.telmediq.docstorage.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.Profile;

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
    @BindView(R.id.profile_editButton)
    Button editButton;
    @BindView(R.id.profile_cancelButton)
    Button cancelButton;
    @BindView(R.id.profile_confirmButton)
    Button confirmButton;
    @BindView(R.id.profile_editTextFirstName)
    EditText editTextFirstName;
    @BindView(R.id.profile_firstName)
    TextView textViewFirstName;
    @BindView(R.id.profile_editTextLastName)
    EditText editTextLastName;
    @BindView(R.id.profile_lastName)
    TextView textViewLastName;
    @BindView(R.id.profile_email)
    TextView textViewEmail;

    RealmResults<Profile> realmProfile;
    Profile profile;


    RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            setupViews();
        }
    };

    private View[] swappableProfileViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        ButterKnife.bind(this);

        swappableProfileViews = new View[]{textViewFirstName, editTextFirstName, textViewLastName, editTextLastName, editButton, confirmButton, cancelButton};

        getProfile();
        setupViews();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditButtonClicked(v);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelButtonClicked(v);
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmButtonClicked(v);
            }
        });

    }

    void getProfile(){
        realmProfile = Profile.getProfile(realm);
        realmProfile.addChangeListener(realmChangeListener);

        Timber.d("get profile contents");
        Call<Profile> profileCall = getTelmediqService().getProfile();
        profileCall.enqueue(profileCallback);
    }

    void updateProfile(){
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
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
    }


    //<editor-fold desc="ButtonMethods">
    @OnClick(R.id.profile_editButton)
    void onEditButtonClicked(View view){
        swapViews();
    }

    @OnClick(R.id.profile_cancelButton)
    void onCancelButtonClicked(View view){
        swapViews();
    }

    @OnClick(R.id.profile_confirmButton)
    void onConfirmButtonClicked(View view){
        //TODO: update server & realm values for first & last name
        updateProfile();

        textViewFirstName.setText(editTextFirstName.getText());
        textViewLastName.setText(editTextLastName.getText());
        swapViews();
    }
    //</editor-fold>

    void swapViews(){
        for(View v : swappableProfileViews){
            if(v.getVisibility() == View.VISIBLE){
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }


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
            Toast.makeText(getApplicationContext(), String.format("Profile Updated."), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Call<Profile> call, Throwable t) {
            Toast.makeText(getApplicationContext(), String.format("Oops!"), Toast.LENGTH_SHORT).show();
        }
    };
    //</editor-fold>
}
