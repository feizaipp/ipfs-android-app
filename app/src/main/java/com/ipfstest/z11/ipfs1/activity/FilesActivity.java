package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.activity.CheckPermissionsActivity;
import com.ipfstest.z11.ipfs1.adapter.FileAdapter;
import com.ipfstest.z11.ipfs1.api.IPFSHttpAPI;
import com.ipfstest.z11.ipfs1.common.FilesEntry;
import com.ipfstest.z11.ipfs1.common.FilesSQLiteOpenHelper;
import com.ipfstest.z11.ipfs1.common.MyDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.wandersnail.fileselector.FileSelector;
import cn.wandersnail.fileselector.OnFileSelectListener;
import io.ipfs.api.MerkleNode;

public class FilesActivity extends CheckPermissionsActivity {

    private static final String TABALENAME = "ipfs_files";
    private static final String DBNAME = "ipfs2.db";

    private RecyclerView mRecyclerView;
    private FileAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "FilesActivity";
    FileSelector selector;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IPFSHttpAPI.HTTP_API_GET_PEERS_ID:

                    break;

                case IPFSHttpAPI.HTTP_API_GET_PINS:

                    break;

                case IPFSHttpAPI.HTTP_API_GET_SWARM_PEERS:

                    break;

                case IPFSHttpAPI.HTTP_API_GET_SWARM_PEERS_COUNT:
                    break;

                case IPFSHttpAPI.HTTP_API_ADD_FILE:
                    List<MerkleNode> files = (List<MerkleNode>)msg.obj;
                    MerkleNode file = files.get(0);
                    addFileInfoToDB(file);

                    break;

                case IPFSHttpAPI.HTTP_API_ADD_DIR:
                    List<MerkleNode> dirs = (List<MerkleNode>)msg.obj;
                    MerkleNode dir = dirs.get(dirs.size() - 1);
                    addFileInfoToDB(dir);
                    break;
            }
        }
    };

    IPFSHttpAPI mHttpApi = new IPFSHttpAPI(mHandler);

    private void addFileInfoToDB(MerkleNode addResult) {
        String name = addResult.name.get();
        String hash = addResult.hash.toString();
        String size = addResult.size.isPresent()?addResult.size.get().toString():"0";
        Log.d(TAG, "name: " + name + ", hash: " + hash + ", size: "+ size);
        FilesEntry fe = new FilesEntry(name, hash, size);
        insertSQL(fe);
        mAdapter.updateData(queryAll());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        selector = new FileSelector().setScreenOrientation(false).showHiddenFiles(true);
        selector.setTitle("文件选择器");
        selector.setOnFileSelectListener(listener);

        initData();
        initView();
    }

    OnFileSelectListener listener = new OnFileSelectListener() {
        @Override
        public void onFileSelect(int requestCode, List<String> paths) {
            Log.d(TAG, "requestCode: " + requestCode);
            switch (requestCode) {
                case 1:
                    for (int i = 0; i < paths.size(); i++) {
                        File file = new File(paths.get(i));
                        String name = file.getName();
                        Log.d(TAG, name);
                        mHttpApi.add_files(paths.get(i), false, 0);
                    }
                    break;

                case 4:
                    for (int i = 0; i < paths.size(); i++) {
                        File file = new File(paths.get(i));
                        String name = file.getName();
                        Log.d(TAG, name);
                        mHttpApi.add_files(paths.get(i), false, 1);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Menu menu;

        int id = item.getItemId();
        switch (id) {
            case R.id.add_file:
                selector.setMultiSelectionEnabled(true);
                selector.setSelectionMode(FileSelector.FILES_ONLY);
                selector.select(FilesActivity.this, 1);
                break;
            case R.id.add_folder:
                selector.setMultiSelectionEnabled(true);
                selector.setSelectionMode(FileSelector.DIRECTORIES_ONLY);
                selector.select(FilesActivity.this, 4);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void insertSQL(FilesEntry fe) {
        FilesSQLiteOpenHelper mysql = new FilesSQLiteOpenHelper(this, DBNAME, 1);
        SQLiteDatabase database = mysql.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", fe.getName());
        values.put("hash", fe.getHash());
        values.put("size", fe.getSize());
        database.insert(TABALENAME, null, values);
        database.close();
    }

    public void insertSQL(ArrayList<FilesEntry> aFe) {
        FilesSQLiteOpenHelper mysql = new FilesSQLiteOpenHelper(this, DBNAME, 1);
        SQLiteDatabase database = mysql.getWritableDatabase();

        for (int i = 0; i < aFe.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("name", aFe.get(i).getName());
            values.put("hash", aFe.get(i).getHash());
            values.put("size", aFe.get(i).getSize());
            database.insert(TABALENAME, null, values);
        }

        database.close();
    }

    public ArrayList<FilesEntry> queryAll() {
        ArrayList<FilesEntry> aFe = new ArrayList<FilesEntry>();
        aFe.clear();
        FilesSQLiteOpenHelper mysql = new FilesSQLiteOpenHelper(this, DBNAME, 1);
        SQLiteDatabase database = mysql.getWritableDatabase();
        Cursor cursor = database.query(TABALENAME, null, null, null, null, null, null);
        boolean isNull = cursor.moveToFirst();
        if (!isNull) {
            return null;
        }
        do {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String hash = cursor.getString(cursor.getColumnIndex("hash"));
            String size = cursor.getString(cursor.getColumnIndex("size"));
            FilesEntry fe = new FilesEntry(name, hash, size);
            aFe.add(fe);
        } while (cursor.moveToNext());
        database.close();
        Log.d(TAG, aFe.toString());

        return aFe;
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new FileAdapter(queryAll());
        mAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView mTvHash;
                mTvHash = (TextView) view.findViewById(R.id.hash);
                Intent intent = new Intent(FilesActivity.this, WebActivity.class);
                intent.putExtra("hash", mTvHash.getText().toString());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                PopupMenu popup = new PopupMenu(FilesActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.menu_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.copy_hash:
                                TextView mTvHash;
                                mTvHash = (TextView) view.findViewById(R.id.hash);

                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData mClipData = ClipData.newPlainText("text", mTvHash.getText().toString());
                                cm.setPrimaryClip(mClipData);
                                break;
                            case R.id.download:

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
