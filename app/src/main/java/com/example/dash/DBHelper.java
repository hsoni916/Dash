package com.example.dash;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    static final String Username = "";
    static final String Password = "";
    static final String DB_NAME = "Customers";
    static final int DB_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    private static final String CREATE_TABLE= "create table if not exists " + "Customers" +
            "("
            + "Name" +" TEXT NOT NULL,"
            + "PhoneNumber" + "STRING NOT NULL,"
            + "DateofBirth" + "STRING NOT NULL,"
            + "Points" + "INTEGER"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "Customers");
        onCreate(db);
    }
}
