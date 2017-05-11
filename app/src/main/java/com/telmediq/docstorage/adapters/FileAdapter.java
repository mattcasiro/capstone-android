package com.telmediq.docstorage.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.model.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 03/05/17.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<File> viewDataset;

    public FileAdapter(List<File> files) {
        viewDataset = files;
    }

    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_filelistitem, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FileAdapter.ViewHolder holder, int position) {
	    holder.bindView(viewDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return viewDataset.size();
    }

	static class ViewHolder extends RecyclerView.ViewHolder{
		@BindView(R.id.text_top) TextView textView1;
		@BindView(R.id.text_bottom) TextView textView2;

		public ViewHolder(View view){
			super(view);
			ButterKnife.bind(this, view);
		}

		public void bindView(File file){
			DateFormat df  = new SimpleDateFormat("MMM dd, yyyy");
			textView1.setText(file.getName());
			textView2.setText(df.format(file.getCreated()));
		}
	}
}
