package com.example.dash;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoiceform);
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
                            "MS-Pendant", "MS-Pendant Set"});
        }else{
            ItemAdapter.addAll("Gents Ring", "Ladies Ring", "Chain",
                    "Plastic Paatla", "Gold Set", "Gold Haar",
                    "Earring", "Tops", "Gents Bracelets",
                    "Ladies Bracelets", "Gold Paatla", "Gold Kada",
                    "Pendant", "Bor", "Nath",
                    "Damdi", "Tikka", "Gold Saakra",
                    "Silver Saakra", "Silver Chain", "Silver Murti",
                    "Gold Coin", "Silver Coin", "Mangalsutra",
                    "MS-Pendant", "MS-Pendant Set");
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
                    }
                }else{
                    Add_Barcode_Item.setEnabled(true);
                }
            }
        });



    }


}
