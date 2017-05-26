package com.telmediq.docstorage.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class ProfileViewActivity extends TelmediqActivity{
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

        //TODO: fetch first & last name + email from server then set the appropriate View contents
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
        //profile = realmProfile.first();

        Timber.d("get profile contents");
        Call<Profile> profileCall = getTelmediqService().getProfile();
        profileCall.enqueue(profileCallback);
    }

    private void setupViews(){
        if(profile != null) {
            textViewFirstName.setText(profile.getFirstName());
            textViewLastName.setText(profile.getLastName());
            textViewEmail.setText(profile.getEmail());
        }
    }

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

        textViewFirstName.setText(editTextFirstName.getText());
        textViewLastName.setText(editTextLastName.getText());
        swapViews();
    }

    void swapViews(){
        for(View v : swappableProfileViews){
            if(v.getVisibility() == View.VISIBLE){
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }
}
