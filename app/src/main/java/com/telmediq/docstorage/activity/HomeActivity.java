package com.telmediq.docstorage.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.toolbar) Toolbar toolbar;
	//</editor-fold>

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setupToolbar();
	}

	private void setupToolbar() {
		setSupportActionBar(toolbar);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	//<editor-fold desc="Menu">
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_settings:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	//</editor-fold>

	//<editor-fold desc="Listeners">
	@OnClick(R.id.fab)
	public void onFabClicked(View view) {
		Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
	}
	//</editor-fold>
}
