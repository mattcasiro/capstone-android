package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.AppValues;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.AuthorizationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.activityLogin_emailText)
	EditText emailEditText;
	@BindView(R.id.activityLogin_passwordText)
	EditText passwordEditText;
	@BindView(R.id.activityLogin_loginButton)
	Button loginButton;
	//</editor-fold>

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);

		loginButton.setOnClickListener(loginClicked);
	}

	private void login() {
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		Timber.i("email: %s, password: %s", email, password);

		Call<AuthorizationResponse> loginCall = getTelmediqService().login(email, password);
		loginCall.enqueue(loginCallback);
	}

	//<editor-fold desc="Listeners">
	View.OnClickListener loginClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			login();
		}
	};
	//</editor-fold>

	//<editor-fold desc="Network Callbacks">
	Callback<AuthorizationResponse> loginCallback = new Callback<AuthorizationResponse>() {
		@Override
		public void onResponse(Call<AuthorizationResponse> call, Response<AuthorizationResponse> response) {
			String error = Utils.checkResponseForError(response);
			if (error != null) {
				onFailure(call, new Throwable(error));
				return;
			}

			Timber.i(response.body().getStatus());
			AppValues.setAccessToken(response.body());
			AppValues.setRootFolderId(response.body());

			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
			Timber.e(t.getMessage());
		}
	};
	//</editor-fold>
}
