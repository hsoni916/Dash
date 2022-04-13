package com.example.dash;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBManager {

    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;
    public static int Column_Count;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public void insert(String Name, String PhoneNumber, String DOB) {
        Log.d("SQL","insert");
        String sql = "INSERT INTO Customer (Name, PhoneNumber, DOB) VALUES (?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(0, Name);
        statement.bindString(1,PhoneNumber);
        statement.bindString(2, DOB);
        statement.executeInsert();
        close();
    }

}
