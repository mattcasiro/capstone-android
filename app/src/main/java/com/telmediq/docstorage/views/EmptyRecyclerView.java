package com.telmediq.docstorage.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Andrea on 2017-05-23.
 */

public class EmptyRecyclerView extends RecyclerView {
	private View emptyView;

	private AdapterDataObserver dataObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			updateEmptyView();
		}
	};

	public void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
	}

	public EmptyRecyclerView(Context context) {
		super(context);
	}

	public EmptyRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setAdapter(RecyclerView.Adapter adapter) {
		if (getAdapter() != null) {
			getAdapter().unregisterAdapterDataObserver(dataObserver);
		}
		if (adapter != null) {
			adapter.registerAdapterDataObserver(dataObserver);
		}
		super.setAdapter(adapter);
		updateEmptyView();
	}

	private void updateEmptyView() {
		if (emptyView != null && getAdapter() != null) {
			boolean showEmptyView = getAdapter().getItemCount() == 0;
			emptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
			setVisibility(showEmptyView ? GONE : VISIBLE);
		}
	}
}
