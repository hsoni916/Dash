package com.example.dash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    List<String> GenericItemsGold = new ArrayList<>();
    List<String> GenericItemsSilver = new ArrayList<>();
    List<String> Purity_Levels_Gold = new ArrayList<>();
    List<String> Purity_Levels_Silver = new ArrayList<>();
    List<Hallmarking_Standards> hallmarking_standards = new ArrayList<>();
    ImageButton Add_Barcode_Item;
    PopupWindow popupWindow;
    Context context;
    private DBHelper dbHelper;
    ArrayAdapter<String> Supplier_Adapter;
    ContentValues contentValues = new ContentValues();
    String typeof = "";
    List<Long> itemids = new ArrayList<>();
    RecyclerView ItemList;
    ItemListAdapter adapter;
    ImageButton Print;
    List<SundryItem> sundryItemList = new ArrayList<>();
    double WeightToCalculateLabour, baseprice;
    PrintData printData = new PrintData();
    TextView error_invoice;
    int invoicecounter = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoiceform);
        Objects.requireNonNull(getSupportActionBar()).setTitle("New Invoice");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.teal_600)));
        context = getBaseContext();
        dbManager = new DBManager(this);
        dbManager.open();
        Names = findViewById(R.id.name_etv);
        PhoneNumber = findViewById(R.id.phone_etv);
        Particular = findViewById(R.id.particular);
        Add_Barcode_Item = findViewById(R.id.Add_item);
        ItemList = findViewById(R.id.item_list);
        Print = findViewById(R.id.Print);
        error_invoice = findViewById(R.id.error_invoice);
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

        List<String> CustomerLists = dbManager.ListAllCustomer();
        List<String> PhoneNumbers = dbManager.ListAllPhone();
        List<String> Birthdays = dbManager.ListDOB();
        ArrayAdapter<String> NameAdapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, CustomerLists);
        ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_item,PhoneNumbers);
        ArrayAdapter<String> DOBAdapter = new ArrayAdapter<>
                (this,android.R.layout.select_dialog_item,Birthdays);
        ArrayAdapter<String> ItemAdapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item,dbManager.ListAllItems());
        if(ItemAdapter.getCount()==0){
            ItemAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                    new ArrayList<String>(){{addAll(GenericItemsGold); addAll(GenericItemsSilver);}});
        }

        Names.setThreshold(2);
        Names.setAdapter(NameAdapter);

        PhoneNumber.setThreshold(1);
        PhoneNumber.setAdapter(PhoneAdapter);

        Particular.setThreshold(2);
        Particular.setAdapter(ItemAdapter);
        Names.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedName = NameAdapter.getItem(i);
            int indextouse = CustomerLists.indexOf(selectedName);
            Log.d("Data",i+ "--" +selectedName+ "--" +indextouse);
            printData.setCustomerName(CustomerLists.get(indextouse));
            PhoneNumber.setEnabled(false);
            PhoneNumber.setText(PhoneNumbers.get(indextouse));
            printData.setPhone(PhoneNumbers.get(indextouse));
            Log.d("Index", String.valueOf(indextouse));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM",Locale.getDefault());
            try {
                //Date date1=new SimpleDateFormat("dd-MM", Locale.getDefault()).parse(String.valueOf(DOBAdapter.getItem(i)));
                Date date1 = dateFormat.parse(String.valueOf(Birthdays.get(indextouse)));
                Date c = Calendar.getInstance().getTime();
                Date today= dateFormat.parse(dateFormat.format(c));
                if(today!=null && date1!=null){
                    Log.d("Date comparison:",today + ":" + date1);
                }
                assert date1 != null;
                if(date1.equals(today)){
                    Toast.makeText(getBaseContext(),"Customer's Birthday.",Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        ArrayAdapter<String> finalItemAdapter = ItemAdapter;
        Particular.setOnItemClickListener((adapterView, view, i, l) -> {
            if(GenericItemsGold.contains(finalItemAdapter.getItem(i)) || GenericItemsSilver.contains(finalItemAdapter.getItem(i))){
                Log.d("Item :","Generic");
                Add_Barcode_Item.setEnabled(false);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(inflater!=null){
                    final View marginView = inflater.inflate(R.layout.generic_popup,null);
                    popupWindow = new PopupWindow(marginView, 1200,800);
                    popupWindow.setAnimationStyle(R.style.popup_animation);
                    popupWindow.showAtLocation(Particular.getRootView(), Gravity.CENTER,0,0);
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    Spinner Purity;
                    Spinner Supplier;
                    TextView Category;
                    EditText GrossWeight, LessWeight, NetWeight, ExtraCharges, Wastage;
                    TextView BasePurity;
                    Purity = marginView.findViewById(R.id.purity_etv);
                    Category = marginView.findViewById(R.id.category_etv);
                    Supplier = marginView.findViewById(R.id.supplier_etv);

                    GrossWeight = marginView.findViewById(R.id.weight_etv);
                    LessWeight = marginView.findViewById(R.id.less_weight_etv);
                    NetWeight = marginView.findViewById(R.id.net_weight_etv);
                    ExtraCharges = marginView.findViewById(R.id.charges_etv);
                    Wastage = marginView.findViewById(R.id.touch_etv);

                    BasePurity = marginView.findViewById(R.id.Label7);
                    TextView NameError = marginView.findViewById(R.id.NameError);
                    Button save,clear;
                    save = marginView.findViewById(R.id.save_item);
                    clear = marginView.findViewById(R.id.clear_item);

                    ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<>
                            (marginView.getContext(), android.R.layout.simple_spinner_item,
                                    new ArrayList<String>(){{addAll(Purity_Levels_Gold); addAll(Purity_Levels_Silver);}});
                    Purity_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    Purity.setAdapter(Purity_Adapter);
                    Purity.setSelection(0);
                    Category.setEnabled(false);
                    Category.setText(finalItemAdapter.getItem(i));
                    if(dbManager.ListAllSuppliers().size()>0){
                        Supplier_Adapter = new ArrayAdapter<>
                                (marginView.getContext(), android.R.layout.simple_spinner_item,
                                        dbManager.ListAllSuppliers());
                    }
                    db.collection("Suppliers").get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            List<String> supplierList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Supplier NewSupplier = doc.toObject(Supplier.class);
                                supplierList.add(NewSupplier.getBusiness());
                            }
                            Supplier_Adapter = new ArrayAdapter<>(marginView.getContext(), android.R.layout.simple_spinner_dropdown_item,
                                    supplierList);
                            Supplier_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            Supplier.setAdapter(Supplier_Adapter);
                        }
                    });


                    Purity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            typeof =Purity.getItemAtPosition(i).toString();
                            switch (i){
                                case 0:
                                    BasePurity.setText("99");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 1:
                                    BasePurity.setText("96");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 2:
                                    BasePurity.setText("92");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 3:
                                    BasePurity.setText("88");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 4:
                                    BasePurity.setText("84");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 5:
                                    BasePurity.setText("76");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 6:
                                    BasePurity.setText("59");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 8: BasePurity.setText("92.5");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 9: BasePurity.setText("72");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 10:
                                case 11:
                                    BasePurity.setText("62");
                                    BasePurity.setEnabled(false);
                                    break;
                                case 7:
                                case 12:
                                    BasePurity.setEnabled(true);
                                    break;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });


                    //if(Supplier_Adapter.getCount()==0)
               /*     Supplier.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if(!b){
                                if(Supplier.getSelectedItem().toString().length()>=3){
                                    if(!Supplier_Adapter.toString().contains(Supplier.getSelectedItem().toString())){
                                        //Bubble to add that to the
                                        ConstraintLayout add_supplier_layout = marginView.findViewById(R.id.supplier_add_layout);
                                        add_supplier_layout.setVisibility(View.VISIBLE);
                                        Button Add,Close;
                                        Add = marginView.findViewById(R.id.add_supplier);
                                        Close = marginView.findViewById(R.id.don't_add_supplier);
                                        Add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                long result = dbManager.AddNewSupplier(Supplier.getSelectedItem().toString());
                                                if(result!=-1){
                                                    TextView Attention_text = marginView.findViewById(R.id.Attention_text);
                                                    Attention_text.setText(R.string.supplier_success);
                                                    Attention_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.success, 0, 0, 0);
                                                    Attention_text.setCompoundDrawablePadding(4);
                                                    final Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Do something after 5s = 5000ms
                                                            add_supplier_layout.setVisibility(View.GONE);
                                                        }
                                                    }, 2500);
                                                }
                                            }
                                        });

                                        Close.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                add_supplier_layout.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }
                            }else{
                                if(!Supplier.getSelectedItem().toString().isEmpty()){
                                    TextView attention = marginView.findViewById(R.id.Attention_text_1);
                                    if(attention.getVisibility()==View.INVISIBLE || attention.getVisibility()==View.GONE){
                                        attention.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    TextView attention = marginView.findViewById(R.id.Attention_text_1);
                                    attention.setVisibility(View.GONE);
                                }
                            }
                        }
                    }); */
                    //Receive input from user and save it.
                    //in a separate database as to avoid adding it to inventory.

                    GrossWeight.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if(!GrossWeight.getText().toString().isEmpty()){
                                if(!LessWeight.getText().toString().isEmpty()){
                                    double diff = Double.parseDouble(GrossWeight.getText().toString())-Double.parseDouble(LessWeight.getText().toString());
                                    NetWeight.setText(String.format(Locale.getDefault(),"%.2f",diff));
                                }else{
                                 NetWeight.setText(GrossWeight.getText().toString());
                                }
                            }else{
                                NetWeight.setText(null);
                                if(NameError.getVisibility()==View.VISIBLE){
                                    NameError.setText(null);
                                    NameError.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    });

                    LessWeight.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if(!GrossWeight.getText().toString().isEmpty()){
                                double GW = Double.parseDouble(GrossWeight.getText().toString());
                                if(!LessWeight.getText().toString().isEmpty()){
                                    try{
                                        double LW = Double.parseDouble(LessWeight.getText().toString());
                                        if(LW<GW){
                                            save.setEnabled(true);
                                            double NW = GW-LW;
                                            NetWeight.setText(String.format(Locale.getDefault(),"%.3f", NW));
                                        }else{
                                            save.setEnabled(false);
                                            Toast.makeText(getBaseContext(),"Invalid weight",Toast.LENGTH_LONG).show();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    NetWeight.setText(GrossWeight.getText().toString());
                                }
                            }
                        }
                    });

                    LessWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if(!b){
                                if(LessWeight.getText().toString().isEmpty()){
                                    double zero =0;
                                    LessWeight.setText(String.format(Locale.getDefault(),"%.3f",zero));
                                }
                            }
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                //    + "Wastage" + " DECIMAL(3,3) NOT NULL,"
                                //    + "ExtraCharges" + " INTEGER,"
                                //    + "ADD_INFO" + " STRING,"
                                 //   + "Status" + " INTEGER NOT NULL DEFAULT 0 CHECK ( Status IN (-1,0,1))"
                            contentValues = new ContentValues();
                            contentValues.put("Name",Category.getText().toString());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                            contentValues.put("Date_Of",dateFormat.format(Calendar.getInstance().getTime()));
                            String example = dateFormat.format(Calendar.getInstance().getTime());
                            if(!GrossWeight.getText().toString().isEmpty()){
                                contentValues.put("GrossWeight",Double.parseDouble(GrossWeight.getText().toString()));
                                if(!LessWeight.getText().toString().isEmpty()){
                                    try{
                                        contentValues.put("LessWeight",Double.parseDouble(LessWeight.getText().toString()));
                                        contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString())-Double.parseDouble(LessWeight.getText().toString()));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        contentValues.put("LessWeight",0.00);
                                        contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString()));
                                    }
                                }else{
                                    contentValues.put("LessWeight",0.00);
                                    contentValues.put("NetWeight",Double.parseDouble(GrossWeight.getText().toString()));
                                }
                                contentValues.put("TypeOfArticle",typeof);
                                if(!Wastage.getText().toString().isEmpty()){
                                    try{
                                        contentValues.put("Wastage",Double.parseDouble(Wastage.getText().toString())
                                                +Double.parseDouble(BasePurity.getText().toString()));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    contentValues.put("Wastage",0.00);
                                }
                                if(!ExtraCharges.getText().toString().isEmpty()){
                                    try{
                                        contentValues.put("ExtraCharges",Double.parseDouble(ExtraCharges.getText().toString()));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                dbHelper = new DBHelper(marginView.getContext());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                long id = db.insert("Sundry_Supplies", null,contentValues);
                                if(id!=-1){
                                    Toast.makeText(marginView.getContext(),"Item Saved",Toast.LENGTH_LONG).show();
                                    AddItem(id);
                                    popupWindow.dismiss();
                                }else{
                                    Toast.makeText(marginView.getContext(),"Item Save Unsuccessful",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                NameError.setText(R.string.gross_weight_error);
                                NameError.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }else{
                Add_Barcode_Item.setEnabled(true);

                //Fetch details and add it to the recyclerview.
            }
        });

        Print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Print.setEnabled(false);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(inflater!=null){
                    final View paymentview = inflater.inflate(R.layout.payment,null);
                    PopupWindow paymentWindow;
                    paymentWindow = new PopupWindow(paymentview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    paymentWindow.setAnimationStyle(R.style.popup_animation);
                    paymentWindow.showAtLocation(Particular.getRootView(), Gravity.CENTER,0,0);
                    paymentWindow.setFocusable(true);
                    paymentWindow.update();
                    ImageButton CashHolder,UPIHolder,PaytmHolder,CardHolder;
                    CashHolder = paymentview.findViewById(R.id.cashlayout);
                    UPIHolder = paymentview.findViewById(R.id.upilayout);
                    PaytmHolder = paymentview.findViewById(R.id.paytmlayout);
                    CardHolder = paymentview.findViewById(R.id.cardlayout);

                    TextView ErrorPayment;
                    ErrorPayment = paymentview.findViewById(R.id.error_payment);
                    String[] Mode = new String[]{};
                    List<String> MOP = new ArrayList<>();

                    EditText Discount = paymentview.findViewById(R.id.Discount_etv);
                    CashHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CashHolder.setSelected(!CashHolder.isSelected());
                            if(CashHolder.isSelected()){
                                MOP.add(getString(R.string.ModeCash));
                                Log.d("Selected","true");
                            }else{
                                if(MOP.size()>0){
                                    MOP.remove(getString(R.string.ModeCash));
                                }
                                Log.d("Selected","false");
                            }
                        }
                    });
                    UPIHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UPIHolder.setSelected(!UPIHolder.isSelected());
                            if(UPIHolder.isSelected()){
                                MOP.add(getString(R.string.ModeUPI));
                            }else{
                                if(MOP.size()>0){
                                    MOP.remove(getString(R.string.ModeUPI));
                                }
                            }
                        }
                    });
                    PaytmHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PaytmHolder.setSelected(!PaytmHolder.isSelected());
                            if(PaytmHolder.isSelected()){
                                MOP.add(getString(R.string.ModePaytm));
                            }else{
                                if(MOP.size()>0){
                                    MOP.remove(getString(R.string.ModePaytm));
                                }
                            }
                        }
                    });
                    CardHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CardHolder.setSelected(!CardHolder.isSelected());
                            if(CardHolder.isSelected()){
                                MOP.add(getString(R.string.ModeCard));
                            }else{
                                if(MOP.size()>0){
                                    MOP.remove(getString(R.string.ModeCard));
                                }
                            }
                        }
                    });

                    Button SavePayment,ClosePayment;
                    SavePayment = paymentview.findViewById(R.id.save_payment);
                    ClosePayment = paymentview.findViewById(R.id.close_payment);

                    SavePayment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!Discount.getText().toString().isEmpty()){
                                printData.setDiscount(Double.parseDouble(Discount.getText().toString()));
                            }else{
                                printData.setDiscount(0.00);
                            }
                            if(MOP.size()>0){
                                printData.setMOP(MOP);
                                ErrorPayment.setVisibility(View.GONE);
                                if(!printData.getCustomerName().isEmpty() && !printData.getPhone().isEmpty()){
                                    ProgressBar progressBar = findViewById(R.id.progress_circular);
                                    progressBar.setVisibility(View.VISIBLE);
                                    if(sundryItemList!=null && sundryItemList.size()!=0){
                                        printData.setSundryItemList(sundryItemList);

                                        String s = DateFormat.format("dd/MM/yyy", new java.util.Date()).toString();
                                        printData.setDate(s);
                                        Calendar calendar = Calendar.getInstance();
                                        dbManager.addCounter(calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR));
                                        invoicecounter = invoicecounter + dbManager.getCounter(calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
                                        printData.setBillNo(s.replaceAll("/","") + invoicecounter);
                                        db.collection("Invoices").add(printData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("Document:",documentReference.toString());

                                                if(error_invoice.getVisibility() == View.VISIBLE){
                                                    error_invoice.setVisibility(View.GONE);
                                                }
                                                sundryItemList = new ArrayList<>();
                                                printData = new PrintData();
                                                Names.setText(null);
                                                PhoneNumber.setText(null);
                                                Particular.setText(null);
                                                Print.setEnabled(true);
                                                ItemList.setAdapter(null);
                                                itemids.clear();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Failure","True");
                                                e.printStackTrace();
                                                error_invoice.setVisibility(View.VISIBLE);
                                                Print.setEnabled(true);
                                                ItemList.setAdapter(null);
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                Print.setEnabled(true);
                                                progressBar.setVisibility(View.GONE);
                                                paymentWindow.dismiss();
                                            }
                                        });
                                    }
                                }
                            }else{
                                ErrorPayment.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    ClosePayment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ErrorPayment.setVisibility(View.GONE);
                            MOP.clear();
                            paymentWindow.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void AddItem(long id) {
        itemids.add(id);
        dbHelper = new DBHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        sundryItemList = new ArrayList<>();
            for(int i=0;i<itemids.size();i++){
                String sql = "SELECT * FROM Sundry_Supplies WHERE rowid = " + itemids.get(i);
                Cursor fetch = db.rawQuery(sql,null);
                while (fetch.moveToNext() && !fetch.isAfterLast()){
                    int CI;
                    SundryItem newitem = new SundryItem();
                    CI = fetch.getColumnIndex("Name");
                    newitem.setName(fetch.getString(CI));
                    CI = fetch.getColumnIndex("GrossWeight");
                    newitem.setGW(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("LessWeight");
                    newitem.setLW(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("NetWeight");
                    newitem.setNW(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("ExtraCharges");
                    newitem.setEC(fetch.getDouble(CI));
                    CI = fetch.getColumnIndex("TypeOfArticle");
                    newitem.setTypeOf(fetch.getString(CI));
                    CI = fetch.getColumnIndex("Wastage");
                    newitem.setWastage(fetch.getDouble(CI));
                    sundryItemList.add(newitem);
                }
            }
            adapter = new ItemListAdapter(sundryItemList, new ItemListAdapter.OnItemClicked() {
                @Override
                public void DeleteThisItem(int position) {
                    sundryItemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    if(sundryItemList.size()==0){
                        Print.setVisibility(View.GONE);
                    }else{
                        Print.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void AddDetails(int position) {
                    //PopUp.
                    SundryItem item  = sundryItemList.get(position);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if(inflater!=null){
                        final View DetailsView = inflater.inflate(R.layout.details_popup,null);
                        popupWindow = new PopupWindow(DetailsView, 1200,800);
                        popupWindow.setAnimationStyle(R.style.popup_animation);
                        popupWindow.showAtLocation(Particular.getRootView(), Gravity.CENTER,0,0);
                        popupWindow.setFocusable(true);
                        popupWindow.update();
                        RadioGroup LabourType;
                        Button Save,Clear;
                        TextView MetalLabel;
                        MetalLabel = DetailsView.findViewById(R.id.MetalPrice);
                        MetalLabel.setText(item.getTypeOf());
                        EditText MetalPrice = DetailsView.findViewById(R.id.MetalPriceET);
                        LabourType = DetailsView.findViewById(R.id.Labour_Type);
                        EditText Labour,ExtraCharges;
                        Labour = DetailsView.findViewById(R.id.ET_Labour);
                        ExtraCharges = DetailsView.findViewById(R.id.ET_extra);
                        TextView LabourLabel,LECLabel,TotalLabourLabel,LabourEcHolder;
                        LabourLabel = DetailsView.findViewById(R.id.TotalLabourLabel);
                        LECLabel = DetailsView.findViewById(R.id.TotalWEC);
                        TotalLabourLabel = DetailsView.findViewById(R.id.TotalLabourTV);
                        LabourEcHolder = DetailsView.findViewById(R.id.LEC);
                        ConstraintLayout LabourSubType1 = DetailsView.findViewById(R.id.Labour_Sub_Type_1);
                        if(item.getLabourType()!=null){
                            if(item.getLabourType().contentEquals("PerGram")){
                                LabourType.clearCheck();
                                LabourType.check(R.id.pergm);
                            }else if(item.getLabourType().contentEquals("Percent")){
                                LabourType.check(R.id.Percent);
                            }else if(item.getLabourType().contentEquals("LumpSum")){
                                LabourType.check(R.id.lumpsum);
                            }
                        }
                        if(item.getRate()!=0){
                            MetalPrice.setText(String.valueOf(item.getRate()));
                        }
                        if(item.getEC()!=0){
                            ExtraCharges.setText(String.valueOf(item.getEC()));
                        }
                        if(item.getLabour()!=0){
                            TotalLabourLabel.setVisibility(View.VISIBLE);
                            TotalLabourLabel.setText(String.valueOf(item.getLabour()));
                            if(item.getEC()!=0){
                                LabourEcHolder.setVisibility(View.VISIBLE);
                                LabourEcHolder.setText(String.valueOf(item.getLabour() + item.getEC()));
                            }else{
                                LabourEcHolder.setVisibility(View.GONE);
                            }
                        }else{
                            TotalLabourLabel.setVisibility(View.GONE);
                        }
                        LabourType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                View view;
                                Labour.setText(null);
                                if(i == R.id.pergm){
                                    item.setLabourType("PerGram");
                                    RadioGroup LabourSubType;
                                    LabourSubType1.removeAllViews();
                                    Log.d("PerGm","True");
                                    view = getLayoutInflater().inflate(R.layout.labourpergram2,null);
                                    LabourSubType1.addView(view);
                                    LabourSubType1.setVisibility(View.VISIBLE);
                                    LabourSubType = view.findViewById(R.id.Labour_Sub_Type);
                                    MetalPrice.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            try{
                                                baseprice= Double.parseDouble(MetalPrice.getText().toString());
                                                item.setRate(baseprice);
                                            }catch (Exception e){e.printStackTrace();}
                                        }
                                    });
                                    LabourSubType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            Labour.setText(null);
                                            if(i==R.id.WithGW){
                                                WeightToCalculateLabour = item.getGW();
                                            }
                                            if(i==R.id.WithNW){
                                                WeightToCalculateLabour = item.getNW();
                                            }
                                            Log.d("Weight:",String.valueOf(WeightToCalculateLabour));
                                        }
                                    });
                                    Labour.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            Log.d("pergm labour","true");
                                            if(!Labour.getText().toString().isEmpty()) {
                                                Log.d("pergm labour not empty","true");
                                                    double value = Double.parseDouble(Labour.getText().toString()) * WeightToCalculateLabour;
                                                    double total = value + item.getEC();
                                                    Log.d("Values:",value+"--"+total);
                                                    TotalLabourLabel.setVisibility(View.VISIBLE);
                                                    LabourEcHolder.setVisibility(View.VISIBLE);
                                                    TotalLabourLabel.setText(String.format(Locale.getDefault(), "%.2f", value));
                                                    LabourEcHolder.setText(String.format(Locale.getDefault(), "%.2f", total));
                                            }else{
                                                TotalLabourLabel.setText("");
                                                LabourEcHolder.setText("");
                                                TotalLabourLabel.setVisibility(View.GONE);
                                                LabourEcHolder.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                else if(i == R.id.Percent){
                                    item.setLabourType("Percent");
                                    LabourSubType1.removeAllViews();
                                    Log.d("Percent","True");
                                    MetalPrice.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            try{
                                                baseprice= Double.parseDouble(MetalPrice.getText().toString());
                                                item.setRate(baseprice);
                                            }catch (Exception e){e.printStackTrace();}
                                        }
                                    });

                                    Labour.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            try {
                                                double valueinpercent = (Double.parseDouble(Labour.getText().toString())*baseprice)/1000;
                                                double finallabour = valueinpercent*item.getNW();
                                                LabourLabel.setVisibility(View.VISIBLE);
                                                LECLabel.setVisibility(View.VISIBLE);
                                                TotalLabourLabel.setVisibility(View.VISIBLE);
                                                LabourEcHolder.setVisibility(View.VISIBLE);
                                                TotalLabourLabel.setText(String.format(Locale.getDefault(),"%.2f", finallabour));
                                                LabourEcHolder.setText(String.format(Locale.getDefault(),"%.2f", finallabour + item.getEC()));
                                                Log.d("Percent Labour",String.valueOf(finallabour));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                                else if(i == R.id.lumpsum){
                                    item.setLabourType("LumpSum");
                                    Log.d("Lumpsum","True");
                                    LabourSubType1.removeAllViews();
                                    LabourSubType1.setVisibility(View.GONE);
                                    MetalPrice.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            try{
                                                baseprice= Double.parseDouble(MetalPrice.getText().toString());
                                                item.setRate(baseprice);
                                            }catch (Exception e){e.printStackTrace();}
                                        }
                                    });
                                    Labour.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                            try{
                                                double value = Double.parseDouble(Labour.getText().toString()) + item.getEC();
                                                TotalLabourLabel.setText(Labour.getText().toString());
                                                LabourEcHolder.setText(String.format(Locale.getDefault(),"%.2f",value));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        Save = DetailsView.findViewById(R.id.save_item);
                        Clear = DetailsView.findViewById(R.id.clear_item);
                        Save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!TotalLabourLabel.getText().toString().isEmpty()){
                                   item.setLabour(Double.parseDouble(TotalLabourLabel.getText().toString()));
                                   popupWindow.dismiss();
                                   Print.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(view.getContext(),"Labour details invalid",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        Clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LabourType.clearCheck();
                                LabourSubType1.removeAllViews();
                                Labour.setText(null);
                                TotalLabourLabel.setText(null);
                                LabourEcHolder.setText(null);
                            }
                        });
                    }
                }
            });
            ItemList.setAdapter(adapter);
            ItemList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            ItemList.setItemAnimator(new DefaultItemAnimator());
            ItemList.setVisibility(View.VISIBLE);
    }
}
