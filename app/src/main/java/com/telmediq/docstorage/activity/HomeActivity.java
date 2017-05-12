package com.telmediq.docstorage.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.adapter.FileAdapter;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;
import com.telmediq.docstorage.model.File;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends TelmediqActivity {
	//<editor-fold desc="View Initialization">
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.homeActivity_recyclerView) RecyclerView recyclerView;
	//</editor-fold>

	RecyclerView.LayoutManager layoutManager;
	FileAdapter adapter;

	List<File> files;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setupToolbar();
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);

		recyclerView.setLayoutManager(layoutManager);

		files = new ArrayList<File>();
		files.add(new File(UUID.randomUUID().toString(), "File1", new Date(), new Date(), 1234));
		files.add(new File(UUID.randomUUID().toString(), "File2", new Date(), new Date(), 14));

		adapter = new FileAdapter(files, fileListener);
		recyclerView.setAdapter(adapter);
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

	FileAdapter.Listener fileListener = new FileAdapter.Listener() {
		@Override
		public void onItemClicked(String fileId) {
			Toast.makeText(getApplicationContext(), String.format("Selected: %s", fileId), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onItemOptionSelected(String fileId) {
			BottomSheetFileDetailsFragment.newInstance(fileId).show(getSupportFragmentManager(), BottomSheetFileDetailsFragment.class.getSimpleName());
		}
	};
	//</editor-fold>
}