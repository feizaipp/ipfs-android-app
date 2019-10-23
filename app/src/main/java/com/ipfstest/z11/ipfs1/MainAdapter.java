package com.ipfstest.z11.ipfs1;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private Map<String, String> mData;
    private final static String TAG = "MainAdapter";

    public MainAdapter(Map<String, String> data) {
        this.mData = data;
    }

    public void updateData(Map<String, String> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.status, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData == null)
            return;
        // 绑定数据
        String s1 = mData.keySet().toArray()[position].toString();
        String s2 = mData.get(s1);
        Log.d(TAG, s1 + "  " + position);
        Log.d(TAG, s2 + "  " + position);
        holder.mTv1.setText(s1);
        holder.mTv2.setText(s2);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTv1, mTv2;

        public ViewHolder(View itemView) {
            super(itemView);
            mTv1 = (TextView) itemView.findViewById(R.id.title);
            mTv2 = (TextView) itemView.findViewById(R.id.info);
        }
    }
}

