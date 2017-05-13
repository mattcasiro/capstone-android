package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;
import com.telmediq.docstorage.fragment.BottomSheetFileDetailsFragment;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileViewActivity extends TelmediqActivity {
	@BindView(R.id.fileViewActivity_fileView)
	ImageView fileView;
	@BindView(R.id.back_arrow)
	MaterialIconView backArrow;
	@BindView(R.id.fileName)
	TextView fileName;
	@BindView(R.id.file_options)
	MaterialIconView fileOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_file_view);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.file_options)
	void onFileOptionClicked(View view) {
		BottomSheetFileDetailsFragment.newInstance("fileId").show(getSupportFragmentManager(), BottomSheetFileDetailsFragment.class.getSimpleName());

	}

	@OnClick(R.id.back_arrow)
	void onBackArrowClicked(View view) {
		finish();
	}
}
