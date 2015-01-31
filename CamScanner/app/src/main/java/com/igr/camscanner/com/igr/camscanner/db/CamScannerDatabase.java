package com.igr.camscanner.com.igr.camscanner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DEVEN SINGH on 1/22/2015.
 */
public class CamScannerDatabase extends SQLiteOpenHelper{

    Context context;
    public static final String DATABASE_NAME = "M_CamScanner.db";

    public CamScannerDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table images " +
                        "(id integer primary key, name text,tag text, phone2 text,email text,email2 text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS images");
        onCreate(db);
    }
}
