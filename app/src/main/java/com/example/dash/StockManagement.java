package com.example.dash;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    AutoCompleteTextView Supplier_tv;
    private DBManager dbManager;
    private DBHelper dbHelper;
    EditText BasePurity;
    ContentValues contentValues = new ContentValues();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText GrossWeight, LessWeight, NetWeight, ExtraCharges, Wastage, HUID;
    Button save,clear;
    String typeof = "", huidString ="";
    private String SelectedSupplier;
    View disableView;
    ProgressBar progressBar;
    FirebaseFirestore inventory = FirebaseFirestore.getInstance();
    RecyclerView inventorylist;
    StockAdapter stockAdapter;
    List<Label> labels = new ArrayList<>();
    Label label = new Label();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventoryform);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Stock Entry");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.mattegrey1)));

        Purity = findViewById(R.id.purity_etv);
        Categories = findViewById(R.id.category_etv);
        Supplier_tv = findViewById(R.id.supplier_etv);
        dbManager = new DBManager(this);
        dbManager.open();
        BasePurity = findViewById(R.id.Label8);

        disableView = findViewById(R.id.disable_layout);
        progressBar = findViewById(R.id.progress_circular);
        inventorylist = findViewById(R.id.inventory_list_1);
        stockAdapter = new StockAdapter(labels);
        GrossWeight = findViewById(R.id.weight_etv);
        LessWeight = findViewById(R.id.less_weight_etv);
        NetWeight = findViewById(R.id.net_weight_etv);
        ExtraCharges = findViewById(R.id.charges_etv);
        Wastage = findViewById(R.id.touch_etv);
        HUID = findViewById(R.id.HUID_etv);
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
        dbManager.insertAllCategoriesGold(GenericItemsGold);
        //dbManager.insertAllCategoriesSilver(GenericItemsSilver);


        Categories.setAdapter(ItemAdapter);
        ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<>
                (getBaseContext(), android.R.layout.simple_spinner_dropdown_item,
                        new ArrayList<String>() {{
                            addAll(Purity_Levels_Gold);
                            addAll(Purity_Levels_Silver);
                        }});
        Categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                label.setName(ItemAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                label.setName(null);
            }
        });
        HUID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(HUID.getText().toString().length()==6){
                    huidString = HUID.getText().toString();
                }
            }
        });

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
                            android.R.layout.select_dialog_item, SupplierNames);
                    Supplier_tv.setThreshold(2);
                    Supplier_tv.setAdapter(SupplierAdapter);
                    Supplier_tv.setOnItemClickListener((adapterView, view, i, l) -> {
                        SelectedSupplier = SupplierAdapter.getItem(i);
                    });
                }
            }
        });

        Supplier_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Supplier_tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!b){
                            if(!Supplier_tv.getText().toString().isEmpty()){
                                if(!SupplierNames.contains(Supplier_tv.getText().toString())){
                                    SelectedSupplier = Supplier_tv.getText().toString();
                                }
                            }
                        }
                    }
                });
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
                    NetWeight.setText(String.valueOf(0.00));
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
            //Disable layout.

            label = new Label();
            if(disableView.getVisibility()==View.GONE){
                disableView.setVisibility(View.VISIBLE);
            }
            if(progressBar.getVisibility()==View.GONE){
                disableView.setVisibility(View.VISIBLE);
            }

            contentValues = new ContentValues();

            if(!huidString.isEmpty()){
                contentValues.put("HUID", huidString);
                label.setHUID(huidString);
            }else{
                label.setHUID("");
            }
            if(!Wastage.getText().toString().isEmpty()){
                label.setTouch(Wastage.getText().toString());
            }else{
                label.setTouch(BasePurity.getText().toString());
            }
            if(!SelectedSupplier.isEmpty()){
                contentValues.put("SupplierCode", SelectedSupplier);
            }else{
                contentValues.put("SupplierCode", "None");
            }
            label.setName(Categories.getSelectedItem().toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            label.setDate(dateFormat.format(Calendar.getInstance().getTime()));
            contentValues.put("DateOfEntry",dateFormat.format(Calendar.getInstance().getTime()));
            String example = dateFormat.format(Calendar.getInstance().getTime());
            if(!GrossWeight.getText().toString().isEmpty()){
                contentValues.put("GrossWeight",Double.parseDouble(GrossWeight.getText().toString()));
                label.setGW(GrossWeight.getText().toString());
                if(!LessWeight.getText().toString().isEmpty()){
                    try{
                        contentValues.put("LessWeight",Double.parseDouble(LessWeight.getText().toString()));
                        contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString())-Double.parseDouble(LessWeight.getText().toString()));
                        label.setLW(LessWeight.getText().toString());
                        label.setNW(String.valueOf(Double.parseDouble(GrossWeight.getText().toString())-Double.parseDouble(LessWeight.getText().toString())));
                    }catch (Exception e){
                        e.printStackTrace();
                        contentValues.put("LessWeight",0.00);
                        contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString()));
                        label.setLW("");
                        label.setNW(GrossWeight.getText().toString());
                    }
                }else{
                    contentValues.put("LessWeight",0.00);
                    contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString()));
                    label.setLW("");
                    label.setNW(GrossWeight.getText().toString());
                }
                contentValues.put("Purity",typeof);
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
                contentValues.put("Purity",BasePurity.getText().toString());
                if(!ExtraCharges.getText().toString().isEmpty()){
                    try{
                        contentValues.put("ExtraCharges",Double.parseDouble(ExtraCharges.getText().toString()));
                        label.setEC(ExtraCharges.getText().toString()+"/-");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    label.setEC("");
                }
                dbHelper = new DBHelper(getBaseContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = db.insertOrThrow(Categories.getSelectedItem().toString().replaceAll(" ","_"), null,contentValues);
                if(id!=-1){
                    Toast.makeText(getBaseContext(),"Item Saved",Toast.LENGTH_LONG).show();
                    String RV = dbManager.getBarCode(id,Categories.getSelectedItem().toString().replaceAll(" ","_"));
                    if(!RV.isEmpty()){
                        label.setBarcode(RV);
                        inventory.collection("Inventory").document(RV)
                                .set(label)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("Label Saved",RV);
                                            //Clear all controls
                                            GrossWeight.setText(null);
                                            LessWeight.setText(null);
                                            NetWeight.setText(null);
                                            ExtraCharges.setText(null);
                                            HUID.setText(null);
                                            Wastage.setText(null);
                                            labels.add(label);
                                            Supplier_tv.setText(null);
                                            Purity.setSelected(false);
                                            Categories.setSelected(false);
                                            inventorylist.setAdapter(stockAdapter);
                                            inventorylist.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                                            inventorylist.setItemAnimator(new DefaultItemAnimator());
                                            inventorylist.setVisibility(View.VISIBLE);
                                            //ItemList.setAdapter(adapter);
                                            //            ItemList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                                            //            ItemList.setItemAnimator(new DefaultItemAnimator());
                                            //            ItemList.setVisibility(View.VISIBLE);
                                        }else{
                                            Toast.makeText(getBaseContext(),"Item not saved.",Toast.LENGTH_LONG).show();
                                            savetobackend(label);

                                        }
                                        disableView.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }else{
                        label.setBarcode("");
                        Log.d("Barcode"," is NULL!");
                    }
                    //AddItem(id);
                }else{
                    Toast.makeText(getBaseContext(),"Item Save Unsuccessful",Toast.LENGTH_LONG).show();
                }
            }else{
                NameError.setText(R.string.gross_weight_error);
                NameError.setVisibility(View.VISIBLE);
            }
            //label = null;
        });
    }

    private void savetobackend(Label label) {
        save.setText(R.string.retry);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventory.collection("Inventory").document(label.getBarcode())
                        .set(label)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Label Saved", label.getBarcode());
                                    //Clear all controls
                                    GrossWeight.setText(null);
                                    LessWeight.setText(null);
                                    NetWeight.setText(null);
                                    ExtraCharges.setText(null);
                                    HUID.setText(null);
                                    Wastage.setText(null);
                                }
                            }
                        });
            }
        });
    }
}
