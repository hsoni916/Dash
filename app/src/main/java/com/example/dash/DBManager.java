package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    int GlobalPointTH = 0;
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


    public long insertNewCustomer(String Name, String PhoneNumber, String DOB) {
        long result = -1;
        Log.d("SQL","insert");
        String sql = "INSERT INTO CustomersList (Name, PhoneNumber, DateofBirth) VALUES (?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, Name);
        statement.bindString(2,PhoneNumber);
        statement.bindString(3, DOB);
        try{
            result = statement.executeInsert();
        }catch (Exception e){
            e.fillInStackTrace();
        }

        close();
        return result;
    }

    public List<String> ListAllCustomer(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT * FROM CustomersList";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> NamesOfCustomer = new ArrayList<>();
        while(fetch.moveToNext()){
            NamesOfCustomer.add(fetch.getString(0));
        }
        return NamesOfCustomer;
        //run a loop to return all the names and phone numbers to the popup.
    }

    public long changePointThreshold(int newthreshold){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PointThreshold",newthreshold);
        long result = db.insert("Controls", null, contentValues);
        db.close();
        return result;
    }

    public long addPoints(String Name, String PhoneNumber, int cashflow){
        long result = -1;
        int initialPoints,pointsToAdd;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Points FROM CustomersList WHERE Name ='" + Name + "' AND PhoneNumber='" + PhoneNumber +"'";
        String sql1 = "SELECT PointThreshold FROM Controls";
        try  {
            Cursor pointCursor = db.rawQuery(sql, null);
            Cursor pthCursor = db.rawQuery(sql1, null);
            if(pointCursor.moveToFirst()){
                if(pthCursor.moveToFirst() && pthCursor.getInt(0)!=0){
                    GlobalPointTH = pthCursor.getInt(0);
                    pointsToAdd = cashflow/GlobalPointTH;
                    initialPoints = pointCursor.getInt(0);
                    pointsToAdd = initialPoints+pointsToAdd;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Points",pointsToAdd);
                    result = db.insert("Customers",null,contentValues);
                }else{
                    Log.d("Add Points","Error.");
                    return result;
                }
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return result;
    }

    public long usePoints(String Name, String PhoneNumber, int pointsToUse){
        long result = -1;
        int initialPoints;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Points FROM CustomersList WHERE Name ='" + Name + "' AND PhoneNumber='" + PhoneNumber +"'";
        Cursor pointCursor = db.rawQuery(sql, null);
        if(pointCursor.moveToFirst()){
            initialPoints = pointCursor.getInt(0);
            initialPoints = initialPoints - pointsToUse;
            ContentValues contentValues = new ContentValues();
            contentValues.put("Points",initialPoints);
            result = db.insert("Customers",null,contentValues);
        }
        return result;
    }
}
