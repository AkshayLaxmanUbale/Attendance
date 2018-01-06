package com.example.android.attendance;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.attendance.Data.classDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 7/11/2017.
 */

public class LateProxy extends AppCompatActivity {
    private classDbHelper mDbHelper;
    private DatePickerDialog mdatePickerDialog;
    private SimpleDateFormat simpleDateFormat;
    private String dates;
    private EditText date,roll,classs,sub;
    private RadioGroup radio;
    private Button submit;
    private int classcount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.late_proxy);
        mDbHelper = new classDbHelper(this);
        date =(EditText)findViewById(R.id.date);
        roll =(EditText)findViewById(R.id.roll);
        classs = (EditText)findViewById(R.id.class_update);
        sub = (EditText)findViewById(R.id.sub_update);
        radio = (RadioGroup)findViewById(R.id.group_att);

        submit = (Button)findViewById(R.id.submit_update);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            classs.setText(intent.getStringExtra("class_name"));
            sub.setText(intent.getStringExtra("sub_name"));
            classcount = intent.getIntExtra("class_count",0);
        }
        Calendar calendar = Calendar.getInstance();

        mdatePickerDialog = new DatePickerDialog(LateProxy.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newC = Calendar.getInstance();
                newC.set(year,month,dayOfMonth);
                dates = simpleDateFormat.format(newC.getTime());
                date.setText(dates);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdatePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()){
                    return;
                }
                if((classs.getText().toString().equals(""))||(date.getText().toString().equals(""))||(sub.getText().toString().equals(""))
                        ||(roll.getText().toString().equals(""))||(radio.getCheckedRadioButtonId()==0)){
                    Toast.makeText(getApplicationContext(),"Don't leave any field!!!",Toast.LENGTH_SHORT).show();
                }
                else {
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    int id = radio.getCheckedRadioButtonId();
                    int status = 0;
                    if (id == R.id.present) {
                        status = 1;
                    } else if (id == R.id.absent) {
                        status = 0;
                    }else{
                        Toast.makeText(getApplication(),"select absent or present",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ContentValues values = new ContentValues();
                    int rollno = Integer.parseInt(roll.getText().toString());
                    if(rollno<0||rollno>classcount){
                        Toast.makeText(getApplicationContext(),"roll no not exists: total count="+classcount,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    values.put("R" + rollno, status);

                    String selection = "Date_of_att LIKE ?";

                    String[] selectionArgs = {dates};
                    String tablename = (classs.getText().toString() + sub.getText().toString()).toLowerCase();

                    try{
                        db.query(tablename,null,null,null,null,null,null);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Invalid information about class and subject.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        db.update(tablename, values, selection, selectionArgs);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Date is not Correct.or Roll no not correct.check",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });
    }
    private boolean check(){
        boolean a=false;

        if(classs.getText().toString().equals("")||classs.getText().toString().matches("[a-zA-Z0-9]]")){
            Toast.makeText(getApplicationContext(),"classname invalid:empty||no special characters allowed",Toast.LENGTH_SHORT).show();
            a=true;
        }
        if(sub.getText().toString().equals("")||sub.getText().toString().matches("[a-zA-Z]]")){
            Toast.makeText(getApplicationContext(),"Subject invalid:empty||only characters allowed",Toast.LENGTH_SHORT).show();
            a=true;
        }
        try{
            Date datee = simpleDateFormat.parse(date.getText().toString());
        }catch (ParseException e){
            Toast.makeText(getApplicationContext(),"invalid date:click on field for date picker",Toast.LENGTH_SHORT).show();
            a=true;
        }

        return a;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
}
