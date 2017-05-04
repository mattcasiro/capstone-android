package com.telmediq.docstorage.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telmediq.docstorage.R;

/**
 * Created by root on 03/05/17.
 */

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {

    private String[] myDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ViewHolder(TextView v){
            super(v);
            mTextView = v;
        }
    }

    public StringAdapter(String[] myDataset) {
        this.myDataset = myDataset;
    }

    public StringAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.string_text_view, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(StringAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(myDataset[position]);
    }

    @Override
    public int getItemCount() {
        return myDataset.length;
    }
}
