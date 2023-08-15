package com.example.dash;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewSupplier extends AppCompatActivity {
    EditText Business, Owner, City, GST, Phone;
    Button  Clear, NewSupplier;
    Button SSB;
    ChipGroup MetalGroup;
    SupplierAdapter supplierAdapter;
    DocumentReference supplierReference;
    ConstraintLayout SupplierForm;
    RecyclerView SupplierRecyclerView;
    boolean result;
    private DBManager dbManager;
    private int Supplier_Position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Supplier Management");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.matteYellow)));
        SupplierForm = findViewById(R.id.SupplierForm);
        NewSupplier = findViewById(R.id.NewSupplierButton);
        SupplierRecyclerView = findViewById(R.id.SupplierList);
        dbManager = new DBManager(this);
        NewSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupplierForm.setVisibility(View.VISIBLE);
                NewSupplier.setVisibility(View.INVISIBLE);
            }
        });
        Business = findViewById(R.id.Business_etv);
        Owner = findViewById(R.id.Owner_etv);
        City = findViewById(R.id.City_etv);
        GST = findViewById(R.id.gstin_etv);
        Phone = findViewById(R.id.phone_etv);
        MetalGroup = findViewById(R.id.Offerings);
        SSB = findViewById(R.id.button1);
        Clear = findViewById(R.id.clear_supplier);
        Clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Button","Click");
            }
        });
        SSB.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "a", Toast.LENGTH_LONG).show();
            Log.d("Button","Click");
        });

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Suppliers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.d("Error",error.toString());
                    return;
                }
                List<Supplier> Suppliers = new ArrayList<>();
                List<DocumentReference> DocRef = new ArrayList<>();
                assert value != null;
                Suppliers = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    Suppliers.add(doc.toObject(Supplier.class));
                    DocRef.add(doc.getReference());
                }
                Log.d("Success", String.valueOf(value.size()));
                supplierAdapter = new SupplierAdapter(Suppliers);
                supplierAdapter.DocReference(DocRef);
                SupplierRecyclerView.setAdapter(supplierAdapter);
                SupplierRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                SupplierRecyclerView.setItemAnimator(new DefaultItemAnimator());
                supplierAdapter.setOnItemClickListener(new SupplierAdapter.OnItemClicked() {
                    @Override
                    public void EditDetails(Supplier supplier, DocumentReference documentReference, int adapterPosition) {
                        Business.setText(supplier.getBusiness());
                        Owner.setText(supplier.getOwner());
                        City.setText(supplier.getCity());
                        Phone.setText(supplier.getPhone());
                        GST.setText(supplier.getGst());
                        for(int i = 0;i<supplier.getCategories().size();i++){
                            Chip current = (Chip) MetalGroup.getChildAt(i);
                            Log.d("Category:",current.getText().toString());
                            if(current.getText().toString().equalsIgnoreCase(supplier.getCategories().get(i))){
                                MetalGroup.check(current.getId());
                                Log.d("Chip Match","true");
                            }
                        }
                        SSB.setText(R.string.update_supplier);
                        supplierReference = documentReference;
                        Supplier_Position = adapterPosition;

                    }

                    @Override
                    public boolean Delete(Supplier supplier, DocumentReference documentReference) {

                        firebaseFirestore.document(documentReference.getPath()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    result = true;
                                    Toast.makeText(getBaseContext(),"Supplier deleted.",Toast.LENGTH_LONG).show();
                                }else{
                                    result = false;
                                    Toast.makeText(getBaseContext(),"Supplier delete failed.",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        return result;
                    }
                });
            }
        });



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
        Business.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Business.getText().toString().isEmpty()){
                    SupplierNew.setBusiness(Business.getText().toString());
                }
            }
        });
        Owner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Owner.getText().toString().isEmpty()){
                    SupplierNew.setOwner(Owner.getText().toString());
                }
            }
        });
        City.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!City.getText().toString().isEmpty()){
                    SupplierNew.setCity(City.getText().toString());
                }
            }
        });
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
                        SupplierNew.setGst(GST.getText().toString());
                    }else{
                        SupplierNew.setGst(null);
                    }
                }else{
                    SupplierNew.setGst(null);
                }
            }
        });
        Phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Phone.getText().toString().isEmpty()){
                    SupplierNew.setPhone(Phone.getText().toString());
                }
            }
        });
        MetalGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                List<String> NewCategory = new ArrayList<>();
                for(int i = 0;i<checkedIds.size();i++){
                    Chip chip = findViewById(checkedIds.get(i));
                    Log.d("Checked Chips",chip.getText().toString());
                    NewCategory.add(chip.getText().toString());
                }
                SupplierNew.setCategories(NewCategory);
            }
        });

        Business.setFilters(new InputFilter[]{business});
        Owner.setFilters(new InputFilter[]{owner});
        City.setFilters(new InputFilter[]{city});
        SSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupplierForm.setEnabled(false);
                if(SSB.getText()==getString(R.string.update_supplier)){
                    dbManager.updateSupplier(SupplierNew, Supplier_Position);
                    firebaseFirestore.collection("Suppliers").document(supplierReference.getId())
                            .set(SupplierNew)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Business.setText(null);
                                        Owner.setText(null);
                                        City.setText(null);
                                        GST.setText(null);
                                        Phone.setText(null);
                                        MetalGroup.clearCheck();
                                        Toast.makeText(view.getContext(),"Update Successful.",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(view.getContext(),"Update Unsuccessful.\nTry Again after few minutes.",Toast.LENGTH_LONG).show();
                                    }
                                    SupplierForm.setEnabled(true);
                                }
                            });
                }else{
                    SupplierForm.setEnabled(false);
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
                                    || SupplierNew.getPhone()!=null || SupplierNew.getGst()!=null))
                    {
                        firebaseFirestore.collection("Suppliers").add(SupplierNew).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    dbManager.insertNewSupplier(SupplierNew.getBusiness(),SupplierNew.getOwner(),
                                            SupplierNew.getGst(),SupplierNew.getCity(),SupplierNew.getPhone(),SupplierNew.getCategories());
                                    Business.setText(null);
                                    Owner.setText(null);
                                    City.setText(null);
                                    GST.setText(null);
                                    Phone.setText(null);
                                    MetalGroup.clearCheck();
                                    Log.d("Supplier:",task.getResult().getId());
                                    Toast.makeText(view.getContext(),"Supplier saved.",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(view.getContext(),"Failed to save supplier.\nTry again in few minutes.",Toast.LENGTH_LONG).show();
                                }
                                SupplierForm.setEnabled(true);
                            }
                        });
                    }
                    SupplierForm.setVisibility(View.GONE);
                    NewSupplier.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
