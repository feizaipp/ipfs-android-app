package com.ipfstest.z11.ipfs1.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FilesSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TABALENAME = "ipfs_files";
    private static final String CREATETABLE = "CREATE TABLE " + TABALENAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, age INTEGER)";

    public FilesSQLiteOpenHelper(Context context, String name, int version){
        super(context,name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABALENAME);
        onCreate(db);
    }
}
