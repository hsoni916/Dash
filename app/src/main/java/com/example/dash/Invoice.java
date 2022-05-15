package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Invoice extends AppCompatActivity {
    private DBManager dbManager;
    AutoCompleteTextView Names,PhoneNumber, Particular;
    List<String> GenericItemsGold = new ArrayList<>();
    List<String> GenericItemsSilver = new ArrayList<>();
    List<String> Purity_Levels_Gold = new ArrayList<>();
    List<String> Purity_Levels_Silver = new ArrayList<>();
    List<Hallmarking_Standards> hallmarking_standards = new ArrayList<>();
    ImageButton Add_Barcode_Item;
    PopupWindow popupWindow;
    Context context;
    private DBHelper dbHelper;
    ArrayAdapter<String> Supplier_Adapter;
    ContentValues contentValues = new ContentValues();
    String typeof = "";
    List<Long> itemids = new ArrayList<>();
    RecyclerView ItemList;
    ItemListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoiceform);
        context = getBaseContext();
        dbManager = new DBManager(this);
        dbManager.open();
        Names = findViewById(R.id.name_etv);
        PhoneNumber = findViewById(R.id.phone_etv);
        Particular = findViewById(R.id.particular);
        Add_Barcode_Item = findViewById(R.id.Add_item);
        ItemList = findViewById(R.id.item_list);
        GenericItemsGold.addAll(Arrays.asList("Mens Ring", "Women Ring", "Chain",
                "Plastic Paatla", "Gold Set", "Gold Haar",
                "Earring", "Tops", "Gents Bracelets",
                "Ladies Bracelets", "Gold Paatla", "Gold Kada",
                "Pendant", "Pendant-Set","Bor", "Nath",
                "Damdi", "Tikka", "Gold Saakra", "Gold Coin",
                "Mangalsutra", "MS-Pendant", "MS-Pendant Set",
                "Dano", "Nathdi", "Pokarva", "Baby-Earring"));

        GenericItemsSilver.addAll(Arrays.asList("Silver Saakra", "Silver Chain", "Silver Murti",
                "Silver Coin", "Silver Mangalsutra", "Silver Bracelets", "Silver Mangalsutra","Silver Men Ring", "Silver Women Rings"));

        Purity_Levels_Silver.addAll(Arrays.asList("925", "Kachhi Silver", "Zevar Silver", "D-Silver", "Rupa"));


        Purity_Levels_Gold.addAll(Arrays.asList("999 Fine Gold", "23KT958", "22KT916", "21KT875",
                "20KT833", "18KT750", "14KT585","Others"));

        //dbManager.insertAllCategoriesGold(new ArrayList<String>(){{addAll(GenericItemsGold); addAll(GenericItemsSilver);}});
        //dbManager.insertAllCategoriesSilver(GenericItemsSilver);

        ArrayAdapter<String> NameAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dbManager.ListAllCustomer());
        ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dbManager.ListAllPhone());
        ArrayAdapter<String> DOBAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dbManager.ListDOB());
        ArrayAdapter<String> ItemAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item,dbManager.ListAllItems());
        if(ItemAdapter.getCount()==0){
            ItemAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,
                    new ArrayList<String>(){{addAll(GenericItemsGold); addAll(GenericItemsSilver);}});
        }

        Names.setThreshold(2);
        Names.setAdapter(NameAdapter);

        PhoneNumber.setThreshold(1);
        PhoneNumber.setAdapter(PhoneAdapter);

        Particular.setThreshold(2);
        Particular.setAdapter(ItemAdapter);
        Names.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PhoneNumber.setEnabled(false);
                PhoneNumber.setText(PhoneAdapter.getItem(i));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
                try {
                    //Date date1=new SimpleDateFormat("dd-MM", Locale.getDefault()).parse(String.valueOf(DOBAdapter.getItem(i)));
                    Date date1 = dateFormat.parse(String.valueOf(DOBAdapter.getItem(i)));
                    Date c = Calendar.getInstance().getTime();
                    Date today= dateFormat.parse(dateFormat.format(c));
                    assert date1 != null;
                    if(date1.equals(today)){
                        Toast.makeText(getBaseContext(),"Customer's Birthday.",Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Next Fetch History of Sales by Name.
            }
        });

        ArrayAdapter<String> finalItemAdapter = ItemAdapter;
        Particular.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(GenericItemsGold.contains(finalItemAdapter.getItem(i)) || GenericItemsSilver.contains(finalItemAdapter.getItem(i))){

                    Log.d("Item :","Generic");
                    Add_Barcode_Item.setEnabled(false);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if(inflater!=null){
                        final View marginView = inflater.inflate(R.layout.generic_popup,null);
                        popupWindow = new PopupWindow(marginView, 800,600);
                        popupWindow.setAnimationStyle(R.style.popup_animation);
                        popupWindow.showAtLocation(Particular.getRootView(), Gravity.CENTER,0,0);
                        popupWindow.setFocusable(true);
                        popupWindow.update();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        Spinner Purity;
                        Spinner Supplier;
                        TextView Category;
                        EditText GrossWeight, LessWeight, NetWeight, ExtraCharges, Wastage;
                        TextView BasePurity;
                        Purity = marginView.findViewById(R.id.purity_etv);
                        Category = marginView.findViewById(R.id.category_etv);
                        Supplier = marginView.findViewById(R.id.supplier_etv);

                        GrossWeight = marginView.findViewById(R.id.weight_etv);
                        LessWeight = marginView.findViewById(R.id.less_weight_etv);
                        NetWeight = marginView.findViewById(R.id.net_weight_etv);
                        ExtraCharges = marginView.findViewById(R.id.charges_etv);
                        Wastage = marginView.findViewById(R.id.touch_etv);
                        BasePurity = marginView.findViewById(R.id.Label7);
                        TextView NameError = marginView.findViewById(R.id.NameError);
                        Button save,clear;
                        save = marginView.findViewById(R.id.save_item);
                        clear = marginView.findViewById(R.id.clear_item);

                        ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<String>
                                (marginView.getContext(), android.R.layout.simple_spinner_item,
                                        new ArrayList<String>(){{addAll(Purity_Levels_Gold); addAll(Purity_Levels_Silver);}});
                        Purity_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        Purity.setAdapter(Purity_Adapter);
                        Purity.setSelection(0);
                        Category.setEnabled(false);
                        Category.setText(finalItemAdapter.getItem(i));
                        Supplier_Adapter = new ArrayAdapter<String>
                                (marginView.getContext(), android.R.layout.simple_spinner_item,
                                        dbManager.ListAllSuppliers());
                        //fetch suppliers from backend.
                        Supplier_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        Purity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                typeof =Purity.getItemAtPosition(i).toString();
                                switch (i){
                                    case 0:
                                        BasePurity.setText("99");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 1:
                                        BasePurity.setText("96");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 2:
                                        BasePurity.setText("92");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 3:
                                        BasePurity.setText("88");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 4:
                                        BasePurity.setText("84");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 5:
                                        BasePurity.setText("76");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 6:
                                        BasePurity.setText("59");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 8: BasePurity.setText("92.5");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 9: BasePurity.setText("72");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 10:
                                    case 11:
                                        BasePurity.setText("62");
                                        BasePurity.setEnabled(false);
                                        break;
                                    case 7:
                                    case 12:
                                        BasePurity.setEnabled(true);
                                        break;
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

                        if(Supplier_Adapter.getCount()<0){
                            Log.d("Supplier count","< 0");
                            List<String> Empty_Adapter = new ArrayList<>();
                            Supplier_Adapter = new ArrayAdapter<String>
                                    (marginView.getContext(), android.R.layout.simple_spinner_item,
                                            new ArrayList<String>(){{addAll(Empty_Adapter);}});
                            Supplier_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        }
                        Supplier.setAdapter(Supplier_Adapter);
                        //if(Supplier_Adapter.getCount()==0)
                   /*     Supplier.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(!b){
                                    if(Supplier.getSelectedItem().toString().length()>=3){
                                        if(!Supplier_Adapter.toString().contains(Supplier.getSelectedItem().toString())){
                                            //Bubble to add that to the
                                            ConstraintLayout add_supplier_layout = marginView.findViewById(R.id.supplier_add_layout);
                                            add_supplier_layout.setVisibility(View.VISIBLE);
                                            Button Add,Close;
                                            Add = marginView.findViewById(R.id.add_supplier);
                                            Close = marginView.findViewById(R.id.don't_add_supplier);
                                            Add.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    long result = dbManager.AddNewSupplier(Supplier.getSelectedItem().toString());
                                                    if(result!=-1){
                                                        TextView Attention_text = marginView.findViewById(R.id.Attention_text);
                                                        Attention_text.setText(R.string.supplier_success);
                                                        Attention_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.success, 0, 0, 0);
                                                        Attention_text.setCompoundDrawablePadding(4);
                                                        final Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                // Do something after 5s = 5000ms
                                                                add_supplier_layout.setVisibility(View.GONE);
                                                            }
                                                        }, 2500);
                                                    }
                                                }
                                            });

                                            Close.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    add_supplier_layout.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                }else{
                                    if(!Supplier.getSelectedItem().toString().isEmpty()){
                                        TextView attention = marginView.findViewById(R.id.Attention_text_1);
                                        if(attention.getVisibility()==View.INVISIBLE || attention.getVisibility()==View.GONE){
                                            attention.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        TextView attention = marginView.findViewById(R.id.Attention_text_1);
                                        attention.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }); */
                        //Receive input from user and save it.
                        //in a separate database as to avoid adding it to inventory.

                        GrossWeight.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if(!GrossWeight.getText().toString().isEmpty()){
                                    if(!LessWeight.getText().toString().isEmpty()){
                                        double diff = Double.parseDouble(GrossWeight.getText().toString())-Double.parseDouble(LessWeight.getText().toString());
                                        NetWeight.setText(String.format(Locale.getDefault(),"%.2f",diff));
                                    }else{
                                     NetWeight.setText(GrossWeight.getText().toString());
                                    }
                                }else{
                                    NetWeight.setText(null);
                                    if(NameError.getVisibility()==View.VISIBLE){
                                        NameError.setText(null);
                                        NameError.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        });

                        LessWeight.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if(!GrossWeight.getText().toString().isEmpty()){
                                    double GW = Double.parseDouble(GrossWeight.getText().toString());
                                    if(!LessWeight.getText().toString().isEmpty()){
                                        try{
                                            double LW = Double.parseDouble(LessWeight.getText().toString());
                                            if(LW<GW){
                                                save.setEnabled(true);
                                                double NW = GW-LW;
                                                NetWeight.setText(String.format(Locale.getDefault(),"%.3f", NW));
                                            }else{
                                                save.setEnabled(false);
                                                Toast.makeText(getBaseContext(),"Invalid weight",Toast.LENGTH_LONG).show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        NetWeight.setText(GrossWeight.getText().toString());
                                    }
                                }
                            }
                        });

                        LessWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(!b){
                                    if(LessWeight.getText().toString().isEmpty()){
                                        double zero =0;
                                        LessWeight.setText(String.format(Locale.getDefault(),"%.3f",zero));
                                    }
                                }
                            }
                        });

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //"Name" + " STRING NOT NULL,"
                                //            + "Date_Of" + " STRING NOT NULL,"
                                //            + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
                                //            + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
                                //            + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
                                //            + "Type" + "STRING NOT NULL,"
                                //            + "Wastage" + " DECIMAL(3,3) NOT NULL,"
                                //            + "ADD_INFO" + "STRING"
                                contentValues.put("Name",Category.getText().toString());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                                contentValues.put("Date_Of",dateFormat.format(Calendar.getInstance().getTime()));
                                if(!GrossWeight.getText().toString().isEmpty()){
                                    contentValues.put("GrossWeight",Double.parseDouble(GrossWeight.getText().toString()));
                                    if(!LessWeight.getText().toString().isEmpty()){
                                        try{
                                            contentValues.put("LessWeight",Double.parseDouble(LessWeight.getText().toString()));
                                            contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString())-Double.parseDouble(LessWeight.getText().toString()));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            contentValues.put("LessWeight",0.00);
                                            contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString()));
                                        }
                                    }else{
                                        contentValues.put("LessWeight",0.00);
                                        contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString()));
                                    }
                                    contentValues.put("TypeOfArticle",typeof);
                                    if(!Wastage.getText().toString().isEmpty()){
                                        try{
                                            contentValues.put("Wastage",Double.parseDouble(Wastage.getText().toString()));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        contentValues.put("Wastage",0.00);
                                    }
                                    dbHelper = new DBHelper(marginView.getContext());
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    long id = db.insert("Sundry_Supplies", null,contentValues);
                                    if(id!=-1){
                                        Toast.makeText(marginView.getContext(),"Item Saved",Toast.LENGTH_LONG).show();
                                        AddItem(id);
                                        popupWindow.dismiss();
                                    }else{
                                        Toast.makeText(marginView.getContext(),"Item Save Unsuccessful",Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    NameError.setText(R.string.gross_weight_error);
                                    NameError.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }else{
                    Add_Barcode_Item.setEnabled(true);
                }
            }
        });
    }

    private void AddItem(long id) {
        //"Name" + " STRING NOT NULL,"
        //            + "Date_Of" + " STRING NOT NULL,"
        //            + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
        //            + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
        //            + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
        //            + "TypeOfArticle" + " STRING NOT NULL,"
        //            + "Wastage" + " DECIMAL(3,3) NOT NULL,"
        //            + "ADD_INFO" + " STRING,"
        //            + "Status" + " INTEGER NOT NULL DEFAULT 0 CHECK ( Status IN (-1,0,1))"
        itemids.add(id);
        dbHelper = new DBHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<SundryItem> sundryItemList = new ArrayList<>();
        if(adapter==null){
            for(int i=0;i<itemids.size();i++){
                String sql = "SELECT * FROM Sundry_Supplies WHERE rowid = " + itemids.get(i);
                Cursor fetch = db.rawQuery(sql,null);
                while (fetch.moveToNext() && !fetch.isAfterLast()){
                    int CI;
                    SundryItem newitem = new SundryItem();
                    CI = fetch.getColumnIndex("Name");
                    newitem.setName(fetch.getString(CI));
                    CI = fetch.getColumnIndex("GrossWeight");
                    newitem.setGW(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("LessWeight");
                    newitem.setLW(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("NetWeight");
                    newitem.setNW(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("ExtraCharges");
                    newitem.setEC(fetch.getDouble(CI));
                    sundryItemList.add(newitem);
                }
            }
            adapter = new ItemListAdapter(sundryItemList);
            ItemList.setAdapter(adapter);
            ItemList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            ItemList.setItemAnimator(new DefaultItemAnimator());
            ItemList.setVisibility(View.VISIBLE);
        }
    }
    private void RemoveItem(long id) {
        //Adapter method.

    }
}
