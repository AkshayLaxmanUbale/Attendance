package com.example.android.attendance;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.attendance.Data.classDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by User on 7/1/2017.
 */

public class TakeAttendace extends AppCompatActivity {
    private classDbHelper mDbHelper;
    private ListView listView;
    private attAdapter adapter;
    private Button fsubmit;
    private String table_name;
    private int columns;
    private Intent intent;
    private ArrayList<attClass> list;
    private SimpleDateFormat simpleDateFormat;
    private String date;
    private String[] rollString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_attend);
        intent = getIntent();
        table_name = intent.getStringExtra("table_name");
        columns = intent.getIntExtra("column",0);
        list =new ArrayList<attClass>();
        listView =(ListView) findViewById(R.id.roll_list);
        adapter = new attAdapter(this,list);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        fsubmit = (Button) findViewById(R.id.submit_att);
        mDbHelper = new classDbHelper(this);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        rollString = new String[columns];
        int i=1;
        while(i<=columns){
            rollString[i-1]="R"+i;
            i++;
        }

        readData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list.get(position).getColor()== Color.parseColor("#f44336"))
                    list.get(position).setColor(Color.WHITE);
                else
                    list.get(position).setColor(Color.parseColor("#f44336"));
                adapter.notifyDataSetChanged();
            }
        });
        fsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                for (attClass a:list){
                    int aa=0;
                    if(a.getColor()== Color.parseColor("#f44336")){
                        aa=0;
                    }
                    else{
                        aa=1;
                    }
                    values.put("R"+a.getRollno(),aa);
                }
                String selection = "Date_of_att "+"like ?";
                String[] selectionArgs={date};
                db.update(table_name,values,selection,selectionArgs);

                finish();
            }
        });
    }
    private void readData(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        date = simpleDateFormat.format(calendar.getTime());
        String selection = "Date_of_att " + "like ?";
        String[] selectionArgs = {date};
        Cursor c = db.query(table_name,rollString,selection,selectionArgs,null,null,null);
        if(c.getCount()==0){
            ContentValues values = new ContentValues();
            values.put("Date_of_att",date);
            db.insert(table_name,null,values);
            int i=1;
            while(i<=columns){
                list.add(new attClass(i,Color.WHITE));
                i++;
            }

        }
        else {
            c.moveToNext();
            int i = 1;
            while (i <= columns) {
                int columnIndex = c.getColumnIndex("R" + i);
                int color;
                if (c.getInt(columnIndex) == 0) {
                    color = Color.parseColor("#f44336");
                } else {
                    color = Color.WHITE;
                }
                list.add(new attClass(i, color));
                i++;
            }
        }
        adapter.notifyDataSetChanged();
        c.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
    /*   private classDbHelper mDbHelper;
    private Intent intent;
    private String table_name;
    private int columns;
    private Button presenty,absenty;
    private ImageButton undo,redo;
    private EditText number;
    private Integer global;
    private SQLiteDatabase db;
    private String date;
    private int local=0;
    private boolean editcalled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_attend);
        mDbHelper = new classDbHelper(this);
        db = mDbHelper.getWritableDatabase();
        intent = getIntent();
        table_name = intent.getStringExtra("table_name");
        columns = intent.getIntExtra("column",0);

        presenty = (Button) findViewById(R.id.present);
        absenty = (Button) findViewById(R.id.absent);
        undo = (ImageButton) findViewById(R.id.back_by_one);
        redo = (ImageButton) findViewById(R.id.front_by_one);
        number = (EditText) findViewById(R.id.count);

        global =1;
        presenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editcalled){
                    local = Integer.parseInt(number.getText().toString());
                    String rp = "R" + local;
                    ContentValues values = new ContentValues();
                    values.put(rp, 0);

                    String selection = "Date_of_att" + " LIKE ?";
                    String[] selectionArgs = {date};

                    db.update(table_name, values, selection, selectionArgs);

                    number.setText(""+global);

                    editcalled=false;
                }else if(global<=columns){
                    String rp = "R"+global;
                    ContentValues values = new ContentValues();
                    values.put(rp,1);

                    String selection = "Date_of_att" + " LIKE ?";
                    String[] selectionArgs = {date};

                    db.update(table_name,values,selection,selectionArgs);
                    if(global==columns){
                        Toast.makeText(getApplicationContext(),"Attendance Complete!!",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        global++;
                        number.setText(""+global);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Attendace Completed!!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        absenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editcalled){
                    local = Integer.parseInt(number.getText().toString());
                    String rp = "R" + local;
                    ContentValues values = new ContentValues();
                    values.put(rp, 0);

                    String selection = "Date_of_att" + " LIKE ?";
                    String[] selectionArgs = {date};

                    db.update(table_name, values, selection, selectionArgs);

                    number.setText(""+global);

                    editcalled=false;
                }else if(global<=columns) {
                    String rp = "R" + global;
                    ContentValues values = new ContentValues();
                    values.put(rp, 0);

                    String selection = "Date_of_att" + " LIKE ?";
                    String[] selectionArgs = {date};

                    db.update(table_name, values, selection, selectionArgs);
                    if(global==columns){
                        Toast.makeText(getApplicationContext(),"Attendance Complete!!",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        global++;
                        number.setText(""+global);
                    }
                } else{
                    Toast.makeText(getApplicationContext(),"Attendance Complete!!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global>1)
                    global--;
                number.setText(""+global);
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(global!=columns)
                    global++;
                number.setText(""+global);
            }
        });
        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number.setText("");
                editcalled=true;
            }
        });
    }
    public boolean checkDate(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projections = {
        "Date_of_att"
        } ;
        Cursor c = db.query(table_name,projections,null,null,null,null,null);

        int cni = c.getColumnIndex("Date_of_att");
        while(c.moveToNext()){
            if(date.equals(c.getString(cni))){
                return true;
            }
        }
        c.close();

        return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        number.setText("1");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(calendar.getTime());
        if(!checkDate()) {
            ContentValues values = new ContentValues();
            values.put("Date_of_att", date);

            db.insert(table_name, null, values);
        }

    }*/
}
