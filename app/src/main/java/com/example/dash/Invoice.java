package com.example.dash;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Invoice extends AppCompatActivity {
    private DBManager dbManager;
    AutoCompleteTextView Names,PhoneNumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoiceform);
        dbManager = new DBManager(this);
        dbManager.open();
        Names = findViewById(R.id.name_etv);
        PhoneNumber = findViewById(R.id.phone_etv);
        ArrayAdapter<String> NameAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dbManager.ListAllCustomer());
        ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dbManager.ListAllPhone());
        ArrayAdapter<String> DOBAdapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,dbManager.ListDOB());

        Names.setThreshold(2);
        Names.setAdapter(NameAdapter);

        PhoneNumber.setThreshold(1);
        PhoneNumber.setAdapter(PhoneAdapter);


        Names.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PhoneNumber.setEnabled(false);
                    PhoneNumber.setText(PhoneAdapter.getItem(i));
                try {
                    Date date1=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(DOBAdapter.getItem(i));
                    Date today= new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).parse(String.valueOf(Calendar.getInstance().getTime()));
                    assert date1 != null;
                    if(date1.equals(today)){
                        Toast.makeText(getBaseContext(),"Customer's Birthday.",Toast.LENGTH_LONG).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                    PhoneNumber.setEnabled(true);
                    PhoneNumber.setText("");
            }
        });



    }


}
