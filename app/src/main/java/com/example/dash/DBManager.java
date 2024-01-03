package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBManager {
    int GlobalPointTH = 0;
    private DBHelper dbHelper;

    private final Context context;

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
                        + "DateofEntry" + " STRING NOT NULL,"
                        + "SupplierCode" + " INTEGER" + ");";
                dbHelper.getWritableDatabase().execSQL(create_table);
            }
        }
        fetch.close();
        return result;
    }



    public List<String> ListDOB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT DateOfBirth FROM CustomersList";
        Cursor fetch = db.rawQuery(sql,null);
        List<String> DOBofCustomer = new ArrayList<>();
        while(fetch.moveToNext()){
           // Log.d("Birthdays",fetch.getString(0));
            DOBofCustomer.add(fetch.getString(0));
        }
        fetch.close();
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
            pthCursor.close();
            pointCursor.close();
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
        pointCursor.close();
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
        fetch.close();
        Log.d("Barcodes", BarcodeList.toString());
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
        fetch.close();
        fetch = db.rawQuery(sql,null);
        while(fetch.moveToNext()){
            GenericItemList.add(fetch.getString(0));
        }
        fetch.close();
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
        fetch.close();
        return GenericItemCodeList;
    }



    public long AddNewSupplier(String Supplier_Name) {
        long result;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Business_Name",Supplier_Name);
        result = db.insert("Supplier", null, contentValues);
        return result;
    }







    public void updateSupplier(Supplier supplierNew, int supplier_Position) {

    }

    //Initial setup to be called in part One.
        //ADD CURRENT STANDARDS
            public void AddStandards() {
        long result = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //check if the table exists.
            // -> check if contains all the standards.
                // -> add missing standards.


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

        //BASIC GOLD CATEGORIES ADDED
            public long insertAllCategoriesGold(List<String> Categories){
                long result = -1;
                ContentValues contentValues = new ContentValues();
                String sql = "SELECT Category FROM Categories";
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor fetch = db.rawQuery(sql,null);
                //Log.d("Cursor Count",String.valueOf(fetch.getCount()));
                if(fetch.getCount()==0){
                    for(int j=0;j<Categories.size();j++){
                        contentValues.put("Category",Categories.get(j));
                        contentValues.put("BaseMetal","Gold");
                        result = result+db.insert("Categories", null,contentValues);
                    }
                }
                fetch.close();
                for(int j=0;j<Categories.size();j++){
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
                            + "DateofEntry" + " STRING NOT NULL,"
                            + "SupplierCode" + " INTEGER" +
                            ");";
                    String indexname = table_name + "_index";
                    String create_index = "create UNIQUE INDEX IF NOT EXISTS " + indexname +
                            " ON "
                            + table_name
                            + " ( BarCode ASC )" +
                            ";";
                    dbHelper.getWritableDatabase().execSQL(create_index);
                    String table_name2 = table_name+"_uid";
                    String create_table_backup = "create table if not exists " + table_name2 + "(" + "Barcode" + " STRING NOT NULL" + ");";
                    dbHelper.getWritableDatabase().execSQL(create_table_backup);
                    String triggerName = table_name+"_barcode_trigger";
                    String triggerName2 = table_name+"_rowid_trigger";
                    StringBuilder prefix = new StringBuilder("MJ" + Categories.get(j).substring(0, 2).replace("-", "").toUpperCase());
                    String prefixbuildersql = "SELECT Category_Code FROM Categories WHERE Category = '" +Categories.get(j)+ "'";
                    Cursor PFB = db.rawQuery(prefixbuildersql,null);
                    while (PFB.moveToNext()){
                        prefix.append(PFB.getInt(0));
                        prefix.append(".");
                        Log.d("Prefix:","MJ"+PFB.getInt(0));
                    }
                    PFB.close();
                    String triggers = "CREATE TRIGGER IF NOT EXISTS " + triggerName + " AFTER INSERT ON "
                            + table_name + " WHEN new.Barcode IS null " + " BEGIN UPDATE " + table_name + " SET Barcode = '"+prefix+"'||rowid; END;";
                    //Log.d("Triggers:",triggers);
                    String trigger2 = "CREATE TRIGGER IF NOT EXISTS " + triggerName2 + " AFTER DELETE ON "
                            + table_name +" BEGIN INSERT INTO " + table_name2 + " (Barcode) " + " VALUES ( old.Barcode ); END;";
                    dbHelper.getWritableDatabase().execSQL(create_table);
                    dbHelper.getWritableDatabase().execSQL(triggers);
                    dbHelper.getWritableDatabase().execSQL(trigger2);
                }
                return result;
        }

        //BASIC SILVER CATEGORIES ADDED
            public long insertAllCategoriesSilver(List<String> Categories){
            long result = -1;
            ContentValues contentValues = new ContentValues();
            String sql = "SELECT Category FROM Categories";
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor fetch = db.rawQuery(sql,null);
            Log.d("Cursor Count",String.valueOf(fetch.getCount()));
            if(fetch.getCount()==0){
                for(int j=0;j<Categories.size();j++){
                    contentValues.put("Category",Categories.get(j));
                    contentValues.put("BaseMetal","Silver");
                    result = result+db.insert("Categories", null,contentValues);
                }
            }
            fetch.close();
            for(int j=0;j<Categories.size();j++){
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
                        + "DateofEntry" + " STRING NOT NULL,"
                        + "SupplierCode" + " INTEGER" +
                        ");";
                String indexname = table_name + "_index";
                String create_index = "create UNIQUE INDEX IF NOT EXISTS " + indexname +
                        " ON "
                        + table_name
                        + " ( BarCode ASC )" +
                        ";";
                dbHelper.getWritableDatabase().execSQL(create_index);
                String table_name2 = table_name+"_uid";
                String create_table_backup = "create table if not exists " + table_name2 + "(" + "Barcode" + " STRING NOT NULL" + ");";
                dbHelper.getWritableDatabase().execSQL(create_table_backup);
                String triggerName = table_name+"_barcode_trigger";
                String triggerName2 = table_name+"_rowid_trigger";
                StringBuilder prefix = new StringBuilder("MJ" + Categories.get(j).substring(0, 2).replace("-", "").toUpperCase());
                String prefixbuildersql = "SELECT Category_Code FROM Categories WHERE Category = '" +Categories.get(j)+ "'";
                Cursor PFB = db.rawQuery(prefixbuildersql,null);
                while (PFB.moveToNext()){
                    prefix.append(PFB.getInt(0));
                    prefix.append(".");
                    Log.d("Prefix:","MJ"+PFB.getInt(0));
                }
                PFB.close();
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

    //Additional methods for adding data on the go in part Two.
        //ADD NEW STANDARD
            public long AddStandard(String Label, int Purity){
                long result;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("Standard_Name",Label);
                contentValues.put("Standard_Value",Purity);
                result = db.insert("Standards", null, contentValues);
                return result;
            }

        //ADD NEW CUSTOMER
            public long insertNewCustomer(String Name, String PhoneNumber, String DOB, String city) {
            long result = -1;
            Log.d("SQL","insert");
                Log.d("DOB",DOB);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date c = Calendar.getInstance().getTime();
            Date today = new Date();
            try{
                today= dateFormat.parse(dateFormat.format(c));
            }catch (Exception e){
                e.printStackTrace();
            }
            String AddNewCustomer = "INSERT INTO CustomersList (Name, PhoneNumber, DateofBirth, AccountCreatedOn, LastActiveOn) VALUES (?, ?, ?, ?, ?)";
            SQLiteStatement statement = database.compileStatement(AddNewCustomer);
            statement.bindString(1, Name);
            statement.bindString(2,PhoneNumber);
            statement.bindString(3, DOB);
            statement.bindString(4, String.valueOf(today));
            statement.bindString(5, String.valueOf(today));
            try{
                result = statement.executeInsert();
                String tableName = Name+"_"+PhoneNumber;
                tableName = tableName.replaceAll(" ","");
                String CustomerAccount = "create table if not exists " + tableName +
                        "(" + "Date" +" TEXT NOT NULL,"
                        + "Narration" + " TEXT NOT NULL,"
                        + "Remarks" + " TEXT,"
                        + "Amount" + " INTEGER,"
                        + "Metal" + " INTEGER"
                        + ");";
                dbHelper.getWritableDatabase().execSQL(CustomerAccount);
            }catch (Exception e){
                e.fillInStackTrace();
            }
            close();

            return result;
        }

        public long quickInsertNewCustomer(String Name, String PhoneNumber){
            long result = -1;
            Log.d("SQL","insert");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            Date c = Calendar.getInstance().getTime();
            Date today = new Date();
            try{
                today= dateFormat.parse(dateFormat.format(c));
            }catch (Exception e){
                e.printStackTrace();
            }
            String AddNewCustomer = "INSERT INTO CustomersList (Name, PhoneNumber, DateofBirth, AccountCreatedOn, LastActiveOn) VALUES (?, ?, ?, ?, ?)";
            SQLiteStatement statement = database.compileStatement(AddNewCustomer);
            statement.bindString(1, Name);
            statement.bindString(2, PhoneNumber);
            statement.bindString(3, "");
            statement.bindString(4, String.valueOf(today));
            statement.bindString(5, String.valueOf(today));
            try{
                result = statement.executeInsert();
                Log.d("Quick Insert:",String.valueOf(result));
                String tableName = Name+"_"+PhoneNumber;
                tableName = tableName.replaceAll(" ","");
                String CustomerAccount = "create table if not exists " + tableName +
                        "(" + "Date" +" TEXT NOT NULL,"
                        + "Narration" + " TEXT NOT NULL,"
                        + "Remarks" + " TEXT,"
                        + "Amount" + " INTEGER,"
                        + "Metal" + " INTEGER"
                        + ");";
                dbHelper.getWritableDatabase().execSQL(CustomerAccount);
            }catch (Exception e){
                e.fillInStackTrace();
                e.printStackTrace();
            }
            close();
            Log.d("Quick Insert:",String.valueOf(result));
            return result;
        }


        //ADD NEW SUPPLIER
            public long insertNewSupplier(String Business, String Owner, String GSTIN,
                                          String City, String Phone, List<String> categories){
            long result = -1;
            String AddNewSupplier = "INSERT INTO Suppliers (Business_Name, Person_In_Charge, PhoneNumber, GSTIN, City, Categories) VALUES (?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = database.compileStatement(AddNewSupplier);
            statement.bindString(1, Business);
            statement.bindString(2, Owner);
            statement.bindString(3, GSTIN);
            statement.bindString(4, City);
            statement.bindString(5, Phone);
            statement.bindString(6, String.valueOf(categories));
            try{
                result = statement.executeInsert();
            }catch (Exception e){
                e.fillInStackTrace();
            }
            close();
            return result;
        }

        //LIST ALL SUPPLIERS
            public List<String> ListAllSuppliers() {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "SELECT Business_Name FROM Supplier";
            Cursor fetch = db.rawQuery(sql,null);
            List<String> SupplierList = new ArrayList<>();
            while(fetch.moveToFirst() && fetch.moveToNext() && !fetch.isAfterLast()){
                SupplierList.add("(" + fetch.getInt(3)+ ")" + " " +fetch.getString(0));
            }
            fetch.close();
            return SupplierList;
        }

        //LIST ALL CUSTOMER
        public List<String> ListAllCustomer(){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "SELECT Name FROM CustomersList";
            Cursor fetch = db.rawQuery(sql,null);
            List<String> NamesOfCustomer = new ArrayList<>();
            while(fetch.moveToNext()){
                NamesOfCustomer.add(fetch.getString(0));
            }
            fetch.close();
            return NamesOfCustomer;
        }

        //LIST ALL PHONENUMBERS OF CUSTOMER
            public List<String> ListAllPhone() {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "SELECT PhoneNumber FROM CustomersList";
            Cursor fetch = db.rawQuery(sql,null);
            List<String> PhoneOfCustomer = new ArrayList<>();
            while(fetch.moveToNext()){
                PhoneOfCustomer.add(fetch.getString(0));
            }
            fetch.close();
            return PhoneOfCustomer;
        }

        //List Name and Phone numbers
            public List<Customer> getCustomerDetails() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "SELECT Name, PhoneNumber FROM CustomersList";
                Cursor fetch = db.rawQuery(sql,null);
                List<Customer> CustomerDetails = new ArrayList<>();
                while(fetch.moveToNext()){
                    Customer customer = new Customer();
                    customer.setName(fetch.getString(0));
                    customer.setPhoneNumber(fetch.getString(1));
                    CustomerDetails.add(customer);
                }
                fetch.close();
                return CustomerDetails;
            }

        public List<Customer> GetCustomerProfile() throws ParseException {
        //Name Phone DOB Address
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "SELECT * FROM CustomersList";
            Cursor fetch = db.rawQuery(sql,null);
            List<Customer> Customers = new ArrayList<>();
            while(fetch.moveToNext()){
                Customer customer = new Customer();
                customer.setName(fetch.getString(0));
                customer.setPhoneNumber(fetch.getString(1));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                Log.d("DOB0",fetch.getString(2));
                if(fetch.getString(2).isEmpty()){
                    customer.setDateOfBirth("");
                    Log.d("DOB1","Empty Date");
                }else{
                    customer.setDateOfBirth(fetch.getString(2));
                    Log.d("DOB2","Date found");
                }
                customer.setAddress(fetch.getString(3));
                Customers.add(customer);
            }
            fetch.close();
            return Customers;
        }

        //GET BARCODE OF AN ITEM
            public String getBarCode(long rowid, String s){
                String returnvalue = "";
                Log.d("Table:",s);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "SELECT Barcode FROM " + s + " WHERE rowid ='" + rowid + "'";
                Cursor fetch = db.rawQuery(sql, null);
                while (fetch.moveToNext()){
                    returnvalue = fetch.getString(0);
                    Log.d("Fetch", fetch.getString(0));
                }
                fetch.close();
                return returnvalue;
            }

            public boolean deleteBarCode(Label label, String Category){
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Category = Category.replaceAll(" ", "_");
                Category = Category.replaceAll("-","_");
                //String sql = "SELECT Category_Code FROM Categories WHERE Category ='" + Category + "'";
                //Cursor fetch = db.rawQuery(sql, null);
                int result =db.delete(Category,"Barcode=?",new String[]{label.getBarcode()});
                Log.d("Delete Result", String.valueOf(result));
                return result>0;
            }

        //ADDING A COUNTER FOR NUMBER OF BILLS IN SPECIFIC MONTH-YEAR
            public long addCounter(int month, int year){
                long result = -1;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "SELECT Month FROM Invoice_Counter WHERE Month ='" + month + "' AND Year='" + year +"'";
                Cursor fetch = db.rawQuery(sql,null);
                if(!fetch.moveToNext()){
                    String addNewCounter = "INSERT INTO Invoice_Counter (Month, Year, Counter) VALUES (?, ?, ?)";
                    SQLiteStatement statement = database.compileStatement(addNewCounter);
                    statement.bindDouble(1, month);
                    statement.bindDouble(2, year);
                    statement.bindDouble(3, 0);
                    try{
                        result = statement.executeInsert();
                    }catch (Exception e){
                        e.fillInStackTrace();
                    }
                }
                fetch.close();
                return result;
            }

        //GET CURRENT NUMBER OF BILLS IN SPECIFIC MONTH-YEAR
            public int getCounter(int month, int year){
            int c = 0;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "SELECT Counter FROM Invoice_Counter WHERE Month ='" + month + "' AND Year='" + year +"'";
            Cursor fetch = db.rawQuery(sql,null);
            if(fetch.moveToFirst()){
                c = fetch.getInt(0);
            }
            fetch.close();
            return c;
        }

        //STORE NUMBER OF BILLS IN SPECIFIC MONTH-YEAR
            public long updateCounter(int month, int year, int D){
            long result;

            ContentValues cv = new ContentValues();
            cv.put("Counter",D);
            result = database.update("Invoice_Counter",cv,"Month =" + month +" AND "+ "Year=" + year,null);

                /*String updateCounter = "INSERT INTO Invoice_Counter (Counter) VALUES (?) WHERE Month ='" + month + "' AND Year='" + year +"'";
            SQLiteStatement statement = database.compileStatement(updateCounter);
            statement.bindDouble(1, D);
            try{
                result = statement.executeInsert();
            }catch (Exception e){
                e.fillInStackTrace();
            }*/
            close();
            return result;
        }

    public List<Label> ListAllItems(String filterString) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        filterString = filterString.replaceAll(" ","_");
        filterString = filterString.replaceAll("-","_");
        String sql = "SELECT * FROM '" + filterString.replaceAll(" ","_") + "'";
        Cursor fetch = db.rawQuery(sql,null);
        List<Label> InventoryList = new ArrayList<>();
        while(fetch.moveToNext()){
            Label newLabel = new Label();
            fetch.getColumnIndex("Wastage");
            newLabel.setName(filterString);
            newLabel.setBarcode(fetch.getString(0));
            newLabel.setHMStandard(fetch.getString(1));
            newLabel.setPurity(fetch.getDouble(2));
            newLabel.setGW(fetch.getString(3));
            newLabel.setLW(fetch.getString(4));
            newLabel.setNW(fetch.getString(5));
            newLabel.setEC(fetch.getString(6));
            newLabel.setHUID(fetch.getString(7));
            newLabel.setDate(fetch.getString(8));
            InventoryList.add(newLabel);
        }
        fetch.close();
        return InventoryList;
    }

    public long addRecord(String name, String phone, double weight, double amount, String remarks, String transactionDate, String narration) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        Date c = Calendar.getInstance().getTime();
        Date today = new Date();
        long result = -1;
        try{
            today= dateFormat.parse(dateFormat.format(c));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(transactionDate.isEmpty()){
            transactionDate = String.valueOf(today);
        }
        String tablename = name +"_"+ phone;
        tablename = tablename.replaceAll(" ","");
        String statement1 = "INSERT INTO " + tablename + "(Date, Narration, Remarks, Amount, Metal) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(statement1);
        statement.bindString(1, transactionDate);
        statement.bindString(2, narration);
        statement.bindString(3, remarks);
        statement.bindDouble(4, amount);
        statement.bindDouble(5, weight);
        try{
            result = statement.executeInsert();
        }catch (Exception e){
            e.fillInStackTrace();
        }
        close();

        return result;

    }

    public double getBalance(String name, String phoneNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tablename = name+"_"+phoneNumber;
        tablename =tablename.replaceAll(" ","");
        String sql = "SELECT * FROM "+ tablename;
        Cursor fetch = db.rawQuery(sql,null);
        double balance = 0;
        while(fetch.moveToNext()){
            int colIndex = fetch.getColumnIndex("Amount");
            balance = balance + fetch.getDouble(colIndex);
        }
        fetch.close();
        return balance;
    }

    public double getTouch(String name, String barcode) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tablename = name.replaceAll(" ","_");
        String sql = "SELECT Wastage FROM "+ tablename +" WHERE Barcode='" + barcode + "'";
        //SELECT Wastage FROM Mens_Ring WHERE Barcode=MJME7.1;
        Cursor fetch = db.rawQuery(sql,null);
        double balance = 0;
        while (fetch.moveToNext()){
            balance = fetch.getDouble(0);
        }
        fetch.close();
        return balance;

    }

    public double getMetalBalance(String name, String phoneNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tablename = name+"_"+phoneNumber;
        tablename =tablename.replaceAll(" ","");
        String sql = "SELECT * FROM "+ tablename;
        Cursor fetch = db.rawQuery(sql,null);
        double balance = 0;
        while(fetch.moveToNext()){
            int colIndex = fetch.getColumnIndex("Metal");
            balance = balance + fetch.getDouble(colIndex);
        }
        fetch.close();
        return balance;
    }

    public List<String> getAllBarcodes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String get_categories = "SELECT Category FROM Categories";
        Cursor fetch = db.rawQuery(get_categories,null);
        List<String> category_list = new ArrayList<>();
        while(fetch.moveToNext()){
            int columnIndex = fetch.getColumnIndex("Category");
            String boiler = fetch.getString(columnIndex).replaceAll(" ","_");
            boiler = boiler.replaceAll("-","_");
            category_list.add(boiler);
        }
        List<String> Barcodes = new ArrayList<>();
        for(String category0 : category_list){
            get_categories = "SELECT Barcode FROM " + category0;
            fetch = db.rawQuery(get_categories,null);
            while (fetch.moveToNext()){
                int columnIndex = fetch.getColumnIndex("Barcode");
                Barcodes.add(fetch.getString(columnIndex));
            }
        }
        return Barcodes;
    }
}
