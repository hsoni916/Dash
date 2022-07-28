package com.example.dash;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NewSupplier extends AppCompatActivity {
    EditText Business, Owner, City, GST, Phone;
    Button Save, Clear;
    ChipGroup MetalGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        Business = findViewById(R.id.Business_etv);
        Owner = findViewById(R.id.Owner_etv);
        City = findViewById(R.id.City_etv);
        GST = findViewById(R.id.gstin_etv);
        Phone = findViewById(R.id.phone_etv);
        MetalGroup = findViewById(R.id.Offerings);
        Save = findViewById(R.id.save_supplier);
        Clear = findViewById(R.id.clear_supplier);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Supplier SupplierNew = new Supplier();
        InputFilter business = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder();
                for (int i = start;i<end;i++){
                    if (Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i))){
                        builder.append(source.charAt(i));
                    }
                }
                boolean validity = (builder.length() == end-start);
                return validity ? null : builder.toString();
            }
        };
        InputFilter owner = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder();
                for (int i = start;i<end;i++){
                    if (Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i))){
                        builder.append(source.charAt(i));
                    }
                }
                boolean validity = (builder.length() == end-start);
                return validity ? null : builder.toString();
            }
        };
        InputFilter city = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                StringBuilder builder = new StringBuilder();
                for (int i = start;i<end;i++){
                    if (Character.isLetterOrDigit(source.charAt(i))){
                        builder.append(source.charAt(i));
                    }
                }
                boolean validity = (builder.length() == end-start);
                return validity ? null : builder.toString();
            }
        };

        GST.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!GST.getText().toString().isEmpty() || (GST.getText().toString().length()==15)){
                    if(GST.getText().toString().matches("\\d\\d[A-Z]{5}[0-9]{4}[A-Z][0-9][A-Z]{2}")){
                        SupplierNew.setGST(GST.getText().toString());
                    }else{
                        SupplierNew.setGST(null);
                    }
                }else{
                    SupplierNew.setGST(null);
                }
            }
        });

        MetalGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                for(int i =0;i<checkedIds.size();i++) {
                    Chip chip = (Chip) group.getChildAt(i);
                    Log.d("Chips:", chip.getText().toString());
                }
            }
        });

        Business.setFilters(new InputFilter[]{business});
        Owner.setFilters(new InputFilter[]{owner});
        City.setFilters(new InputFilter[]{city});
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Business.getText().toString().isEmpty()){
                    SupplierNew.setBusiness(Business.getText().toString());
                }else{
                    SupplierNew.setBusiness(null);
                }
                if(!Owner.getText().toString().isEmpty()){
                    SupplierNew.setOwner(Owner.getText().toString());
                }else{
                    SupplierNew.setOwner(null);
                }
                if(!City.getText().toString().isEmpty()){
                    SupplierNew.setCity(City.getText().toString());
                }else{
                    SupplierNew.setCity(null);
                }
                if(!Phone.getText().toString().isEmpty()){
                    SupplierNew.setPhone(Phone.getText().toString());
                }else{
                    SupplierNew.setPhone(null);
                }
                if(SupplierNew.getBusiness()!=null &&
                        (SupplierNew.getCity()!=null || SupplierNew.getOwner()!=null
                        || SupplierNew.getPhone()!=null || SupplierNew.getGST()!=null))
                {
                        //
                }
            }
        });

    }

}
