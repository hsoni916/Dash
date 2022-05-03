package com.example.dash;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import androidx.constraintlayout.widget.ConstraintLayout;

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

        //For future upgrades, fetch hallmarking standards from cloud and add it to list.
        //Also fetch any new products from the cloud to add it to backend.
        //Periodically take a backup of all data.

        Purity_Levels_Gold.addAll(Arrays.asList("999 Fine Gold", "23KT958", "22KT916", "21KT875",
                "20KT833", "18KT750", "14KT585"));

        dbManager.insertAllCategoriesGold(GenericItemsGold);
        dbManager.insertAllCategoriesSilver(GenericItemsSilver);

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
                        Spinner Purity,  Supplier;
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
                        ArrayAdapter<String> Supplier_Adapter = new ArrayAdapter<String>
                                (marginView.getContext(), android.R.layout.simple_spinner_item,
                                        dbManager.ListAllSuppliers());
                        Supplier_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Purity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                switch (i){
                                    case 0: BasePurity.setText("99");
                                        break;
                                    case 1: BasePurity.setText("96");
                                        break;
                                    case 2: BasePurity.setText("92");
                                        break;
                                    case 3: BasePurity.setText("88");
                                        break;
                                    case 4: BasePurity.setText("84");
                                        break;
                                    case 5: BasePurity.setText("76");
                                        break;
                                    case 6: BasePurity.setText("59");
                                        break;
                                    case 7: BasePurity.setText("");
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        if(Supplier_Adapter.getCount()>0){
                            Supplier.setAdapter(Supplier_Adapter);
                        }
                        //if(Supplier_Adapter.getCount()==0)
                        Supplier.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                                            Close = marginView.findViewById(R.id.dont_add_supplier);

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
                        });
                        //Receive input from user and save it.
                        //in a separate database as to avoid adding it to inventory.
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
                                        double LW = Double.parseDouble(LessWeight.getText().toString());
                                        if(LW<GW){
                                            save.setEnabled(true);
                                            double NW = GW-LW;
                                            NetWeight.setText(String.format(Locale.getDefault(),"%.3f", NW));
                                        }else{
                                            save.setEnabled(false);
                                        }
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
                    }
                }else{
                    Add_Barcode_Item.setEnabled(true);
                }
            }
        });
    }
}
