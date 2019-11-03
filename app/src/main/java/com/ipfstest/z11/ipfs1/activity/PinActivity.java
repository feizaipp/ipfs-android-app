package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.adapter.PinAdapter;
import com.ipfstest.z11.ipfs1.api.IPFSHttpAPI;
import com.ipfstest.z11.ipfs1.common.FilesEntry;
import com.ipfstest.z11.ipfs1.common.MyDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.wandersnail.fileselector.FileSelector;
import cn.wandersnail.fileselector.OnFileSelectListener;
import io.ipfs.multihash.Multihash;

public class PinActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private PinAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "PinActivity";
    FileSelector selector;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IPFSHttpAPI.HTTP_API_GET_PINS:
                    Map<Multihash, Object> pins = (Map)msg.obj;
                    mAdapter.updateData(getData(pins));
                    break;
                case IPFSHttpAPI.HTTP_API_PIN_RM:
                    mHttpApi.getPins();
                    break;
            }
        }
    };

    IPFSHttpAPI mHttpApi = new IPFSHttpAPI(mHandler);

    private ArrayList<FilesEntry> getData(Map<Multihash, Object> pins) {
        ArrayList<FilesEntry> aFe = new ArrayList<FilesEntry>();
        aFe.clear();
        Log.d(TAG, pins.toString());
        //Map<Multihash, Object>
        for (Map.Entry<Multihash, Object> entry : pins.entrySet()) {

            Log.d(TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
            FilesEntry fe = new FilesEntry("", entry.getKey().toString(), "");
            aFe.add(fe);

        }

        return aFe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        selector = new FileSelector().setScreenOrientation(false).showHiddenFiles(true);
        selector.setTitle("文件选择器");

        initData();
        initView();
        mHttpApi.getPins();
    }

    OnFileSelectListener download_listener = new OnFileSelectListener() {
        @Override
        public void onFileSelect(int requestCode, List<String> paths) {
            Log.d(TAG, "requestCode: " + requestCode);
            switch (requestCode) {
                case 2:
                    File file = new File(paths.get(0));
                    String name = file.getName();
                    Log.d(TAG, name);

                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selector.onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new PinAdapter(null);
        mAdapter.setOnItemClickListener(new PinAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView mTvHash;
                
                mTvHash = (TextView) view.findViewById(R.id.hash);
                Intent intent = new Intent(PinActivity.this, WebActivity.class);
                intent.putExtra("hash", mTvHash.getText().toString());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                PopupMenu popup = new PopupMenu(PinActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.menu_pop_pin, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        TextView mTvHash;
                        switch (item.getItemId()){
                            case R.id.copy_hash:
                                mTvHash = (TextView) view.findViewById(R.id.hash);

                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData mClipData = ClipData.newPlainText("text", mTvHash.getText().toString());
                                cm.setPrimaryClip(mClipData);
                                break;
                            case R.id.download:
                                selector.setOnFileSelectListener(download_listener);
                                selector.setMultiSelectionEnabled(true);
                                selector.setSelectionMode(FileSelector.FILES_ONLY);
                                selector.select(PinActivity.this, 2);
                                break;
                            case R.id.cancel_pin:
                                mTvHash = (TextView) view.findViewById(R.id.hash);
                                Multihash hash = Multihash.fromBase58(mTvHash.getText().toString());
                                mHttpApi.pinRm(hash);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
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
