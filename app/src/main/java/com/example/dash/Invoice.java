package com.example.dash;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ListAdapter;
import android.widget.PopupWindow;
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
import java.util.Objects;

public class Invoice extends AppCompatActivity {
    private DBManager dbManager;
    AutoCompleteTextView Names,PhoneNumber, Particular;
    List<String> GenericItems = new ArrayList<>();
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
        GenericItems.addAll(Arrays.asList("Gents Ring", "Ladies Ring", "Chain",
                "Plastic Paatla", "Gold Set", "Gold Haar",
                "Earring", "Tops", "Gents Bracelets",
                "Ladies Bracelets", "Gold Paatla", "Gold Kada",
                "Pendant", "Pendant-Set","Bor", "Nath",
                "Damdi", "Tikka", "Gold Saakra",
                "Silver Saakra", "Silver Chain", "Silver Murti",
                "Gold Coin", "Silver Coin", "Mangalsutra",
                "MS-Pendant", "MS-Pendant Set"));
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
                    new String[]{"Gents Ring", "Ladies Ring", "Chain",
                            "Plastic Paatla", "Gold Set", "Gold Haar",
                            "Earring", "Tops", "Gents Bracelets",
                            "Ladies Bracelets", "Gold Paatla", "Gold Kada",
                            "Pendant", "Pendant-Set","Bor", "Nath",
                            "Damdi", "Tikka", "Gold Saakra",
                            "Silver Saakra", "Silver Chain", "Silver Murti",
                            "Gold Coin", "Silver Coin", "Mangalsutra",
                            "MS-Pendant", "MS-Pendant Set", "Pan+Tops"});
        }else{
            ItemAdapter.addAll("Gents Ring", "Ladies Ring", "Chain",
                    "Plastic Paatla", "Gold Set", "Gold Haar",
                    "Earring", "Tops", "Gents Bracelets",
                    "Ladies Bracelets", "Gold Paatla", "Gold Kada",
                    "Pendant", "Bor", "Nath",
                    "Damdi", "Tikka", "Gold Saakra",
                    "Silver Saakra", "Silver Chain", "Silver Murti",
                    "Gold Coin", "Silver Coin", "Mangalsutra",
                    "MS-Pendant", "MS-Pendant Set", "Pan+Tops");
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
                if(GenericItems.contains(finalItemAdapter.getItem(i))){
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
                        AutoCompleteTextView Purity, Category, Supplier;
                        EditText GrossWeight, LessWeight, NetWeight, ExtraCharges, Wastage;

                        Purity = marginView.findViewById(R.id.purity_etv);
                        Category = marginView.findViewById(R.id.category_etv);
                        Supplier = marginView.findViewById(R.id.supplier_etv);

                        GrossWeight = marginView.findViewById(R.id.weight_etv);
                        LessWeight = marginView.findViewById(R.id.less_weight_etv);
                        NetWeight = marginView.findViewById(R.id.net_weight_etv);
                        ExtraCharges = marginView.findViewById(R.id.charges_etv);
                        Wastage = marginView.findViewById(R.id.touch_etv);

                        ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<String>
                                (marginView.getContext(), android.R.layout.select_dialog_item,
                                        new String[]{"999 Fine Gold", "23KT958", "22KT916", "21KT875",
                                        "20KT833", "18KT750", "14KT585"});

                        Purity.setThreshold(2);
                        Purity.setAdapter(Purity_Adapter);


                        ArrayAdapter<String> genericitemadapter=new ArrayAdapter<String>(marginView.getContext(),
                                android.R.layout.select_dialog_item,
                                dbManager.ListGenericItems());

                        Category.setThreshold(2);
                        Category.setAdapter(genericitemadapter);
                        ArrayAdapter<String> Supplier_Adapter = new ArrayAdapter<String>
                                (marginView.getContext(), android.R.layout.select_dialog_item,
                                        dbManager.ListAllSuppliers());

                        if(Supplier_Adapter.getCount()>0){
                            Supplier.setThreshold(2);
                            Supplier.setAdapter(Supplier_Adapter);
                        }

                        //if(Supplier_Adapter.getCount()==0)
                        Supplier.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(!b){
                                    if(Supplier.getText().toString().length()>=3){
                                        if(!Supplier_Adapter.toString().contains(Supplier.getText().toString())){
                                            //Bubble to add that to the
                                            ConstraintLayout add_supplier_layout = marginView.findViewById(R.id.supplier_add_layout);
                                            add_supplier_layout.setVisibility(View.VISIBLE);
                                            Button Add,Close;
                                            Add = marginView.findViewById(R.id.add_supplier);
                                            Close = marginView.findViewById(R.id.dont_add_supplier);

                                            Add.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    long result = dbManager.AddNewSupplier(Supplier.getText().toString());
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
                                    if(!Supplier.getText().toString().isEmpty()){
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
                    }
                }else{
                    Add_Barcode_Item.setEnabled(true);
                }
            }
        });



    }


}
