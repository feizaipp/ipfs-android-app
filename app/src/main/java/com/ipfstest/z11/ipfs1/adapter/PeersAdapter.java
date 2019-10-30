package com.ipfstest.z11.ipfs1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.common.PeersEntry;

import java.util.ArrayList;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder>  {
    private ArrayList<PeersEntry> mData;
    private final static String TAG = "PeersAdapter";
    private PeersAdapter.OnItemClickListener mOnItemClickListener;

    public PeersAdapter(ArrayList<PeersEntry> data) {
        this.mData = data;
    }

    public void setOnItemClickListener(PeersAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void updateData(ArrayList<PeersEntry> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    @Override
    public PeersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.peers, parent, false);
        PeersAdapter.ViewHolder viewHolder = new PeersAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PeersAdapter.ViewHolder holder, int position) {
        PeersEntry pe = mData.get(position);

        holder.mTvPeer.setText(pe.getId());

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

        TextView mTvPeer;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvPeer = (TextView) itemView.findViewById(R.id.peer);
        }
    }
}
