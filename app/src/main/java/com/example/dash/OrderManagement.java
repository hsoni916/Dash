package com.example.dash;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class OrderManagement extends AppCompatActivity {

    private DBManager dbManager;
    private TextView SelectAnOrder;

    private RecyclerView CurrentOrderList;
    private String SelectedOrderFile = "";

    private AutoCompleteTextView Name,Phone,Item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_management);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Order Management");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.teal_500)));
        dbManager = new DBManager(this);

        SelectAnOrder = findViewById(R.id.OrderPlaceHolder);




    }
}
