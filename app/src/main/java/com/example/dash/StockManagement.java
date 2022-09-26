package com.example.dash;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StockManagement extends AppCompatActivity {
    List<String> GenericItemsGold = new ArrayList<>();
    List<String> GenericItemsSilver = new ArrayList<>();
    List<String> Purity_Levels_Gold = new ArrayList<>();
    List<String> Purity_Levels_Silver = new ArrayList<>();
    List<Supplier> Suppliers = new ArrayList<>();
    List<String> SupplierNames = new ArrayList<>();
    Spinner Categories, Purity;
    AutoCompleteTextView Supplier;
    private DBManager dbManager;
    private DBHelper dbHelper;
    EditText BasePurity;
    ContentValues contentValues = new ContentValues();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText GrossWeight, LessWeight, NetWeight, ExtraCharges, Wastage;
    Button save,clear;
    String typeof = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventoryform);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Stock Entry");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.mattegrey1)));

        Purity = findViewById(R.id.purity_etv);
        Categories = findViewById(R.id.category_etv);
        Supplier = findViewById(R.id.supplier_etv);

        dbManager = new DBManager(this);
        dbManager.open();
        BasePurity = findViewById(R.id.Label7);

        GrossWeight = findViewById(R.id.weight_etv);
        LessWeight = findViewById(R.id.less_weight_etv);
        NetWeight = findViewById(R.id.net_weight_etv);
        ExtraCharges = findViewById(R.id.charges_etv);
        Wastage = findViewById(R.id.touch_etv);

        save = findViewById(R.id.save_item);
        clear = findViewById(R.id.clear_item);

        TextView NameError = findViewById(R.id.NameError);
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
        ArrayAdapter<String> ItemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<String>() {{
                    addAll(GenericItemsGold);
                    addAll(GenericItemsSilver);
                }});
        Categories.setAdapter(ItemAdapter);
        ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<>
                (getBaseContext(), android.R.layout.simple_spinner_dropdown_item,
                        new ArrayList<String>() {{
                            addAll(Purity_Levels_Gold);
                            addAll(Purity_Levels_Silver);
                        }});
        Purity.setAdapter(Purity_Adapter);

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
        db.collection("Suppliers").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult()!=null){
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Supplier NewSupplier = doc.toObject(Supplier.class);
                        Suppliers.add(NewSupplier);
                        SupplierNames.add(NewSupplier.getBusiness());
                    }
                    ArrayAdapter<String> SupplierAdapter = new ArrayAdapter<>(getBaseContext(),
                            android.R.layout.simple_spinner_dropdown_item, SupplierNames);
                    Supplier.setAdapter(SupplierAdapter);
                }
            }
        });

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

        LessWeight.setOnFocusChangeListener((view, b) -> {
            if(!b){
                if(LessWeight.getText().toString().isEmpty()){
                    double zero =0;
                    LessWeight.setText(String.format(Locale.getDefault(),"%.3f",zero));
                }
            }
        });

        save.setOnClickListener(view -> {
            //            + "Purity" + " STRING NOT NULL,"
            //            + "Name" + " STRING NOT NULL,"
            //            + "Wastage" + " DECIMAL(2,2),"
            //            + "GrossWeight" + " DECIMAL(7,3) NOT NULL,"
            //            + "LessWeight" + " DECIMAL(7,3) NOT NULL,"
            //            + "NetWeight" + " DECIMAL(7,3) NOT NULL,"
            //            + "ExtraCharges" + " INTEGER,"
            //            + "HUID" + " STRING,"
            //            + "SupplierCode" + " INTEGER" + ");";
            contentValues = new ContentValues();
            contentValues.put("Name",Categories.getSelectedItem().toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            contentValues.put("Date_Of",dateFormat.format(Calendar.getInstance().getTime()));
            String example = dateFormat.format(Calendar.getInstance().getTime());
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
                        contentValues.put("Wastage",Double.parseDouble(Wastage.getText().toString())
                                +Double.parseDouble(BasePurity.getText().toString()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    contentValues.put("Wastage",0.00);
                }
                if(!ExtraCharges.getText().toString().isEmpty()){
                    try{
                        contentValues.put("ExtraCharges",Double.parseDouble(ExtraCharges.getText().toString()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                dbHelper = new DBHelper(getBaseContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = db.insert("Inventory", null,contentValues);
                if(id!=-1){
                    Toast.makeText(getBaseContext(),"Item Saved",Toast.LENGTH_LONG).show();
                    //AddItem(id);
                }else{
                    Toast.makeText(getBaseContext(),"Item Save Unsuccessful",Toast.LENGTH_LONG).show();
                }
            }else{
                NameError.setText(R.string.gross_weight_error);
                NameError.setVisibility(View.VISIBLE);
            }
        });

    }
}
