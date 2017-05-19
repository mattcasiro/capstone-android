package com.telmediq.docstorage.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        ButterKnife.bind(this);

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

    @OnClick(R.id.profile_editButton)
    void onEditButtonClicked(View view){
        firstName.setVisibility(View.GONE);
        editTextFirstName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.GONE);
        editTextLastName.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        confirmButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.profile_cancelButton)
    void onCancelButtonClicked(View view){
        firstName.setVisibility(View.VISIBLE);
        editTextFirstName.setVisibility(View.GONE);
        lastName.setVisibility(View.VISIBLE);
        editTextLastName.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.profile_confirmButton)
    void onConfirmButtonClicked(View view){
        firstName.setVisibility(View.VISIBLE);
        editTextFirstName.setVisibility(View.GONE);
        lastName.setVisibility(View.VISIBLE);
        editTextLastName.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }
}
