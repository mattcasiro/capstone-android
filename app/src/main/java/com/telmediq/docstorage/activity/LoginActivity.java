package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.activityLogin_emailText) EditText emailEditText;
	@BindView(R.id.activityLogin_passwordText) EditText passwordEditText;
	@BindView(R.id.activityLogin_loginButton) Button loginButton;
	//</editor-fold>

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);

		loginButton.setOnClickListener(loginClicked);
	}

	//<editor-fold desc="Listeners">
	View.OnClickListener loginClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
			startActivity(intent);
			finish();
		}
	};
	//</editor-fold>
}
