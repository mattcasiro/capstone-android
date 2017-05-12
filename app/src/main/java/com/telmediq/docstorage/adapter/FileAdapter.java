package com.telmediq.docstorage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telmediq.docstorage.R;
import com.telmediq.docstorage.model.File;

import net.steamcrafted.materialiconlib.MaterialIconView;

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
	private Listener listener;

    public FileAdapter(List<File> files, Listener listener) {
	    this.viewDataset = files;
	    this.listener = listener;
    }

    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_filelistitem, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FileAdapter.ViewHolder holder, int position) {
	    holder.bindView(viewDataset.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return viewDataset.size();
    }

	static class ViewHolder extends RecyclerView.ViewHolder{
		//<editor-fold desc="View Initialization">
		@BindView(R.id.filelistitem_filename) TextView filename;
		@BindView(R.id.filelistitem_modified_date) TextView modifiedDate;
		@BindView(R.id.filelistitem_menu) MaterialIconView fileOptionsIcon;
		@BindView(R.id.filelistitem_root) View rootView;
		//</editor-fold>

		Context ctx;

		ViewHolder(View view){
			super(view);
			ctx = view.getContext();
			ButterKnife.bind(this, view);
		}

		void bindView(File file, final Listener listener){
			DateFormat df  = new SimpleDateFormat("MMM dd, yyyy");
			filename.setText(file.getName());

			modifiedDate.setText(df.format(file.getModified()));

			setupListener(file.getId(), listener);
		}

		private void setupListener(final String fileId, final Listener listener) {
			if (listener == null){
				return;
			}

			rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onItemClicked(fileId);
				}
			});

			fileOptionsIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onItemOptionSelected(fileId);
				}
			});
		}
	}

	public interface Listener {
		void onItemClicked(String fileId);

		void onItemOptionSelected(String fileId);
	}
}
