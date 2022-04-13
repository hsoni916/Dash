package com.example.dash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    final Calendar calendar = Calendar.getInstance();
    Button NewCustomer;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewCustomer = findViewById(R.id.NewCustomer);
        NewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a popup to add customer details.
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(inflater!=null){
                    final View marginView = inflater.inflate(R.layout.customerform,null);
                    popupWindow = new PopupWindow(marginView, 800,600);
                    popupWindow.setAnimationStyle(R.style.popup_animation);
                    popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                    EditText dob = marginView.findViewById(R.id.dob_etv);
                    ImageButton CalendarButton = marginView.findViewById(R.id.datePicker);
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            String dobs = date+"/"+month+"/"+year;
                            dob.setText(dobs);
                        }
                    } ;
                    CalendarButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog dialog = new DatePickerDialog(view.getContext(), dateSetListener,
                                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH));
                            dialog.show();
                        }
                    });
                    Button cancel = marginView.findViewById(R.id.cancel);
                    Button save = marginView.findViewById(R.id.save);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
            }
        });
    }
}