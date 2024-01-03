package com.example.dash;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final Calendar calendar = Calendar.getInstance();
    Button NewCustomer, newInvoiceButton, newInventory, CashDeposit, NewSupplier, PersonalInfo, Analytics, NewOrder;
    PopupWindow popupWindow;
    private DBManager dbManager;
    String Name="",PhoneNumber="",DateOfBirth="",City="";
    List<Customer> recordList = new ArrayList<>();
    List<Customer> recordList2 = new ArrayList<>();
    customer_adapter Custom_Adapter;
    TextView ItemsSold, InvoicesMade, NewCustomers, TotalSales, TotalRevenue, RevenueUnit, SalesUnit;
    FirebaseFirestore firebaseFirestore;
    FrameLayout CornerHub;
    String name="", phone="";
    Double amount = 0.00;
    List<String> NewCategory = new ArrayList<>();
    String transactionDate = "";
    public Context CAContext;
    private List<String> GenericItemsGold = new ArrayList<>();
    private List<String> GenericItemsSilver = new ArrayList<>();
    private List<String> Purity_Levels_Silver = new ArrayList<>();
    private List<String> Purity_Levels_Gold = new ArrayList<>();
    ArrayAdapter<String> itemAdapter;
    private Bitmap uriToBitmap(Uri image_uri){
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(image_uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard v1.0");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.mattegrey1)));
        dbManager = new DBManager(this);
        dbManager.open();
        CAContext = this;

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
        itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<String>() {{
                    addAll(GenericItemsGold);
                    addAll(GenericItemsSilver);
                }});

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dbManager.insertAllCategoriesGold(GenericItemsGold);
                dbManager.insertAllCategoriesSilver(GenericItemsSilver);
            }
        },3000);



        NewCustomer = findViewById(R.id.NewCustomer);
        newInvoiceButton = findViewById(R.id.NewInvoice);
        newInventory = findViewById(R.id.AddInventory);
        CashDeposit = findViewById(R.id.CashEntry);
        NewSupplier = findViewById(R.id.NewSupplier);
        PersonalInfo = findViewById(R.id.PersonalInfo);
        Analytics = findViewById(R.id.reports);
        NewOrder = findViewById(R.id.NewOrder);
        Button manageOrders = findViewById(R.id.OrderManagement);
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
                try {
                    List<Customer> Customers = dbManager.GetCustomerProfile();
                    recordList.addAll(Customers);
                    recordList2.addAll(Customers);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                recordContainer = marginView.findViewById(R.id.Fetch_Customer_Details);
                recordContainer.setVisibility(View.VISIBLE);
                Custom_Adapter = new customer_adapter(recordList, CAContext);
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
                popupWindow = new PopupWindow(marginView, 1350,850);
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
                        Custom_Adapter = new customer_adapter(recordList, CAContext);
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
                        Custom_Adapter = new customer_adapter(recordList, CAContext);
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
                    try {
                        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(dobs);
                        Log.d("Date", String.valueOf(date1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
                        long result = -1;
                        progressBar.setVisibility(View.VISIBLE);
                        NameEtv.setEnabled(false);
                        PhoneEtv.setEnabled(false);
                        Address.setEnabled(false);
                        CalendarButton.setEnabled(false);
                        if(Name.length()>=2 && !PhoneNumber.isEmpty() && !DateOfBirth.isEmpty()){
                            dbManager.open();
                            if(City.isEmpty()){
                                result = dbManager.insertNewCustomer(Name,PhoneNumber,DateOfBirth,"");
                            }else{
                                result = dbManager.insertNewCustomer(Name,PhoneNumber,DateOfBirth,City);
                                Log.d("DOB",DateOfBirth);
                            }

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
                final View CashDepositView = inflater.inflate(R.layout.new_deposit,null);
                TextView NameError,PhoneError,CashError;
                DepositClass depositClass = new DepositClass();

                Button cancel = CashDepositView.findViewById(R.id.cancel);
                Button save = CashDepositView.findViewById(R.id.save);
                ChipGroup ModeOfPayments = CashDepositView.findViewById(R.id.ModeOfPaymentChips);
                NameError = CashDepositView.findViewById(R.id.NameError);
                PhoneError = CashDepositView.findViewById(R.id.PhoneError);
                CashError = CashDepositView.findViewById(R.id.cash_error);

                AutoCompleteTextView NameEtv = CashDepositView.findViewById(R.id.name_etv);
                AutoCompleteTextView PhoneEtv = CashDepositView.findViewById(R.id.phone_etv);
                EditText Amount = CashDepositView.findViewById(R.id.cash_etv);
                TextView weightlabel = CashDepositView.findViewById(R.id.label7);
                EditText weightEtv = CashDepositView.findViewById(R.id.weightdetails);
                TextView RemarkLabel = CashDepositView.findViewById(R.id.label6);
                EditText remarkEtv = CashDepositView.findViewById(R.id.remarks_etv);
                EditText narrationEtv = CashDepositView.findViewById(R.id.narration_etv);
                ImageButton CalendarButton = CashDepositView.findViewById(R.id.datePicker);
                EditText dateEtv = CashDepositView.findViewById(R.id.date_etv);

                DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, date) -> {
                    month = month+1;
                    String DateOfTransaction;
                    if(month<10){
                        DateOfTransaction = date+"/"+"0"+month+"/"+year;
                    }else{
                        DateOfTransaction = date+"/"+month+"/"+year;
                    }
                    dateEtv.setText(DateOfTransaction);
                    try {
                        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(DateOfTransaction);
                        Log.d("Date", String.valueOf(date1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    transactionDate = DateOfTransaction;
                };

                CalendarButton.setOnClickListener(view13 -> {
                    DatePickerDialog dialog = new DatePickerDialog(view13.getContext(), dateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                });

                List<String> NameList = dbManager.ListAllCustomer();
                List<String> PhoneList = dbManager.ListAllPhone();

                ArrayAdapter<String> NameAdapter = new ArrayAdapter<>
                        (view.getContext(), android.R.layout.select_dialog_item, NameList);
                ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<>
                        (view.getContext(), android.R.layout.select_dialog_item, PhoneList);

                NameEtv.setThreshold(2);
                NameEtv.setAdapter(NameAdapter);

                PhoneEtv.setThreshold(2);
                PhoneEtv.setAdapter(PhoneAdapter);

                NameEtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        name = NameAdapter.getItem(i);
                        int indextouse = NameList.indexOf(name);
                        PhoneEtv.setText(PhoneList.get(indextouse));
                        phone = PhoneList.get(indextouse);
                        NameEtv.setEnabled(false);
                        PhoneEtv.setEnabled(false);
                    }
                });

                PhoneEtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        phone = PhoneAdapter.getItem(i);
                        int indextouse = PhoneAdapter.getPosition(phone);
                        NameEtv.setText(NameAdapter.getItem(indextouse));
                        name = NameAdapter.getItem(indextouse);
                        NameEtv.setEnabled(false);
                        PhoneEtv.setEnabled(false);
                    }
                });

                Amount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!Amount.getText().toString().isEmpty()){
                            amount = Double.valueOf(Amount.getText().toString());
                        }
                    }
                });

                ModeOfPayments.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                    @Override
                    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                        for(int i =0;i<group.getChildCount();i++){
                            Chip chip = (Chip) group.getChildAt(i);
                            Chip metalchip = group.findViewById(R.id.Chip6);
                            if(chip.isChecked()){
                                NewCategory.add(chip.getText().toString());
                            }
                            if(checkedIds.contains(metalchip.getId())){
                                weightlabel.setVisibility(View.VISIBLE);
                                weightEtv.setVisibility(View.VISIBLE);
                                RemarkLabel.setVisibility(View.VISIBLE);
                                remarkEtv.setVisibility(View.VISIBLE);
                            }else{
                                weightlabel.setVisibility(View.GONE);
                                weightEtv.setVisibility(View.GONE);
                                RemarkLabel.setVisibility(View.GONE);
                                remarkEtv.setVisibility(View.GONE);
                            }
                        }
                        Log.d("Checked",NewCategory.toString());
                    }
                });

                popupWindow = new PopupWindow(CashDepositView, 1200,LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setAnimationStyle(R.style.popup_animation);
                popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
                popupWindow.setFocusable(true);
                popupWindow.update();


                TextView depositerror = CashDepositView.findViewById(R.id.Deposit_Error);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbManager.open();
                        if(NameList.contains(name)){
                            //Customer exists.
                            if(savedeposit()>0){
                             NameEtv.setText("");
                             name = "";
                             PhoneEtv.setText("");
                             phone = "";
                             Amount.setText("");
                             ModeOfPayments.clearCheck();
                             NewCategory.clear();
                             narrationEtv.setText("");
                             popupWindow.dismiss();
                             depositerror.setVisibility(View.GONE);
                            }else{
                                depositerror.setVisibility(View.VISIBLE);
                            }
                        }else{
                            //Customer is new.
                            if(PhoneEtv.getText().toString().isEmpty()){
                                PhoneError.setVisibility(View.VISIBLE);
                                PhoneError.setText("Please enter contact number.");
                            }else{
                                if( PhoneEtv.getText().toString().length()==10 || PhoneEtv.getText().toString().length()==7){
                                    PhoneError.setText("");
                                    PhoneError.setVisibility(View.GONE);
                                    phone = PhoneEtv.getText().toString();
                                    //Customer basic record complete.
                                    dbManager.quickInsertNewCustomer(NameEtv.getText().toString(),phone);
                                    //check for payment details.
                                    if(savedeposit()>0) {
                                        NameEtv.setText("");
                                        name = "";
                                        PhoneEtv.setText("");
                                        phone = "";
                                        Amount.setText("");
                                        ModeOfPayments.clearCheck();
                                        NewCategory.clear();
                                        narrationEtv.setText("");
                                        popupWindow.dismiss();
                                        depositerror.setVisibility(View.GONE);
                                        //Add the record to backend.
                                        //name,phone,amount,weight,remarks,transactionDate,narration
                                    }else{
                                        depositerror.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    PhoneError.setText("Only Indian numbers accepted.");
                                    PhoneError.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                    private long savedeposit() {
                        long result = 0;
                        if(!Amount.getText().toString().isEmpty()){

                            depositClass.setAmount(Double.parseDouble(Amount.getText().toString()));
                            depositClass.setName(name);
                            depositClass.setPhone(phone);

                            if(!NewCategory.isEmpty()){
                                double weight = 0;
                                String remarks;
                                String narration = "";
                                double amount = Double.parseDouble(Amount.getText().toString());
                                depositClass.setNarration(NewCategory.toString());
                                depositClass.setRemarks(remarkEtv.getText().toString());

                                if(NewCategory.contains("Metal")){
                                    if(!weightEtv.getText().toString().isEmpty()){
                                        weight = Double.parseDouble(weightEtv.getText().toString());
                                        depositClass.setWeight(weight);
                                    }
                                    if(!remarkEtv.getText().toString().isEmpty()){
                                        remarks = remarkEtv.getText().toString();
                                        depositClass.setRemarks(remarks);
                                    }else{
                                        remarks = NewCategory.toString();
                                        depositClass.setRemarks(remarks);
                                    }
                                    if(!narrationEtv.getText().toString().isEmpty()){
                                        narration = narrationEtv.getText().toString();
                                        depositClass.setNarration(narration);
                                    }else{
                                        narration = NewCategory.toString();
                                        depositClass.setNarration(narration);
                                    }
                                }else{
                                    remarks = NewCategory.toString();
                                    depositClass.setRemarks(remarks);
                                }
                                if(transactionDate==null){
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    result =  dbManager.addRecord(name,phone,weight,amount,remarks,dateFormat.format(Calendar.getInstance().getTime()),narration);
                                    depositClass.setTransactionDate(dateFormat.format(Calendar.getInstance().getTime()));
                                }else{
                                    result =  dbManager.addRecord(name,phone,weight,amount,remarks,transactionDate,narration);
                                    depositClass.setTransactionDate(transactionDate);
                                }

                                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault());
                                String docName = name+"_"+phone+"_"+dateFormat.format(Calendar.getInstance().getTime());
                                firebaseFirestore.collection("Deposits").document(docName)
                                        .set(depositClass)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(view.getContext(),"Deposit Saved.",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }
                        return result;
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

        NewOrder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent NewOrderIntent = new Intent(MainActivity.this, com.example.dash.NewOrder.class);
                                            startActivity(NewOrderIntent);
                                        }
                                    });

        manageOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stockManagement = new Intent(MainActivity.this,OrderManagement.class);
                startActivity(stockManagement);
            }
        });
    }
}