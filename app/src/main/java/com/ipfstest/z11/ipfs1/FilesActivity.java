package com.ipfstest.z11.ipfs1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.wandersnail.fileselector.FileSelector;
import cn.wandersnail.fileselector.OnFileSelectListener;

public class FilesActivity extends CheckPermissionsActivity {

    private static final String TABALENAME = "ipfs_files";
    private static final String DBNAME = "ipfs.db";
    private static final String CREATETABLE = "CREATE TABLE " + TABALENAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, hash TEXT, size TEXT)";

    private RecyclerView mRecyclerView;
    private FileAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = "FilesActivity";
    FileSelector selector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        selector = new FileSelector().setScreenOrientation(false).showHiddenFiles(true);
        selector.setTitle("文件选择器");
        selector.setOnFileSelectListener(listener);
    }

    OnFileSelectListener listener = new OnFileSelectListener() {
        @Override
        public void onFileSelect(int requestCode, List<String> paths) {
            Log.d(TAG, "requestCode: " + requestCode);
            for (int i=0; i<paths.size(); i++) {
                Log.d(TAG, paths.get(i));
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
        database.insert("student", null, values);
        database.close();
    }

    public void insertSQL(ArrayList<FilesEntry> aFe) {
        FilesSQLiteOpenHelper mysql = new FilesSQLiteOpenHelper(this, DBNAME, 1);
        SQLiteDatabase database = mysql.getWritableDatabase();

        for (int i=0; i<aFe.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("name", aFe.get(i).getName());
            values.put("hash", aFe.get(i).getHash());
            values.put("size", aFe.get(i).getSize());
            database.insert(TABALENAME, null, values);
        }

        database.close();
    }

    public ArrayList<FilesEntry> quaryAll() {
        ArrayList<FilesEntry> aFe = new ArrayList<FilesEntry>();
        aFe.clear();
        FilesSQLiteOpenHelper mysql = new FilesSQLiteOpenHelper(this, DBNAME, 1);
        SQLiteDatabase database = mysql.getWritableDatabase();
        Cursor cursor = database.query(TABALENAME,null,null,null,null,null,null);
        cursor.moveToFirst();
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
        mAdapter = new FileAdapter(quaryAll());
        mAdapter.setOnItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(FilesActivity.this, position + " click",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(FilesActivity.this, position + " long click",
                        Toast.LENGTH_SHORT).show();
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
