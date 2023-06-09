package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    static final String Username = "";
    static final String Password = "";
    static final String DB_NAME = "MainFrame";
    static final int DB_VERSION = 6;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    private static final String CREATE_TABLE_MASTER = "create table if not exists " + "Invoice_Counter" +
            "(" + "Month" + " INTEGER NOT NULL,"
            + "Year" + " INTEGER NOT NULL,"
            + "Counter" + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    private static final String CREATE_TABLE= "create table if not exists " + "CustomersList" +
            "(" + "Name" +" TEXT NOT NULL,"
            + "PhoneNumber" + " STRING PRIMARY KEY NOT NULL CHECK (length(PhoneNumber)==10),"
            + "DateofBirth" + " STRING NOT NULL,"
            + "Address" + " STRING,"
            + "Points" + " INTEGER,"
            + "Reference" + " TEXT,"
            + "AccountCreatedOn" + " TEXT NOT NULL,"
            + "LastActiveOn" + " TEXT NOT NULL"
            + ");";

    private static final String CREATE_TABLE_2 = "create table if not exists " + "Inventory" +
            "(" + "Barcode" + " STRING,"
            + "Name" + " STRING NOT NULL,"
            + "Purity" + " STRING NOT NULL,"
            + "Wastage" + " DECIMAL(2,2),"
            + "ExtraCharges" + " INTEGER,"
            + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
            + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
            + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
            + "HUID" + " STRING,"
            + "DateofEntry" + " STRING NOT NULL,"
            + "Supplier" + " STRING"
            + ");";

    private static final String CREATE_TABLE_3 = "create table if not exists " + "Supplier" +
            "(" + "Business_Name" + " STRING,"
            + "Person_In_Charge" + " STRING,"
            + "PhoneNumber" + " STRING CHECK (length(PhoneNumber)==10),"
            + "GSTIN" + " STRING CHECK (length(GSTIN)==15),"
            + "City" + " STRING,"
            + "Categories" + " STRING,"
            + "SupplierCode" + " INTEGER PRIMARY KEY" + ");";
    //GST, Categories.

    private static final String CREATE_TABLE_3_1 = "create table if not exists " + "Sundry_Supplies"
            + "("
            + "Name" + " STRING NOT NULL,"
            + "Date_Of" + " STRING NOT NULL,"
            + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
            + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
            + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
            + "TypeOfArticle" + " STRING NOT NULL,"
            + "Wastage" + " DECIMAL(3,3) NOT NULL,"
            + "ExtraCharges" + " INTEGER,"
            + "ADD_INFO" + " STRING,"
            + "Status" + " INTEGER NOT NULL DEFAULT 0 CHECK ( Status IN (-1,0,1))"
            + ");";

    private static final String CREATE_TABLE_4_2 = "create table if not exists " + "Standards"
            + "("
            + "Standard_Name" + " STRING NOT NULL,"
            + "Standard_Value" + " DECIMAL(4,2) NOT NULL"
            + ");";

    private static final String CREATE_TABLE_4 = "create table if not exists " + "Categories"
            + "("
            + "Category" + " STRING NOT NULL,"
            + "BaseMetal" + " STRING NOT NULL,"
            + "Base_Purity" + " DECIMAL(4,2),"
            + "Category_Code" + " INTEGER PRIMARY KEY NOT NULL"
            + ");";

    private static final String CREATE_TABLE_CONTROLS = "create table if not exists " + "Controls" +
            "(" + "PointThreshold" + " INTEGER NOT NULL CHECK (PointThreshold>=100)" + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MASTER);
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_CONTROLS);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
        db.execSQL(CREATE_TABLE_3_1);
        db.execSQL(CREATE_TABLE_4);
        db.execSQL(CREATE_TABLE_4_2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CustomersList");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Standards");
        db.execSQL("DROP TABLE IF EXISTS Controls");
        db.execSQL("DROP TABLE IF EXISTS Inventory");
        db.execSQL("DROP TABLE IF EXISTS Supplier");
        db.execSQL("DROP TABLE IF EXISTS Sundry_Supplies");
        db.execSQL("DROP TABLE IF EXISTS Invoice_counter");
        onCreate(db);
    }
}
