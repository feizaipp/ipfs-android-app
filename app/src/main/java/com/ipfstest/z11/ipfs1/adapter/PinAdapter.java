package com.ipfstest.z11.ipfs1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.common.FilesEntry;

import java.util.ArrayList;

public class PinAdapter extends RecyclerView.Adapter<PinAdapter.ViewHolder> {
    private ArrayList<FilesEntry> mData;
    private final static String TAG = "PinAdapter";
    private PinAdapter.OnItemClickListener mOnItemClickListener;

    public PinAdapter(ArrayList<FilesEntry> data) {
        this.mData = data;
    }

    public void setOnItemClickListener(PinAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void updateData(ArrayList<FilesEntry> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    @Override
    public PinAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pin_files, parent, false);
        PinAdapter.ViewHolder viewHolder = new PinAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PinAdapter.ViewHolder holder, int position) {
        FilesEntry fe = mData.get(position);

        holder.mTvHash.setText(fe.getHash());
        holder.mTvSize.setText(fe.getSize());

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvHash, mTvSize;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvHash = (TextView) itemView.findViewById(R.id.hash);
            mTvSize = (TextView) itemView.findViewById(R.id.size);
        }
    }
}
