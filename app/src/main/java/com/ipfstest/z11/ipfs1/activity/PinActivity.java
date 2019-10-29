package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.adapter.PinAdapter;
import com.ipfstest.z11.ipfs1.api.IPFSHttpAPI;
import com.ipfstest.z11.ipfs1.common.FilesEntry;
import com.ipfstest.z11.ipfs1.common.MyDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ipfs.api.MerkleNode;

public class PinActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private PinAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "PinActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IPFSHttpAPI.HTTP_API_GET_PINS:
                    Map pins = (Map)msg.obj;
                    mAdapter.updateData(getData(pins));
                    break;

            }
        }
    };

    IPFSHttpAPI mHttpApi = new IPFSHttpAPI(mHandler);

    private ArrayList<FilesEntry> getData(Map id) {
        ArrayList<FilesEntry> aFe = new ArrayList<FilesEntry>();
        aFe.clear();

        return aFe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        initData();
        initView();
        mHttpApi.getPins();
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new PinAdapter(null);
        mAdapter.setOnItemClickListener(new PinAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(PinActivity.this, WebActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                return;
            }
        });
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.files_rv);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }
}
