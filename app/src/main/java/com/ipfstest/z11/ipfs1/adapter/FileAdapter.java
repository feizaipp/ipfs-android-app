package com.ipfstest.z11.ipfs1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.common.FilesEntry;

import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private ArrayList<FilesEntry> mData;
    private final static String TAG = "FileAdapter";
    private OnItemClickListener mOnItemClickListener;

    public FileAdapter(ArrayList<FilesEntry> data) {
        this.mData = data;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void updateData(ArrayList<FilesEntry> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.files, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据
        FilesEntry fe = mData.get(position);

        holder.mTvName.setText(fe.getName());
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

        TextView mTvName, mTvHash, mTvSize;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvName = (TextView) itemView.findViewById(R.id.name);
            mTvHash = (TextView) itemView.findViewById(R.id.hash);
            mTvSize = (TextView) itemView.findViewById(R.id.size);
        }
    }
}
