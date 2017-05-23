package com.telmediq.docstorage.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telmediq.docstorage.R;
import com.telmediq.docstorage.helper.UrlHelper;
import com.telmediq.docstorage.model.DirectoryHolder;
import com.telmediq.docstorage.model.File;
import com.telmediq.docstorage.model.Folder;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Andrea on 2017-05-18.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
	private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = -1;
	private List<DirectoryHolder> data;
	private Listener listener;

	public DirectoryAdapter(List<DirectoryHolder> data, Listener listener) {
		this.data = data;
		this.listener = listener;
	}

	@Override
	public int getItemViewType(int position) {
		if(data.isEmpty()){
			return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
		}

		return data.get(position).getType();
	}


	@Override
	public DirectoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View contentView;

		switch (viewType) {
			case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
				contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_empty_list, parent, false);
				break;
			case DirectoryHolder.FOLDER:
				contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_folder, parent, false);
				break;
			case DirectoryHolder.FILE:
				contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_file, parent, false);
				break;
			case DirectoryHolder.HEADER:
				contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_header, parent, false);
				break;
			default:
				contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_header, parent, false);
				break;
		}
		return new ViewHolder(contentView);
	}

	@Override
	public void onBindViewHolder(DirectoryAdapter.ViewHolder holder, int position) {
		switch (holder.getItemViewType()) {
			case DirectoryHolder.FOLDER:
				holder.bindFolder(data.get(position).getFolder(), listener);
				break;

			case DirectoryHolder.FILE:
				holder.bindFile(data.get(position).getFile(), listener);
				break;

			case DirectoryHolder.HEADER:
			default:
				holder.bindHeader(data.get(position).getHeader());
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public void updateData(List<DirectoryHolder> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		Context ctx;

		@BindView(R.id.listItem_rootView)
		View rootView;

		@Nullable
		@BindView(R.id.listItem_titleTextView)
		TextView headerTitleText;

		@Nullable
		@BindView(R.id.listItemFolder_name)
		TextView folderTitleText;


		// File binds
		@Nullable
		@BindView(R.id.filelistitem_filename)
		TextView filename;

		@Nullable
		@BindView(R.id.filelistitem_modified_date)
		TextView modifiedDate;

		@Nullable
		@BindView(R.id.filelistitem_menu)
		MaterialIconView fileOptionsIcon;

		@Nullable
		@BindView(R.id.filelistitem_image)
		ImageView thumbnail;
		// end File binds


		ViewHolder(View view) {
			super(view);
			ctx = view.getContext();
			ButterKnife.bind(this, view);
		}

		void bindHeader(String headerText) {
			if (headerTitleText == null) {
				return;
			}
			headerTitleText.setText(headerText);
		}

		void bindFolder(final Folder folder, final DirectoryAdapter.Listener listener) {
			if (folderTitleText == null) {
				return;
			}
			folderTitleText.setText(folder.getName());

			if (listener == null) {
				return;
			}

			rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onFolderClicked(folder.getId());
				}
			});
		}

		void bindFile(final File file, final DirectoryAdapter.Listener listener) {
			if (filename == null || fileOptionsIcon == null) {
				return;
			}
			DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
			filename.setText(file.getName());

			modifiedDate.setText(df.format(file.getModified()));

			Glide.with(ctx)
					.load(UrlHelper.getAuthenticatedUrl(file.getUrl()))
					.into(thumbnail);

			if (listener == null) {
				return;
			}

			rootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onFileClicked(file.getId());
				}
			});

			fileOptionsIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Timber.i("File menu clicked");
					listener.onFileOptionClicked(file.getId());
				}
			});
		}
	}

	public interface Listener {
		void onFolderClicked(Integer folderId);

		void onFileClicked(Integer fileId);

		void onFileOptionClicked(Integer fileId);
	}
}
