package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.adapter.PeersAdapter;
import com.ipfstest.z11.ipfs1.api.IPFSHttpAPI;
import com.ipfstest.z11.ipfs1.common.FilesEntry;
import com.ipfstest.z11.ipfs1.common.MyDividerItemDecoration;
import com.ipfstest.z11.ipfs1.common.PeersEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.ipfs.api.MerkleNode;
import io.ipfs.api.Peer;
import io.ipfs.multihash.Multihash;

public class PeersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PeersAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "PeersActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers);

        initData();
        initView();
        mHttpApi.getSwarmPeers();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IPFSHttpAPI.HTTP_API_GET_SWARM_PEERS:
                    List<Peer> peers = (List<Peer>)msg.obj;
                    mAdapter.updateData(getData(peers));
                    break;
            }
        }
    };

    IPFSHttpAPI mHttpApi = new IPFSHttpAPI(mHandler);

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new PeersAdapter(null);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.peers_rv);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private ArrayList<PeersEntry>getData(List<Peer> peers) {
        ArrayList<PeersEntry> aPe = new ArrayList<PeersEntry>();
        aPe.clear();

        for (int i=0; i<peers.size(); i++) {
            Peer p = peers.get(i);
            PeersEntry pe = new PeersEntry(p.address.toString(), p.id.toString());
            aPe.add(pe);
        }

        return aPe;
    }
}
