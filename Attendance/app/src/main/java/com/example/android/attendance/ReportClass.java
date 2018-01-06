package com.example.android.attendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.attendance.Data.classDbHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Cell;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by User on 7/1/2017.
 */

public class ReportClass extends AppCompatActivity {
    classDbHelper mDbHelper;
    String tablename;
    int count;
    ArrayList<String> report;
    ArrayAdapter<String> adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout);
        mDbHelper = new classDbHelper(this);
        Intent intent = getIntent();
        tablename = intent.getStringExtra("table_name");
        count = intent.getIntExtra("count",0);

        ActivityCompat.requestPermissions(ReportClass.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


        listView = (ListView) findViewById(R.id.class_report);
        //listView.setBackgroundColor(Color.rgb(0,0,0));

        report = new ArrayList<String>();
        readData();
        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.report_list,R.id.report_item,report);
        listView.setAdapter(adapter);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"Granted!!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Not Granted!!",Toast.LENGTH_SHORT).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void readData(){
        report.clear();
        SQLiteDatabase db= mDbHelper.getReadableDatabase();
        String date,absent="";
        String[] projections=new String[count+1];
        projections[0] = "Date_of_att";
        int i=1;
        while(i<=count){
            projections[i] = "R"+i;
            i++;
        }

        Cursor c = db.query(tablename,projections,null,null,null,null,null,null);

        while(c.moveToNext()){
            int dc = c.getColumnIndexOrThrow("Date_of_att");
            date = c.getString(dc);
            int s=1;
            while(s<=count){
                int index = c.getColumnIndex("R"+s);
                int att = c.getInt(index);
                if(att==0) {
                    absent = absent + s + ",";
                }
                s++;
            }
            report.add(date+ "\n :absent:" + absent );
            absent="";
        }
        c.close();
        try {
            adapter.notifyDataSetChanged();
        }
        catch (NullPointerException ae){
            ae.printStackTrace();
        }
    }
    private void PercentageReport(){
        report.clear();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int present=0;
        int total=0;
        String[] rollno = new String[1];
        int i=1;
        Cursor c;
        rollno[0] = "Date_of_att";
        c = db.query(tablename,rollno,null,null,null,null,null);
        total = c.getCount();
        while(i<=count){
            rollno[0] = "R" + i ;
            String select = "R"+i+ " LIKE ?";
            String[] selectionArgs = {"1"};
            c = db.query(tablename,rollno,select,selectionArgs,null,null,null);
            present = c.getCount();

            //Claculte percetage and add to report

            float per = ((float)present/(float)total)*100;

            String finalString = "ROLL NO.: " + i + "\n" + "PERCENTAGE ATTENDANCE: " + per + "%" ;

            report.add(finalString);

            i++;
        }
        c.close();
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.report_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.date_wise:
                readData();
                return true;
            case R.id.roll_wise:
                PercentageReport();
                return true;
            case R.id.pdf_report:
                try {
                    createPdf();
                }catch (WriteException e){
                    e.printStackTrace();
                }
                return  true;
        }
        return false;
    }
    public void createPdf() throws WriteException{
        String filename = "Report" + tablename + ".xls";

        File sdcard = Environment.getExternalStorageDirectory();

        File directory = new File(sdcard, "/Attendance");
        directory.mkdir();

        File file = new File(directory,filename);

        try{
            file.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("en","EN"));
        WritableWorkbook workbook=null;
        try {
            workbook = jxl.Workbook.createWorkbook(file,ws);
        }catch (IOException e){
            e.printStackTrace();
        }
        workbook.createSheet("Percentage",0);
        workbook.createSheet("dates",1);
        WritableCellFormat titleFormat = null;
        WritableCellFormat newFormat = null;
        try {
            WritableSheet sheet = workbook.getSheet(0);
            Cell readCell = sheet.getCell(0,0);
            WritableCellFormat readedFormat = new WritableCellFormat();
            CellFormat readFormat = readCell.getCellFormat() == null ? readedFormat : readCell.getCellFormat();
            newFormat = new WritableCellFormat(readFormat);
            newFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
            newFormat.setAlignment(Alignment.CENTRE);
            newFormat.setWrap(true);
            titleFormat = new WritableCellFormat(readedFormat);
            titleFormat.setAlignment(Alignment.CENTRE);
            titleFormat.setBorder(Border.ALL,BorderLineStyle.THIN);
            titleFormat.setBackground(Colour.YELLOW2);


            Label label1 = new Label(0, 0, "ROLL NO",titleFormat);
            sheet.addCell(label1);
            Label label2 = new Label(1, 0, "ATTENDED",titleFormat);
            sheet.addCell(label2);
            Label label3 = new Label(2, 0, "TOTAL",titleFormat);
            sheet.addCell(label3);
            Label label4 = new Label(3, 0, "PERCENTAGE",titleFormat);
            sheet.addCell(label4);

            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = db.query(tablename, null, null, null, null, null, null);
            int total = c.getCount();
            int i = 1;
            String[] rollno = {" "};
            while (i <= count) {
                rollno[0] = "R" + i;
                String selection = "R" + i + " LIKE ?";
                String[] selectionArgs = {"1"};

                c = db.query(tablename, rollno, selection, selectionArgs, null, null, null);
                int attend = c.getCount();
                float percentage = ((float) attend / (float) total) * 100;

                label1 = new Label(0, i, "" + i,newFormat);
                sheet.addCell(label1);
                label2 = new Label(1, i, "" + attend,newFormat);
                sheet.addCell(label2);
                label3 = new Label(2, i, "" + total,newFormat);
                sheet.addCell(label3);
                label4 = new Label(3, i, "" + percentage,newFormat);
                sheet.addCell(label4);
                i++;
            }

            WritableSheet sheet1 = workbook.getSheet(1);
            Label label = new Label(0,0,"Roll no.",titleFormat);
            sheet1.addCell(label);
            String[] projections=new String[count+1];
            projections[0] = "Date_of_att";
            i=1;
            while(i<=count){
                projections[i] = "R"+i;
                label = new Label(0,i,""+i,newFormat);
                sheet1.addCell(label);
                i++;
            }

            Cursor cursor = db.query(tablename,projections,null,null,null,null,null);
            i=1;
            while(cursor.moveToNext()){
                int dc = cursor.getColumnIndexOrThrow("Date_of_att");
                Label date = new Label(i,0,cursor.getString(dc),titleFormat);
                sheet1.addCell(date);
                int s=1;
                while(s<=count){
                    int index = cursor.getColumnIndex("R"+s);
                    int att = cursor.getInt(index);
                    if(att==1){
                        label = new Label(i,s,"P",newFormat);
                    }else{
                        label = new Label(i,s,"A",newFormat);
                    }
                    sheet1.addCell(label);
                    s++;
                }
                i++;
            }
            label2 = new Label(i, 0, "ATTENDED",titleFormat);
            sheet1.addCell(label2);
            label3 = new Label(i+1, 0, "TOTAL",titleFormat);
            sheet1.addCell(label3);
            label4 = new Label(i+2, 0, "PERCENTAGE",titleFormat);
            sheet1.addCell(label4);
            int s=1;
            while (s <= count) {
                rollno[0] = "R" + s;
                String selection = "R" + s + " LIKE ?";
                String[] selectionArgs = {"1"};

                c = db.query(tablename, rollno, selection, selectionArgs, null, null, null);
                int attend = c.getCount();
                float percentage = ((float) attend / (float) total) * 100;

                label2 = new Label(i, s, "" + attend,newFormat);
                sheet1.addCell(label2);
                label3 = new Label(i+1, s, "" + total,newFormat);
                sheet1.addCell(label3);
                label4 = new Label(i+2, s, String.format(Locale.getDefault(),"%.2f",percentage),newFormat);
                sheet1.addCell(label4);
                s++;
            }
            cursor.close();
            c.close();
        }catch (RowsExceededException e){
            e.printStackTrace();
        }
        try {
            workbook.write();
            workbook.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
