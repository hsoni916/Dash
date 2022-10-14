package com.example.dash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class SimplifiedActivity extends AppCompatActivity {

    LinearLayout NewInvoice;
    TextView AdvancedOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplified);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard v1.0");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.mattegrey1)));
        NewInvoice = findViewById(R.id.MainButton);
        AdvancedOptions = findViewById(R.id.OtherOptions);
        NewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newinvoice = new Intent(SimplifiedActivity.this,Invoice.class);
                startActivity(newinvoice);
            }
        });
        AdvancedOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AdvancedActivity = new Intent( SimplifiedActivity.this, MainActivity.class);
                startActivity(AdvancedActivity);
            }
        });
    }
}