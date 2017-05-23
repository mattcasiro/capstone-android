package com.telmediq.docstorage.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.File;

import java.util.Dictionary;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
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
    TextView firstName;
    @BindView(R.id.profile_editTextLastName)
    EditText editTextLastName;
    @BindView(R.id.profile_lastName)
    TextView lastName;

    private View[] swappableProfileViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        ButterKnife.bind(this);

        //TODO: fetch first & last name + email from server then set the appropriate View contents
        Timber.d("get profile contents");
        Call<Dictionary> profileCall = getTelmediqService().getProfile();
        profileCall.enqueue(profileCallback);

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

        //swappableProfileViews is a list of all views that are hidden / exposed during button clicks
        swappableProfileViews = new View[]{firstName, editTextFirstName, lastName, editTextLastName, editButton, confirmButton, cancelButton};
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

        firstName.setText(editTextFirstName.getText());
        lastName.setText(editTextLastName.getText());
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

    Callback<Dictionary> profileCallback = new Callback<Dictionary>() {
        @Override
        public void onResponse(Call<Dictionary> call, final Response<Dictionary> response) {
            String error = Utils.checkResponseForError(response);
            if (error != null) {
                onFailure(call, new Throwable(error));
                return;
            }
            /*realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(response.body());
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Timber.d("WOWOW (saved to db)");
                }
            });*/
        }

        @Override
        public void onFailure(Call<Dictionary> call, Throwable t) {

        }
    };
}
