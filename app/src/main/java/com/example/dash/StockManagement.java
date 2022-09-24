package com.example.dash;

import android.content.ContentValues;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StockManagement extends AppCompatActivity {
    List<String> GenericItemsGold = new ArrayList<>();
    List<String> GenericItemsSilver = new ArrayList<>();
    List<String> Purity_Levels_Gold = new ArrayList<>();
    List<String> Purity_Levels_Silver = new ArrayList<>();
    AutoCompleteTextView Categories, Purity;
    Spinner Supplier;
    private DBManager dbManager;
    ContentValues contentValues = new ContentValues();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventoryform);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Stock Entry");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.mattegrey1)));

        Purity = findViewById(R.id.purity_etv);
        Categories = findViewById(R.id.category_etv);
        dbManager = new DBManager(this);
        dbManager.open();
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
        ArrayAdapter<String> ItemAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,
                new ArrayList<String>(){{addAll(GenericItemsGold); addAll(GenericItemsSilver);}});
        Categories.setThreshold(2);
        Categories.setAdapter(ItemAdapter);

        ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<String>
                (getBaseContext(), android.R.layout.simple_spinner_item,
                        new ArrayList<String>(){{addAll(Purity_Levels_Gold); addAll(Purity_Levels_Silver);}});

        Purity_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Purity.setAdapter(Purity_Adapter);

        Categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contentValues.put("Name",Categories.getText().toString());
            }
        });

        




    }
}
