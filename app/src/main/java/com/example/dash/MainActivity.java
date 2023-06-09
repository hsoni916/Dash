package com.example.dash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    final Calendar calendar = Calendar.getInstance();
    Button NewCustomer, newInvoiceButton, newInventory, CashDeposit, NewSupplier, PersonalInfo;
    PopupWindow popupWindow;
    private DBManager dbManager;
    String Name="",PhoneNumber="",DateOfBirth="",City="";
    List<Customer> recordList = new ArrayList<>();
    List<Customer> recordList2 = new ArrayList<>();
    customer_adapter Custom_Adapter;
    TextView ItemsSold, InvoicesMade, NewCustomers, TotalSales, TotalRevenue, RevenueUnit, SalesUnit;
    FirebaseFirestore firebaseFirestore;
    FrameLayout CornerHub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard v1.0");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.mattegrey1)));
        dbManager = new DBManager(this);
        dbManager.open();
        NewCustomer = findViewById(R.id.NewCustomer);
        newInvoiceButton = findViewById(R.id.NewInvoice);
        newInventory = findViewById(R.id.AddInventory);
        CashDeposit = findViewById(R.id.CashEntry);
        NewSupplier = findViewById(R.id.NewSupplier);
        PersonalInfo = findViewById(R.id.PersonalInfo);
        //Metrics
        ItemsSold = findViewById(R.id.ItemsSold);
        InvoicesMade = findViewById(R.id.InvoicesMade);
        NewCustomers = findViewById(R.id.CustomersAdded);
        TotalSales = findViewById(R.id.SalesNW);
        TotalRevenue = findViewById(R.id.Revenue);
        RevenueUnit = findViewById(R.id.unit_r);
        SalesUnit = findViewById(R.id.unit_s);

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Invoices").addSnapshotListener((value, error) -> {
            assert value != null;
            if(!value.getMetadata().hasPendingWrites()){
                int itemsold = 0;
                int invoicemade = 0;
                for(DocumentSnapshot document : value.getDocuments()){
                    PrintData invoice = document.toObject(PrintData.class);
                    if(invoice!=null){
                        Log.d("Size:", String.valueOf(invoice.getSundryItemList().size()));
                        itemsold = itemsold+invoice.getSundryItemList().size();
                    }
                }
                invoicemade = invoicemade + value.getDocuments().size();
                itemsold = itemsold + Integer.parseInt(ItemsSold.getText().toString());
                invoicemade = invoicemade + Integer.parseInt(InvoicesMade.getText().toString());
                ItemsSold.setText(String.valueOf(itemsold));
                InvoicesMade.setText(String.valueOf(invoicemade));
            }
        });


        NewSupplier.setOnClickListener(view -> {
            Intent NewSupplier = new Intent(MainActivity.this, com.example.dash.NewSupplier.class);
            startActivity(NewSupplier);
        });
        dbManager.AddStandards();
        NewCustomer.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater!=null){
                RecyclerView recordContainer;
                final View marginView = inflater.inflate(R.layout.customerform,null);
                TextView NameError,PhoneError,DOBError;
                Button cancel = marginView.findViewById(R.id.cancel);
                Button save = marginView.findViewById(R.id.save);
                NameError = marginView.findViewById(R.id.NameError);
                PhoneError = marginView.findViewById(R.id.PhoneError);
                DOBError = marginView.findViewById(R.id.DOBError);
                EditText NameEtv = marginView.findViewById(R.id.name_etv);
                ArrayAdapter<String> NameAdapter = new ArrayAdapter<>
                        (view.getContext(), android.R.layout.select_dialog_item, dbManager.ListAllCustomer());
                ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<>
                        (view.getContext(), android.R.layout.select_dialog_item, dbManager.ListAllPhone());
                Log.d("Size of customers",String.valueOf(dbManager.ListAllCustomer().size()));
                recordList.clear();
                recordList2.clear();
                for(int i = 0;i<dbManager.ListAllCustomer().size();i++){
                    Customer customer = new Customer();
                    customer.setName(NameAdapter.getItem(i));
                    customer.setPhoneNumber(PhoneAdapter.getItem(i));
                    Log.d("Customer:",NameAdapter.getItem(i));
                    Log.d("Size of record:", String.valueOf(recordList.size()));
                    recordList.add(customer);
                    recordList2.add(customer);
                }
                recordContainer = marginView.findViewById(R.id.Fetch_Customer_Details);
                recordContainer.setVisibility(View.VISIBLE);
                Custom_Adapter = new customer_adapter(recordList);
                recordContainer.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recordContainer.setItemAnimator(new DefaultItemAnimator());
                recordContainer.setAdapter(Custom_Adapter);
                ProgressBar progressBar = marginView.findViewById(R.id.progress_circular);
                EditText PhoneEtv = marginView.findViewById(R.id.phone_etv);
                EditText dob = marginView.findViewById(R.id.dob_etv);
                ImageButton CalendarButton = marginView.findViewById(R.id.datePicker);
                EditText Address = marginView.findViewById(R.id.city_etv);
                Address.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!Address.getText().toString().isEmpty()){
                            if(Address.getText().toString().length()>=3){
                                City = Address.getText().toString();
                            }
                        }
                    }
                });
                popupWindow = new PopupWindow(marginView, 1200,800);
                popupWindow.setAnimationStyle(R.style.popup_animation);
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
                popupWindow.setFocusable(true);
                popupWindow.update();
                dob.setEnabled(false);
                NameEtv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!NameEtv.getText().toString().isEmpty()){
                            Filter_By_Name(NameEtv.getText().toString());
                        }else{
                            resetlist();
                        }
                        if(NameEtv.getText().toString().isEmpty()){
                            PhoneEtv.setEnabled(true);
                        }
                        if(NameEtv.getText().toString().length()>=2){
                            Name = NameEtv.getText().toString();
                            NameError.setVisibility(View.GONE);
                        }else if (NameEtv.getText().toString().isEmpty()){
                            if(!NameEtv.hasFocus()){
                                NameError.setText(R.string.name_error_1);
                                NameError.setVisibility(View.VISIBLE);
                            }
                        }else{
                            if(!NameEtv.hasFocus()){
                                NameError.setText(R.string.name_error);
                                NameError.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    private void resetlist() {
                        recordList = new ArrayList<>();
                        recordList.addAll(recordList2);
                        Custom_Adapter = new customer_adapter(recordList);
                        recordContainer.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        recordContainer.setItemAnimator(new DefaultItemAnimator());
                        recordContainer.setAdapter(Custom_Adapter);
                        Custom_Adapter.setOnItemClickListener(position -> {
                            Intent intent = new Intent(MainActivity.this, Invoice.class);
                            intent.putExtra("Name",recordList.get(position).getName());
                            intent.putExtra("Phone",recordList.get(position).getPhoneNumber());
                            startActivity(intent);
                            return false;
                        });
                    }

                    private void Filter_By_Name(String name) {
                        recordList = new ArrayList<>();
                        Custom_Adapter = new customer_adapter(recordList);
                        recordContainer.setAdapter(Custom_Adapter);
                            for(int i =0;i<recordList2.size();i++){
                                if(recordList2.get(i).getName().contains(name)){
                                    recordList.add(recordList2.get(i));
                                    Custom_Adapter.notifyItemInserted(i);
                                }
                            }

                    }
                });
                NameEtv.setOnFocusChangeListener((view1, b) -> {
                    if(!b){
                        if(NameEtv.getText().toString().isEmpty()){
                            NameError.setText(R.string.name_error_1);
                            NameError.setVisibility(View.VISIBLE);
                        }else if(NameEtv.getText().toString().length()<=2){
                            NameError.setText(R.string.name_error);
                            NameError.setVisibility(View.VISIBLE);
                        }else{
                            NameError.setVisibility(View.GONE);
                        }
                    }
                });
                PhoneEtv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(PhoneEtv.getText().toString().length()==10){
                            PhoneNumber = PhoneEtv.getText().toString();
                            PhoneError.setVisibility(View.GONE);
                        }else{
                            PhoneNumber = "";
                            if(!PhoneEtv.hasFocus()){
                                PhoneError.setText(R.string.phone_error);
                                PhoneError.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                PhoneEtv.setOnFocusChangeListener((view12, b) -> {
                    if(!b){
                        if(PhoneEtv.getText().toString().length()!=10){
                            PhoneError.setText(R.string.phone_error);
                            PhoneError.setVisibility(View.VISIBLE);
                        }else{
                            PhoneError.setText(null);
                            PhoneError.setVisibility(View.GONE);
                        }

                    }
                });
                dob.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(dob.getText().toString().isEmpty()){
                            DOBError.setText(R.string.dob_error);
                            DOBError.setVisibility(View.VISIBLE);
                        }else{
                            DOBError.setText(null);
                            DOBError.setVisibility(View.GONE);
                        }
                    }
                });
                DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, date) -> {
                    month = month+1;
                    String dobs;
                    if(month<10){
                        dobs = date+"/"+"0"+month+"/"+year;
                    }else{
                        dobs = date+"/"+month+"/"+year;
                    }
                    dob.setText(dobs);
                    DateOfBirth = dobs;
                };
                CalendarButton.setOnClickListener(view13 -> {
                    DatePickerDialog dialog = new DatePickerDialog(view13.getContext(), dateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                });

                save.setOnClickListener(view14 -> {
                    if(NameEtv.getError()==null && PhoneEtv.getError()==null){
                        progressBar.setVisibility(View.VISIBLE);
                        NameEtv.setEnabled(false);
                        PhoneEtv.setEnabled(false);
                        Address.setEnabled(false);
                        CalendarButton.setEnabled(false);
                        if(Name.length()>=2 && !PhoneNumber.isEmpty() && !DateOfBirth.isEmpty()){
                            dbManager.open();
                            long result = dbManager.insertNewCustomer(Name,PhoneNumber,DateOfBirth);

                            NameEtv.setEnabled(true);
                            PhoneEtv.setEnabled(true);
                            CalendarButton.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);

                            if(result!=-1){
                                Toast.makeText(view14.getContext(),"Customer added successfully",Toast.LENGTH_LONG).show();
                                popupWindow.dismiss();
                                //Success.
                            }else{
                                Toast.makeText(view14.getContext(),"Failure, an error occurred.",Toast.LENGTH_LONG).show();
                            }

                        }
                        if(Name.isEmpty() || Name.length()<=2){
                            NameError.setText(R.string.name_error);
                            NameError.setVisibility(View.VISIBLE);
                        }else{
                            NameError.setText(null);
                            NameError.setVisibility(View.GONE);
                        }

                        if(PhoneNumber.isEmpty()||PhoneNumber.length()<10){
                            PhoneError.setText(R.string.phone_error);
                            PhoneError.setVisibility(View.VISIBLE);
                        }else{
                            PhoneError.setText(null);
                            PhoneError.setVisibility(View.GONE);
                        }

                        if(DateOfBirth.isEmpty()){
                            DOBError.setText(R.string.dob_error);
                            DOBError.setVisibility(View.VISIBLE);
                        }else{
                            DOBError.setText(null);
                            DOBError.setVisibility(View.GONE);
                        }
                        NameEtv.setEnabled(true);
                        PhoneEtv.setEnabled(true);
                        CalendarButton.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                cancel.setOnClickListener(v -> popupWindow.dismiss());
            }
        });

        newInvoiceButton.setOnClickListener(view -> {
            Intent newinvoice = new Intent(MainActivity.this,Invoice.class);
            startActivity(newinvoice);
        });

        newInventory.setOnClickListener(view -> {
            Intent stockManagement = new Intent(MainActivity.this,StockManagement.class);
            startActivity(stockManagement);
        });

        CashDeposit.setOnClickListener(view -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater!=null){
                final View CashDepositView = inflater.inflate(R.layout.cash_deposit,null);
                TextView NameError,PhoneError,CashError;
                Button cancel = CashDepositView.findViewById(R.id.cancel);
                Button save = CashDepositView.findViewById(R.id.save);
                ChipGroup ModeOfPayments = CashDepositView.findViewById(R.id.ModeOfPaymentChips);
                NameError = CashDepositView.findViewById(R.id.NameError);
                PhoneError = CashDepositView.findViewById(R.id.PhoneError);
                CashError = CashDepositView.findViewById(R.id.cash_error);

                AutoCompleteTextView NameEtv = CashDepositView.findViewById(R.id.name_etv);
                ArrayAdapter<String> NameAdapter = new ArrayAdapter<>
                        (view.getContext(), android.R.layout.select_dialog_item, dbManager.ListAllCustomer());
                ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<>
                        (view.getContext(), android.R.layout.select_dialog_item, dbManager.ListAllPhone());


                NameEtv.setThreshold(2);
                NameEtv.setAdapter(NameAdapter);
                ModeOfPayments.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                    @Override
                    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                        List<String> NewCategory = new ArrayList<>();
                        for(int i =0;i<group.getChildCount();i++){
                            Chip chip = (Chip) group.getChildAt(i);
                            if(chip.isChecked()){
                                NewCategory.add(chip.getText().toString());
                            }
                        }
                        Log.d("Checked",NewCategory.toString());
                    }
                });

                popupWindow = new PopupWindow(CashDepositView, 1200,800);
                popupWindow.setAnimationStyle(R.style.popup_animation);
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
                popupWindow.setFocusable(true);
                popupWindow.update();
            }
        });
    }
}