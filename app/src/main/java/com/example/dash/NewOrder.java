package com.example.dash;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
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
import java.util.concurrent.TimeUnit;

public class NewOrder extends AppCompatActivity {

    private DBManager dbManager;
    String name="", phone="";
    private String OrderName;
    private String OrderDeliveryDate;
    private String OrderOnDateString;
    Calendar calendar = Calendar.getInstance();
    private ArrayAdapter<String> ItemAdapter;
    private List<String> GenericItemsGold = new ArrayList<>();
    private List<String> GenericItemsSilver = new ArrayList<>();
    private List<String> Purity_Levels_Silver = new ArrayList<>();
    private List<String> Purity_Levels_Gold = new ArrayList<>();
    private List<String> ItemList = new ArrayList<>();
    private List<String> Carats = new ArrayList<>();
    private List<Double> Sizes = new ArrayList<>();
    private List<String> Pictures = new ArrayList<>();
    private List<Double> Weights = new ArrayList<>();
    private Uri image_uri;
    private RecyclerView ItemRecyclerView;
    boolean ExistingCustomer = false;


    private ImageButton ItemPicture;
    ActivityResultLauncher<Intent> GetJewelleryPicture = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){
                //Set thumbnail
                Log.d("IMAGE:",String.valueOf(image_uri));
                Toast.makeText(getBaseContext(),"Image saved.",Toast.LENGTH_LONG).show();
                if(ItemPicture!=null){
                    ItemPicture.setImageResource(R.drawable.check_green);
                }
            }
        }
    });
    private FirebaseFirestore firebaseFirestore;
    private List<String> NewCategory = new ArrayList<>();
    private List<String> RemarksList = new ArrayList<>();
    private TempOrderList OrderItemList;
    private ImageButton ItemSave;
    private AutoCompleteTextView ItemName;
    private AutoCompleteTextView ItemCarat;
    private EditText ItemWeight;
    private EditText ItemSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_order_1);
        OrderDetails NewOrderVariable = new OrderDetails();
        dbManager = new DBManager(this);
        dbManager.open();
        firebaseFirestore = FirebaseFirestore.getInstance();
        OrderItemList = new TempOrderList(ItemList, Weights, Carats, Sizes, new TempOrderList.OnItemClicked() {
            @Override
            public void DeleteThisItem(int position) {
                ItemList.remove(position);
                Carats.remove(position);
                Sizes.remove(position);
                Pictures.remove(position);
                OrderItemList.notifyItemRemoved(position);
            }

            @Override
            public void EditDetails(int position) {

                ItemName.setText(ItemList.get(position));
                ItemWeight.setText(String.valueOf(Weights.get(position)));
                ItemCarat.setText(Carats.get(position));
                ItemSize.setText(String.valueOf(Sizes.get(position)));
                image_uri = Uri.parse(Pictures.get(position));
                ItemPicture.setImageResource(R.drawable.check_green);
                ItemList.remove(position);
                Carats.remove(position);
                Sizes.remove(position);
                Pictures.remove(position);
                OrderItemList.notifyItemRemoved(position);

            }
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle("New Order");
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getBaseContext(),R.color.teal_500)));

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
        ItemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<String>() {{
                    addAll(GenericItemsGold);
                    addAll(GenericItemsSilver);
                }});
            //General Order Details Form
            AutoCompleteTextView NameEtv = findViewById(R.id.name_etv);
            AutoCompleteTextView PhoneEtv = findViewById(R.id.phone_etv);
            EditText OrderAdvance = findViewById(R.id.cash_etv);
            MaterialButtonToggleGroup AdvanceType = findViewById(R.id.MetalOrCash);
            MaterialButton cash = findViewById(R.id.cash);
            MaterialButton gram = findViewById(R.id.gram);
            AdvanceType.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                @Override
                public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                    if(checkedId == R.id.cash){
                        Log.d("Checked=","cash");

                    }else{
                        Log.d("Checked=","metal");
                    }
                }
            });
            cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cash.setBackgroundColor(getColor(R.color.teal_500));
                    cash.setTextColor(getColor(R.color.white));
                    gram.setBackgroundColor(getColor(R.color.white));
                    gram.setTextColor(getColor(R.color.black));
                    cash.setElevation(0);
                    gram.setElevation(10);
                    NewOrderVariable.setAdvanceType("Cash");
                }
            });
            gram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gram.setBackgroundColor(getColor(R.color.teal_500));
                    gram.setTextColor(getColor(R.color.white));
                    cash.setBackgroundColor(getColor(R.color.white));
                    cash.setTextColor(getColor(R.color.black));
                    cash.setElevation(10);
                    gram.setElevation(0);
                    NewOrderVariable.setAdvanceType("Metal");
                }
            });

            EditText ItemRemarks = findViewById(R.id.remarks_etv);
            EditText dateEtv = findViewById(R.id.date_etv);
            ImageButton OrderDateBtn = findViewById(R.id.datePicker);
            EditText deliveryDateEtv = findViewById(R.id.delivery_date_etv);
            ImageButton OrderDeliveryDateBtn = findViewById(R.id.delivery_datePicker);
            RecyclerView NewItemList = findViewById(R.id.OrderItemsList);
            Button Save_Order = findViewById(R.id.save);
            RadioGroup isRateFix = findViewById(R.id.RateChoices);
            ItemPicture = findViewById(R.id.CameraBtn);
            ChipGroup ModeOfPayments = findViewById(R.id.ModeOfPaymentChips);

        //General Order Details Form End

            //General details capture
            List<String> NameList = new ArrayList<>();
            List<String> PhoneList = new ArrayList<>();
            List<Customer> CustomerList = dbManager.getCustomerDetails();
            for(Customer customer:CustomerList){
                NameList.add(customer.getName());
                PhoneList.add(customer.getPhoneNumber());
            }
            ArrayAdapter<String> NameAdapter = new ArrayAdapter<>
                    (getBaseContext(), android.R.layout.select_dialog_item, NameList);
            ArrayAdapter<String> PhoneAdapter = new ArrayAdapter<>
                    (getBaseContext(), android.R.layout.select_dialog_item, PhoneList);
            NameEtv.setThreshold(2);
            NameEtv.setAdapter(NameAdapter);
            PhoneEtv.setThreshold(2);
            PhoneEtv.setAdapter(PhoneAdapter);
            NameEtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    name = NameAdapter.getItem(i);
                    int indextouse=0;
                    for(int nameindex = 0;nameindex<CustomerList.size();nameindex++){
                        if(CustomerList.get(nameindex).getName().equals(name))
                            indextouse = nameindex;
                    }
                    ExistingCustomer = true;
                    PhoneEtv.setText(PhoneList.get(indextouse));
                    phone = PhoneList.get(indextouse);
                    NameEtv.setEnabled(false);
                    PhoneEtv.setEnabled(false);
                    OrderName = name+"_"+phone;
                    NewOrderVariable.setCustomerName(name);
                    NewOrderVariable.setPhoneNumber(phone);
                }
            });
            PhoneEtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    phone = PhoneAdapter.getItem(i);
                    int indextouse=0;
                    for(int nameindex = 0;nameindex<CustomerList.size();nameindex++){
                        if(CustomerList.get(nameindex).getName().equals(phone))
                            indextouse = nameindex;
                    }
                    ExistingCustomer = true;
                    NameEtv.setText(NameAdapter.getItem(indextouse));
                    name = NameAdapter.getItem(indextouse);
                    NameEtv.setEnabled(false);
                    PhoneEtv.setEnabled(false);
                    NewOrderVariable.setCustomerName(name);
                    NewOrderVariable.setPhoneNumber(phone);
                }
            });
            EditText RateFixETV = findViewById(R.id.Rate);
            isRateFix.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i == R.id.RateFixed){
                        RateFixETV.setVisibility(View.VISIBLE);
                        NewOrderVariable.setRateFix(true);
                    }
                    if(i == R.id.RateUnFixed){
                        RateFixETV.setVisibility(View.GONE);
                        NewOrderVariable.setRateFix(false);
                    }
                }
            });
            RateFixETV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!RateFixETV.getText().toString().isEmpty()){
                        NewOrderVariable.setRate(Double.parseDouble(RateFixETV.getText().toString()));
                    }
                }
            });
            OrderDeliveryDate="";
            OrderOnDateString="";
            DatePickerDialog.OnDateSetListener OrderOnDateListener = (datePicker, year, month, date) -> {
                month = month+1;
                if(month<10){
                    OrderOnDateString = date+"/"+"0"+month+"/"+year;
                }else{
                    OrderOnDateString = date+"/"+month+"/"+year;
                }
                dateEtv.setText(OrderOnDateString);
                try {
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(OrderOnDateString);
                    Log.d("Date", String.valueOf(date1));
                    NewOrderVariable.setOrderDate(OrderOnDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            };
            DatePickerDialog.OnDateSetListener DeliveryDateListener = (datePicker, year, month, date) -> {
                month = month+1;
                if(month<10){
                    OrderDeliveryDate = date+"/"+"0"+month+"/"+year;
                }else{
                    OrderDeliveryDate = date+"/"+month+"/"+year;
                }
                deliveryDateEtv.setText(OrderDeliveryDate);
                try {
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(OrderDeliveryDate);
                    Log.d("Date", String.valueOf(date1));
                    NewOrderVariable.setDeliveryDate(OrderDeliveryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            };
            OrderDateBtn.setOnClickListener(view13 -> {
                DatePickerDialog dialog = new DatePickerDialog(view13.getContext(), OrderOnDateListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            });
            OrderDeliveryDateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dialog = new DatePickerDialog(view.getContext(), DeliveryDateListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }
            });

            ModeOfPayments.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                    for(int i =0;i<group.getChildCount();i++){
                        Chip chip = (Chip) group.getChildAt(i);
                        if(chip.isChecked()){
                            NewCategory.add(chip.getText().toString());
                        }
                    }
                    Log.d("Checked",NewCategory.toString());
                }
            });


        //General details capture end


            //Item Form
            ItemName = findViewById(R.id.ItemName);
            ItemWeight = findViewById(R.id.ItemWeight);
            ItemCarat = findViewById(R.id.ItemCarat);
            ItemSize = findViewById(R.id.ItemSize);
            ItemPicture = findViewById(R.id.CameraBtn);
            ItemSave = findViewById(R.id.SaveBtn);
            //Item Form End

            //Item Details Capture
            if(ItemAdapter!=null){
                ItemName.setThreshold(2);
                ItemName.setAdapter(ItemAdapter);
            }


            ItemCarat.setThreshold(2);
            ArrayAdapter<String> Purity_Adapter = new ArrayAdapter<>
                    (getBaseContext(), android.R.layout.simple_spinner_item,
                            new ArrayList<String>(){{addAll(Purity_Levels_Gold); addAll(Purity_Levels_Silver);}});
            Purity_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ItemCarat.setAdapter(Purity_Adapter);
            //Item details capture end

            //Capture ItemPicture
            ItemPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED){
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, 112);
                        }else{
                            openCamera();
                        }
                    }else{
                        openCamera();
                    }
                }
                private void openCamera() {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                    image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                    GetJewelleryPicture.launch(cameraIntent);
                    //ItemPicture Captured.
                }


            });
            ItemSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ItemName.setEnabled(false);
                    ItemCarat.setEnabled(false);
                    ItemSize.setEnabled(false);
                    ItemPicture.setEnabled(false);
                    if(RateFixETV.getVisibility()==View.VISIBLE){
                        RateFixETV.setEnabled(false);
                    }


                    //Save Item to list.
                    if(!ItemName.getText().toString().isEmpty() && !ItemSize.getText().toString().isEmpty() && !ItemCarat.getText().toString().isEmpty()){
                        ItemList.add(ItemName.getText().toString());
                        Sizes.add(Double.valueOf(ItemSize.getText().toString()));
                        Carats.add(ItemCarat.getText().toString());
                        if(image_uri!=null){
                            Pictures.add(String.valueOf(image_uri));
                        }else{
                            Pictures.add("");
                        }
                        if(!ItemWeight.getText().toString().isEmpty()){
                            Weights.add(Double.valueOf(ItemWeight.getText().toString()));
                        }
                        if(!OrderAdvance.getText().toString().isEmpty()){
                            NewOrderVariable.setAdvance(Double.valueOf(OrderAdvance.getText().toString()));
                        }

                    }else{

                    }
                    if(!ItemRemarks.getText().toString().isEmpty()){
                        RemarksList.add(ItemRemarks.getText().toString());
                    }
                    NewItemList.setAdapter(OrderItemList);
                    NewItemList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    NewItemList.setItemAnimator(new DefaultItemAnimator());
                    NewItemList.setVisibility(View.VISIBLE);

                    //Save Item to list end.
                    ItemName.setText(null);
                    ItemCarat.setText(null);
                    ItemPicture.setImageResource(R.drawable.camera);
                    ItemSize.setText(null);
                    ItemName.setEnabled(true);
                    ItemCarat.setEnabled(true);
                    ItemSize.setEnabled(true);
                    ItemPicture.setEnabled(true);

                }
            });

        Save_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ExistingCustomer){
                    dbManager.open();
                    long result = dbManager.quickInsertNewCustomer(NameEtv.getText().toString(),PhoneEtv.getText().toString());
                    if(result!=-1){
                        NewOrderVariable.setCustomerName(NameEtv.getText().toString());
                        name = name.replaceAll(" ","_");
                        name = name.replaceAll("-","_");
                        NewOrderVariable.setPhoneNumber(PhoneEtv.getText().toString());
                    }
                    dbManager.close();
                }
                String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                OrderName = OrderName+"_"+timeStamp+".json";
                NewOrderVariable.setOrderDate(dateFormat.format(Calendar.getInstance().getTime()));
                NewOrderVariable.setItems(ItemList);
                NewOrderVariable.setRemarks(RemarksList);
                NewOrderVariable.setWeights(Weights);
                NewOrderVariable.setSamplePhotos(Pictures);
                NewOrderVariable.setDeliveryDate(OrderDeliveryDate);
                NewOrderVariable.setOrderNumber(timeStamp);
                ItemName.setEnabled(false);
                ItemWeight.setEnabled(false);
                OrderAdvance.setEnabled(false);
                ItemRemarks.setEnabled(false);

                Gson gson = new Gson();
                String FileText = gson.toJson(NewOrderVariable);
                dbManager.open();
                if(dbManager.addRecord(name,phone,0.00,
                        Double.parseDouble(OrderAdvance.getText().toString()),
                        "Order deposit.",
                        dateFormat.format(Calendar.getInstance().getTime()),
                        NewCategory.toString())>0){
                    OrderName.replaceAll(" ","");
                    File SaveTheOrder = new File(getExternalFilesDir(null),OrderName);
                    FileOutputStream fileOutputStream = null;
                    try{
                        SaveTheOrder.createNewFile();
                        fileOutputStream = new FileOutputStream(SaveTheOrder,true);
                        fileOutputStream.write(FileText.getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Toast.makeText(view.getContext(),"Order saved.",Toast.LENGTH_LONG).show();
                        Log.d("File:",fileOutputStream.toString());
                        OrderName = OrderName.replace(".json","");
                        NewOrderVariable.setFileName(OrderName);
                        firebaseFirestore.collection("Orders")
                                .document(OrderName)
                                .set(NewOrderVariable).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(view.getContext(),"Order saved to server.",Toast.LENGTH_LONG).show();
                                        }else{
                                            //Send the document to a background worker thread.
                                        }
                                    }
                                });

                    }catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(view.getContext(),"Order not saved.",Toast.LENGTH_LONG).show();
                    }
                }
                dbManager.close();
            }
        });
        }
    }