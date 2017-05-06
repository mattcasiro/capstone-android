package com.telmediq.docstorage.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telmediq.docstorage.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 03/05/17.
 */

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {

    private String[] myDataset;


    public StringAdapter(String[] myDataset) {
        this.myDataset = myDataset;
    }

    public StringAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.string_text_view, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StringAdapter.ViewHolder holder, int position) {
	    holder.bindView(myDataset[position]);

    }

    @Override
    public int getItemCount() {
        return myDataset.length;
    }

	public static class ViewHolder extends RecyclerView.ViewHolder{
		@BindView(R.id.test_boop) RelativeLayout relativeLayout;


		public ViewHolder(RelativeLayout view){
			super(view);
			ButterKnife.bind(this, view);

		}

		public void bindView(String text){
			TextView textTop = (TextView) relativeLayout.findViewById(R.id.text_top);
			textTop.setText(text);
		}
	}
}
