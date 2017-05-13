package com.telmediq.docstorage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.TelmediqActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileViewActivity extends TelmediqActivity {
	@BindView(R.id.fileViewActivity_fileView)
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_file_view);
		ButterKnife.bind(this);
	}
}
