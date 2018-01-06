/*

 * Copyright (C) 2016 The Android Open Source Project

 *

 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.

 * You may obtain a copy of the License at

 *

 *      http://www.apache.org/licenses/LICENSE-2.0

 *

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */

package com.example.android.attendance;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.attendance.Data.attContract;
import com.example.android.attendance.Data.classDbHelper;

import java.util.ArrayList;

import static com.example.android.attendance.R.id.delete_class;

/**

 * Displays list of pets that were entered and stored in the app.

 */

public class MainActivity extends AppCompatActivity {


    private classDbHelper mDbHelper;
    private ListView listView;
    private ArrayList<ClassInfo> Classes;
    private classAdapter adapter;
    TextView tv;
    private ClassInfo SelectedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mDbHelper = new classDbHelper(this);
        Classes = new ArrayList<ClassInfo>();
        listView = (ListView) findViewById(R.id.list_view_class);
        tv = (TextView) findViewById(R.id.initial_msg);
        tv.setText("Insert classes into database");

        adapter = new classAdapter(MainActivity.this,Classes);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        // Setup FAB to open EditorActivity

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, EnterClass.class);
                startActivity(intent);

            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,TakeAttendace.class);
                intent.putExtra("table_name",Classes.get(position).getCtable());
                intent.putExtra("column",Classes.get(position).getCcount());
                startActivity(intent);
            }
        });

        registerForContextMenu(listView);

       //displayDatabaseInfo();

    }

    public void readData(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projections = {
           attContract.classInfo.COLUMN_CLASS_NAME,
                attContract.classInfo.COLUMN_SUBJECT,
                attContract.classInfo.COLUMN_COUNT,
                attContract.classInfo.COLUMN_ATT_TABLE
        } ;
        Cursor c = db.query(attContract.classInfo.TABLE_NAME,projections,null,null,null,null,null);

        int cni = c.getColumnIndex(attContract.classInfo.COLUMN_CLASS_NAME);
        int csi = c.getColumnIndex(attContract.classInfo.COLUMN_SUBJECT);
        int cci = c.getColumnIndex(attContract.classInfo.COLUMN_COUNT);
        int cti = c.getColumnIndex(attContract.classInfo.COLUMN_ATT_TABLE);
        while(c.moveToNext()){
            Classes.add(new ClassInfo(c.getString(cni),c.getString(csi),c.getInt(cci),c.getString(cti)));
        }
        c.close();
    }

    /**
     * Called when a context menu for the {@code view} is about to be shown.
     * Unlike {@link #onCreateOptionsMenu(Menu)}, this will be called every
     * time the context menu is about to be shown and should be populated for
     * the view (or item inside the view for subclasses,
     * this can be found in the {@code menuInfo})).
     * <p>
     * Use {@link #onContextItemSelected(MenuItem)} to know when an
     * item has been selected.
     * <p>
     * It is not safe to hold onto the context menu after this method returns.
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu,menu);
    }

    /**
     * This hook is called whenever an item in a context menu is selected. The
     * default implementation simply returns false to have the normal processing
     * happen (calling the item's Runnable or sending a message to its Handler
     * as appropriate). You can use this method for any items for which you
     * would like to do processing without those other facilities.
     * <p>
     * Use {@link MenuItem#getMenuInfo()} to get extra information set by the
     * View that added this menu item.
     * <p>
     * Derived classes should call through to the base class for it to perform
     * the default menu handling.
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SelectedClass = Classes.get(info.position);
        switch (item.getItemId()){
            case R.id.update_class:
                updateclass();
                return true;
            case delete_class:
                DialogAlert f = DialogAlert.newInstance("Do You Want To Delete This Class",0);
                f.show(getFragmentManager(),"dialog");
                return true;
            case R.id.report:
                display_report();
                return true;
            case R.id.lateatt:
                Intent intent = new Intent(MainActivity.this,LateProxy.class);
                intent.putExtra("class_name",SelectedClass.getCname());
                intent.putExtra("sub_name",SelectedClass.getCsub());
                intent.putExtra("class_count",SelectedClass.getCcount());
                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    public void display_report( ){
        Intent intent = new Intent(MainActivity.this,ReportClass.class);
        intent.putExtra("table_name",SelectedClass.getCtable());
        intent.putExtra("count",SelectedClass.getCcount());
        startActivity(intent);
    }
    public void updateclass( ){
        Intent intent = new Intent(MainActivity.this,EnterClass.class);
        intent.putExtra("class_name",SelectedClass.getCname());
        intent.putExtra("class_sub",SelectedClass.getCsub());
        intent.putExtra("class_count",SelectedClass.getCcount());

        startActivity(intent);
    }
    public void deleteclass( ){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = attContract.classInfo.COLUMN_CLASS_NAME + " LIKE ? AND " + attContract.classInfo.COLUMN_SUBJECT +
                " LIKE ?";

        String[] selectionArgs = {SelectedClass.getCname(),SelectedClass.getCsub()};

        db.delete(attContract.classInfo.TABLE_NAME,selection,selectionArgs);

        Classes.clear();
        readData();
        adapter.notifyDataSetChanged();
        if(Classes.isEmpty()){
            tv.setVisibility(View.VISIBLE);
        }

    }
    /**

     * Temporary helper method to display information in the onscreen TextView about the state of

     * the pets database.

     */

   /* private void displayDatabaseInfo() {

        // To access our database, we instantiate our subclass of SQLiteOpenHelper

        // and pass the context, which is the current activity.

        classDbHelper mDbHelper = new classDbHelper(this);



        // Create and/or open a database to read from it

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
          classInfo.COLUMN_CLASS_NAME,
                classInfo.COLUMN_SUBJECT,
                classInfo.COLUMN_COUNT
        };

        Cursor cursor = db.query(classInfo.TABLE_NAME,projection,null,null,null,null,null);

        try {

            // Display the number of rows in the Cursor (which reflects the number of rows in the

            // pets table in the database).

            TextView displayView = (TextView) findViewById(R.id.text_view_pet);

            displayView.setText("Number of rows in pets database table: " + cursor.getCount());

        } finally {

            // Always close the cursor when you're done reading from it. This releases all its

            // resources and makes it invalid.

            cursor.close();

        }

    }
*/

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_catalog.xml file.

        // This adds menu items to the app bar.

        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);

        return true;

    }

    private void insertclass(){
        Intent intent = new Intent(MainActivity.this, EnterClass.class);

        startActivity(intent);
        /*
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(classInfo.COLUMN_CLASS_NAME,"TE-4");
        values.put(classInfo.COLUMN_SUBJECT,"CN");
        values.put(classInfo.COLUMN_COUNT,20);

        long newrowId = db.insert(classInfo.TABLE_NAME,null,values);

        displayDatabaseInfo();
        */
    }
    public void dropall(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        for (ClassInfo info : Classes) {
            String table_name = info.getCtable();
            String drop = "DROP TABLE IF EXISTS " + table_name;
            db.execSQL(drop);
        }
        for (ClassInfo info : Classes) {
            String class_name = info.getCname();
            String selection = attContract.classInfo.COLUMN_CLASS_NAME + " LIKE ?";
            String[] selectionArgs = {class_name};
            db.delete(attContract.classInfo.TABLE_NAME,selection,selectionArgs);
        }
        Classes.clear();
        readData();
        adapter.notifyDataSetChanged();
        tv.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu

        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option

            case R.id.insert_new_class:

                // Do nothing for now
                insertclass();
                return true;

            case R.id.late_attend:
                startActivity(new Intent(MainActivity.this,LateProxy.class));
                return true;
            // Respond to a click on the "Delete all entries" menu option

            case R.id.delete_all:
                DialogAlert f = DialogAlert.newInstance("Do You Want To Delete All Classes",1);
                f.show(getFragmentManager(),"dialog");
                //dropall();
                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Classes.clear();
        readData();
        adapter.notifyDataSetChanged();

        if(Classes.isEmpty()){
            tv.setVisibility(View.VISIBLE);
        }else{
            tv.setVisibility(View.INVISIBLE);
        }
        //displayDatabaseInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
}