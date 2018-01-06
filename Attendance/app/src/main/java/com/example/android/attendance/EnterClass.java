package com.example.android.attendance;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.attendance.Data.attContract.classInfo;
import com.example.android.attendance.Data.classDbHelper;

/**
 * Created by User on 6/25/2017.
 */

public class EnterClass extends AppCompatActivity {
    private EditText ClassName,SubjectName,StudentCount;
    private classDbHelper mDbHelper;
    private boolean up;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_class);

        ClassName = (EditText) findViewById(R.id.class_name);
        SubjectName = (EditText) findViewById(R.id.subject_name);
        StudentCount = (EditText) findViewById(R.id.student_total);
        mDbHelper = new classDbHelper(this);
        intent = getIntent();
        up=false;
        if(intent.getExtras()!=null){
            ClassName.setText(intent.getStringExtra("class_name"));
            SubjectName.setText(intent.getStringExtra("class_sub"));
            //StudentCount.setText(intent.getStringExtra("class_count"));
            up=true;
        }



    }

    //check function
    private boolean check(){
        boolean a=false;

        if(ClassName.getText().toString().equals("")||!ClassName.getText().toString().matches("[a-zA-Z0-9]*")){
            Toast.makeText(getApplicationContext(),"classname invalid:empty||no special characters allowed",Toast.LENGTH_SHORT).show();
            a=true;
        }
        if(SubjectName.getText().toString().equals("")||!SubjectName.getText().toString().matches("[a-zA-Z]*")){
            Toast.makeText(getApplicationContext(),"Subject invalid:empty||only characters allowed",Toast.LENGTH_SHORT).show();
            a=true;
        }
        if(StudentCount.getText().toString().equals("")||!StudentCount.getText().toString().matches("[0-9]*")){
            Toast.makeText(getApplicationContext(),"fill and only number allowed.",Toast.LENGTH_SHORT).show();
            a=true;
        }

        return a;
    }
    private boolean updateclass(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String cn = ClassName.getText().toString().trim();
        String cs = SubjectName.getText().toString().trim();
        Integer cc=0;
        try{
            cc = Integer.parseInt(StudentCount.getText().toString().trim());
        }catch (NumberFormatException e){

        }
        if(!cn.equals(intent.getStringExtra("class_name")) || !cs.equals(intent.getStringExtra("class_sub"))){
            Toast.makeText(getApplicationContext(),"Class Name and subject name Cannot be Modified!!",Toast.LENGTH_SHORT).show();
            ClassName.setText(intent.getStringExtra("class_name"));
            SubjectName.setText(intent.getStringExtra("class_sub"));
            return false;
        }
        else if(cc<intent.getIntExtra("class_count",0)){
            Toast.makeText(getApplicationContext(),"count only updated only if increment in total count",Toast.LENGTH_SHORT).show();
            cc = intent.getIntExtra("class_count",0);
        }

       // String old_table_name = intent.getStringExtra("class_name")+intent.getStringExtra("class_sub");
       // String rn = "RENAME TABLE " + old_table_name + " TO " + at + ";";
        //db.execSQL(rn);

        ContentValues values = new ContentValues();
        values.put(classInfo.COLUMN_COUNT,cc);

        String Selection = classInfo.COLUMN_CLASS_NAME + " LIKE ? AND " + classInfo.COLUMN_SUBJECT + " LIKE ?";
        String[] SelectionArgs = {cn,cs};

        int count = db.update(classInfo.TABLE_NAME,values,Selection,SelectionArgs);
        int addcount = intent.getIntExtra("class_count",0);
        addcount++;
        while(cc>=addcount){
            String addcol = "ALTER TABLE " + (cn+cs).toLowerCase() + " ADD R" + addcount + " INT NOT NULL DEFAULT 0 ;";
            db.execSQL(addcol);
            addcount++;
        }
        return true;
    }
    private void insertclass(){
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String cn = ClassName.getText().toString().trim();
            String cs = SubjectName.getText().toString().trim();
            Integer cc = Integer.parseInt(StudentCount.getText().toString().trim());
            String at = (cn + cs).toLowerCase();
            ContentValues values = new ContentValues();
            values.put(classInfo.COLUMN_CLASS_NAME, cn);
            values.put(classInfo.COLUMN_SUBJECT, cs);
            values.put(classInfo.COLUMN_COUNT, cc);
            values.put(classInfo.COLUMN_ATT_TABLE, at);

            //check whether data already exists or not
            String[] projections = {classInfo.COLUMN_CLASS_NAME,classInfo.COLUMN_SUBJECT,classInfo.COLUMN_COUNT};
            String Selection = classInfo.COLUMN_CLASS_NAME + " LIKE ? AND " + classInfo.COLUMN_SUBJECT + " LIKE ?";
            String selectionArgs[] = {cn, cs};
            Cursor c = db.query(classInfo.TABLE_NAME, projections, Selection, selectionArgs, null, null, null);
            if (!c.moveToNext()) {
                long rowId = db.insert(classInfo.TABLE_NAME, null, values);

                if (rowId == -1) {
                    Toast.makeText(this, "error saving data", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, "saved data successfully", Toast.LENGTH_SHORT).show();
                }
                String createAT = "CREATE TABLE " + at + "( _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,Date_of_att DATE NOT NULL );";

                db.execSQL(createAT);
                int i = 1;
                while (i <= cc) {
                    String addcol = "ALTER TABLE " + at + " ADD " + "R" + i + " INT NOT NULL DEFAULT 0 ;";
                    db.execSQL(addcol);
                    i++;
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Data already exists!!",Toast.LENGTH_SHORT).show();
            }

        }catch(NumberFormatException ae){
                Toast.makeText(getApplicationContext(), "Don't leave the area's blank", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.enterclass_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.save_class:
                if(!check()){
                    if(!up) {
                        insertclass();
                    }
                    else{
                        boolean itera;
                        while(true){
                            itera = updateclass();
                            if(itera){
                                break;
                            }
                        }
                    }
                    //finishes the activity
                    finish();
                }

                return true;
            case R.id.clear_all:
                ClassName.setText("");
                SubjectName.setText("");
                StudentCount.setText("");
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
}
