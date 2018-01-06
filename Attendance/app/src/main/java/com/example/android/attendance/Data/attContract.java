package com.example.android.attendance.Data;

import android.provider.BaseColumns;

/**
 * Created by User on 6/27/2017.
 */

public class attContract {
    private attContract(){};

    public static abstract class classInfo implements BaseColumns{

        public final static String TABLE_NAME = "class_info";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CLASS_NAME = "class_name";
        public final static String COLUMN_SUBJECT = "class_subject";
        public final static String COLUMN_COUNT = "student_count";
        public final static String COLUMN_ATT_TABLE = "attendance_table";

    }
}
