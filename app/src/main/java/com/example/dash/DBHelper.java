package com.example.dash;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    static final String Username = "";
    static final String Password = "";
    static final String DB_NAME = "MainFrame";
    static final int DB_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    private static final String CREATE_TABLE= "create table if not exists " + "CustomersList" +
            "("
            + "Name" +" TEXT NOT NULL,"
            + "PhoneNumber" + " STRING PRIMARY KEY NOT NULL CHECK (length(PhoneNumber)==10),"
            + "DateofBirth" + " STRING NOT NULL,"
            + "Points" + "INTEGER"
            + ");";

    private static final String CREATE_TABLE_2 = "create table if not exists " + "Inventory" +
            "("
            + "Barcode" + " STRING PRIMARY KEY NOT NULL,"
            + "Purity" + " STRING NOT NULL,"
            + "Wastage" + " DECIMAL(2,2),"
            + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
            + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
            + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
            + "ExtraCharges" + " INTEGER,"
            + "HUID" + " STRING,"
            + "SupplierCode" + " INTEGER"
            + ");" ;



    private static final String CREATE_TABLE_CONTROLS = "create table if not exists " + "Controls" +
            "("
            + "PointThreshold" + " INTEGER NOT NULL CHECK (PointThreshold>=100)"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_CONTROLS);
        db.execSQL(CREATE_TABLE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "CustomersList");
        db.execSQL("DROP TABLE IF EXISTS " + "Controls");
        db.execSQL("DROP TABLE IF EXISTS " + "Inventory");
        onCreate(db);
    }
}
