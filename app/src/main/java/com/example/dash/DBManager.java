package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

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


    public void insertNewCustomer(String Name, String PhoneNumber, String DOB) {
        Log.d("SQL","insert");
        String sql = "INSERT INTO Customers (Name, PhoneNumber, DOB) VALUES (?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(0, Name);
        statement.bindString(1,PhoneNumber);
        statement.bindString(2, DOB);
        statement.executeInsert();
        close();
    }

    public void changePointThreshold(int newthreshold){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PointThreshold",newthreshold);
        db.insert("Controls", null, contentValues);
        db.close();
    }

    public void addPoints(String Name, String PhoneNumber, int cashflow){
        int initialPoints,pointsToAdd;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Points FROM Customers WHERE Name ='" + Name + "' AND PhoneNumber='" + PhoneNumber +"'";
        String sql1 = "SELECT PointThreshold FROM Controls";
        try  {
            Cursor pointCursor = db.rawQuery(sql, null);
            if(pointCursor.moveToFirst()){
                if(pointCursor.getInt(0)!=0){
                    Cursor pthCursor = db.rawQuery(sql1, null);
                    GlobalPointTH = pthCursor.getInt(0);
                    pointsToAdd = cashflow/GlobalPointTH;
                    initialPoints = pointCursor.getInt(0);
                    pointsToAdd = initialPoints+pointsToAdd;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Points",pointsToAdd);
                    db.insert("Customers",null,contentValues);
                }else{
                    //cashFlow divide by point decimal.
                    //Add the result to
                }
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }

    }

    public void usePoints(String Name, String PhoneNumber, int points){

    }

}
