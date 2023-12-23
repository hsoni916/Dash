package com.example.dash;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firestore.v1.StructuredQuery;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderManagement extends AppCompatActivity {

    private DBManager dbManager;
    private TextView SelectAnOrder;

    private RecyclerView CurrentOrderView;

    private EditText searchBar;

    List<OrderDetails> CurrentOrders = new ArrayList<>();
    private OrderListAdapter OrderAdapter;

    TextView Name,PhoneNumber,DD,RateFixed;
    RecyclerView Items;
    private OrderItemAdapter OrderItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_management_new);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Order Management");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.teal_500)));
        dbManager = new DBManager(this);
        searchBar = findViewById(R.id.search_bar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        OrderAdapter = new OrderListAdapter(CurrentOrders,getBaseContext());
        CurrentOrderView = findViewById(R.id.OrderList);
        CurrentOrderView.setAdapter(OrderAdapter);
        CurrentOrderView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        CurrentOrderView.setItemAnimator(new DefaultItemAnimator());

        Items = findViewById(R.id.OrderItems);
        Name = findViewById(R.id.CustomerName);
        PhoneNumber = findViewById(R.id.PhoneNumber);
        DD = findViewById(R.id.DeliveryDate);
        RateFixed = findViewById(R.id.RateFixed);
        File orderFiles = getBaseContext().getExternalFilesDir(null);
        Log.d("Path:",orderFiles.getAbsolutePath());
        for(String ordername: Objects.requireNonNull(orderFiles.list()))
        {
            Log.d("OrderName:",ordername);
            try {
                File f = new File(getBaseContext().getExternalFilesDir(null),ordername);
                Log.d("File:",f.getAbsolutePath());
                FileInputStream fileInputStream = new FileInputStream(f);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String fileString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((fileString = bufferedReader.readLine())!=null){
                    stringBuilder.append(fileString);
                }
                Gson gson = new Gson();
                OrderDetails orderDetails = gson.fromJson(stringBuilder.toString(),OrderDetails.class);
                CurrentOrders.add(orderDetails);
                Log.d("Date:", String.valueOf(CurrentOrders.size()));
                OrderAdapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                Log.d("File Not Found"," True");
            } catch (UnsupportedEncodingException e) {
                Log.d("File is unsupported"," True");
            } catch (IOException e) {
                Log.d("Unhandled Exception"," True");
            }
        }

        OrderAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                OrderDetails details = CurrentOrders.get(position);
                Log.d("Details:",details.getCustomerName()+"-"+details.getPhoneNumber()+"-"+details.getDeliveryDate());
                Name.setText(details.getCustomerName());
                PhoneNumber.setText(details.getPhoneNumber());
                DD.setText(details.getDeliveryDate());
                if(details.isRateFix()){
                    RateFixed.setVisibility(View.VISIBLE);
                    String FixedRate = details.getRate() + "/10gm";
                    RateFixed.setText(FixedRate);
                }
                MotionLayout SidePanel = findViewById(R.id.SidePanel);
                SidePanel.transitionToEnd();
                OrderItemAdapter = new OrderItemAdapter(getBaseContext(),details.getItems(),details.getWeights(),details.getRemarks(),details.getSamplePhotos());
                Items.setAdapter(OrderItemAdapter);
                Items.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                Items.setItemAnimator(new DefaultItemAnimator());
                return true;
            }
        });
    }
}
