package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.helper.AppValues;
import com.telmediq.docstorage.helper.Constants;
import com.telmediq.docstorage.helper.Utils;
import com.telmediq.docstorage.model.AuthorizationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.telmediq.docstorage.R.layout.activity_login;

public class LoginActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.activityLogin_emailText)
	EditText emailEditText;
	@BindView(R.id.activityLogin_passwordText)
	EditText passwordEditText;
	@BindView(R.id.activityLogin_loginButton)
	Button loginButton;
	@BindView(R.id.activityLogin_emailLayout)
	TextInputLayout emailLayout;
	@BindView(R.id.activityLogin_passwordLayout)
	TextInputLayout passwordLayout;
	//</editor-fold>

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(activity_login);
		ButterKnife.bind(this);

		loginButton.setOnClickListener(loginClicked);
	}

	private void login() {
		String email = emailEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		// Validate both so that all errors are displayed, then check the validity
		validateEmailAddress();
		validatePassword();
		if (!validateEmailAddress() || !validatePassword()) {
			Timber.e("Email or password not filled in, login cancelled.");
			return;
		}
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

			// Redirect user to the Home activity
			Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
			intent.putExtra(Constants.Extras.FOLDER_ID, response.body().getRootId());
			startActivity(intent);
			finish();
		}

		@Override
		public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
			Timber.e(t.getMessage());
		}
	};
	//</editor-fold>

	//<editor-fold desc="Validation">
	private boolean validateEmailAddress() {
		if (emailEditText == null) {
			return false;
		}

		String error = validateEmailAddress(emailEditText.getText().toString());
		if (error != null) {
			emailLayout.setError(error);
			return false;
		} else {
			emailLayout.setErrorEnabled(false);
		}
		return true;
	}

	private String validateEmailAddress(String email) {
		if (email == null || email.equalsIgnoreCase("")) {
			return "Email address is required";
		}
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			return "Please use a valid email address";
		}
		return null;
	}

	private boolean validatePassword() {
		if (passwordEditText == null) {
			return false;
		}

		String error = validatePassword(passwordEditText.getText().toString());
		if (error != null) {
			passwordLayout.setError(error);
			return false;
		} else {
			passwordLayout.setErrorEnabled(false);
		}
		return true;
	}

	private String validatePassword(String password) {
		if (password == null || password.equalsIgnoreCase("")) {
			return "Password is required";
		}
		return null;
	}
	//</editor-fold>
}
