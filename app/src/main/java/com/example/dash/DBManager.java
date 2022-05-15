package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.nio.charset.StandardCharsets;
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
        String sql = "SELECT Name FROM CustomersList";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> NamesOfCustomer = new ArrayList<>();
        while(fetch.moveToNext()){
            NamesOfCustomer.add(fetch.getString(0));
        }
        return NamesOfCustomer;
        //run a loop to return all the names and phone numbers to the popup.
    }

    public List<String> ListAllPhone() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT PhoneNumber FROM CustomersList";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> PhoneOfCustomer = new ArrayList<>();
        while(fetch.moveToNext()){
            PhoneOfCustomer.add(fetch.getString(0));
        }
        return PhoneOfCustomer;
    }

    public long insertAllCategoriesGold(List<String> Categories){

        long result = -1;
        ContentValues contentValues = new ContentValues();
        String sql = "SELECT Category FROM Categories";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor fetch = db.rawQuery(sql,null);
             String delete_statement= "DELETE FROM Categories";
             Cursor delete_execute = db.rawQuery(delete_statement,null);
            for(int j=0;j<Categories.size();j++){
                contentValues.put("Category",Categories.get(j));
                if(Categories.get(j).contains("Silver") || Categories.get(j).contains("silver")){
                    contentValues.put("BaseMetal","Silver");
                }else{
                    contentValues.put("BaseMetal","Gold");
                }
                result = result+db.insert("Categories", null,contentValues);
                String label = Categories.get(j).replace(" ","_");
                String table_name = label.replace("-","_");
                String create_table = "create table if not exists " + table_name +
                        "("
                        + "Barcode" + " STRING,"
                        + "Purity" + " STRING NOT NULL,"
                        + "Wastage" + " DECIMAL(2,2),"
                        + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
                        + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
                        + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
                        + "ExtraCharges" + " INTEGER,"
                        + "HUID" + " STRING,"
                        + "SupplierCode" + " INTEGER" +
                        ");";
                String table_name2 = table_name+"_uid";
                String create_table_backup = "create table if not exists " + table_name2 +
                        "(" + "Barcode" + " STRING NOT NULL" + ");";
                dbHelper.getWritableDatabase().execSQL(create_table_backup);
                String triggerName = table_name+"_barcode_trigger";
                String triggerName2 = table_name+"_rowid_trigger";
                String prefix = "MJ"+Categories.get(j).substring(0,2).replace("-","").toUpperCase();
                String prefixbuildersql = "SELECT Category_Code FROM Categories WHERE Category = '" +Categories.get(j)+ "'";
                Cursor PFB = db.rawQuery(prefixbuildersql,null);
                while (PFB.moveToNext()){
                    Log.d("Prefix:","MJ"+PFB.getInt(0));
                }
                String triggers = "CREATE TRIGGER IF NOT EXISTS " + triggerName + " AFTER INSERT ON "
                        + table_name + " WHEN new.Barcode IS null " + " BEGIN UPDATE " + table_name + " SET Barcode = '"+prefix+"'||rowid; END;";
                Log.d("Triggers:",triggers);
                String trigger2 = "CREATE TRIGGER IF NOT EXISTS " + triggerName2 + " AFTER DELETE ON "
                        + table_name +" BEGIN INSERT INTO " + table_name2 + " (Barcode) " + " VALUES ( old.Barcode ); END;";
                dbHelper.getWritableDatabase().execSQL(create_table);
                dbHelper.getWritableDatabase().execSQL(triggers);
                dbHelper.getWritableDatabase().execSQL(trigger2);
            }
        return result;
    }

    public long insertAllCategoriesSilver(List<String> Categories){
        long result = -1;
        ContentValues contentValues = new ContentValues();
        String sql = "SELECT Category FROM Categories_Silver";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor fetch = db.rawQuery(sql,null);
        String delete_statement= "DELETE FROM Categories_Silver";
        Cursor delete_execute = db.rawQuery(delete_statement,null);
        for(int j=0;j<Categories.size();j++){
            contentValues.put("Category",Categories.get(j));
            result = result+db.insert("Categories_Silver", null,contentValues);
            String label = Categories.get(j).replace(" ","_");
            String table_name = label.replace("-","_")+"Silver";
            String create_table = "create table if not exists " + table_name +
                    "("
                    + "Barcode" + " INTEGER,"
                    + "Purity" + " STRING NOT NULL,"
                    + "Wastage" + " DECIMAL(2,2),"
                    + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
                    + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
                    + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
                    + "ExtraCharges" + " INTEGER,"
                    + "HUID" + " STRING,"
                    + "SupplierCode" + " INTEGER" + ");";
            dbHelper.getWritableDatabase().execSQL(create_table);
        }
        return result;
    }

    public long insertCategorySilver(String Category, boolean b){
        long result = -1;
        String table_name;
        ContentValues contentValues = new ContentValues();
        String sql = "SELECT Category FROM Categories_Silver";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor fetch = db.rawQuery(sql,null);
        while(fetch.moveToNext() && fetch.isAfterLast()){
            if(!fetch.getString(0).equals(Category)){
                contentValues.put("Category",Category);
                if(b){
                    result = result+db.insert("Categories_Gold", null,contentValues);
                    table_name = Category+"_"+"Gold";
                }else{
                    result = result+db.insert("Categories_Silver", null,contentValues);
                    table_name = Category+"_"+"Silver";
                }
                String create_table = "create table if not exists" + table_name +
                        "("
                        + "Barcode" + " INTEGER,"
                        + "Purity" + " STRING NOT NULL,"
                        + "Wastage" + " DECIMAL(2,2),"
                        + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
                        + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
                        + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
                        + "ExtraCharges" + " INTEGER,"
                        + "HUID" + " STRING,"
                        + "SupplierCode" + " INTEGER" + ");";
                dbHelper.getWritableDatabase().execSQL(create_table);
            }
        }
        return result;
    }



    public List<String> ListDOB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT DateOfBirth FROM CustomersList";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> DOBofCustomer = new ArrayList<>();
        while(fetch.moveToNext()){
            DOBofCustomer.add(fetch.getString(0));
        }
        return DOBofCustomer;
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

    public List<String> ListAllItems() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Barcode FROM Inventory";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> BarcodeList = new ArrayList<>();
        while(fetch.moveToNext()){
            BarcodeList.add(fetch.getString(0));
        }
        return BarcodeList;
    }

    public List<String> ListGenericItems() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Category FROM Categories_Gold";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> GenericItemList = new ArrayList<>();
        while(fetch.moveToNext()){
            GenericItemList.add(fetch.getString(0));
        }
        sql = "SELECT Category FROM Categories_Silver";
        fetch = db.rawQuery(sql,null);
        while(fetch.moveToNext()){
            GenericItemList.add(fetch.getString(0));
        }
        return GenericItemList;
    }

    public List<Integer> ListGenericItemCodes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Category FROM Categories";
        Cursor fetch = db.rawQuery(sql,null);
        List<Integer> GenericItemCodeList = new ArrayList<>();
        while(fetch.moveToFirst() && fetch.moveToNext() && !fetch.isAfterLast()){
            GenericItemCodeList.add(fetch.getInt(1));
        }
        return GenericItemCodeList;
    }

    public List<String> ListAllSuppliers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT Business_Name FROM Supplier";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> SupplierList = new ArrayList<>();
        while(fetch.moveToFirst() && fetch.moveToNext() && !fetch.isAfterLast()){
            SupplierList.add("(" + fetch.getInt(3)+ ")" + " " +fetch.getString(0));
        }
        return SupplierList;
    }

    public long AddNewSupplier(String Supplier_Name) {
        long result = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Business_Name",Supplier_Name);
        result = db.insert("Supplier", null, contentValues);
        return result;
    }


    public void AddStandards() {
        long result = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Standard_Name","999 Fine Gold");
        contentValues.put("Standard_Value",99.9);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","23KT958");
        contentValues.put("Standard_Value",95.8);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","22KT916");
        contentValues.put("Standard_Value",91.6);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","21KT875");
        contentValues.put("Standard_Value",87.5);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","20KT833");
        contentValues.put("Standard_Value",83.3);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","18KT750");
        contentValues.put("Standard_Value",75.0);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","14KT585");
        contentValues.put("Standard_Value",58.5);
        db.insert("Standards", null, contentValues);

        //"Kachhi Silver", "Zevar Silver", "D-Silver", "Rupa"
        contentValues.put("Standard_Name","925");
        contentValues.put("Standard_Value",92.5);
        db.insert("Standards", null, contentValues);

        contentValues.put("Standard_Name","Kachhi Silver");
        contentValues.put("Standard_Value",78.0);
        db.insert("Standards", null, contentValues);

    }

    public long AddStandard(String Label, int Purity){
        long result = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Standard_Name",Label);
        contentValues.put("Standard_Value",Purity);
        result = db.insert("Standards", null, contentValues);
        return result;
    }
}
