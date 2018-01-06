package com.example.android.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 6/28/2017.
 */

public class classAdapter extends ArrayAdapter<ClassInfo> {

    public classAdapter(Context context, ArrayList<ClassInfo> list){
        super(context,0,list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listClass = convertView;

        if(listClass == null){
            listClass = LayoutInflater.from(getContext()).inflate(R.layout.list_class,parent,false);
        }

        ClassInfo cInfo = getItem(position);

        TextView cn = (TextView) listClass.findViewById(R.id.classname);
        TextView cs = (TextView) listClass.findViewById(R.id.subjectname);
        TextView cc = (TextView) listClass.findViewById(R.id.classcount);
        try{
            cn.setText(cInfo.getCname());
            cs.setText(cInfo.getCsub());
            cc.setText(String.valueOf(cInfo.getCcount()));
        }catch (NullPointerException ae) {

        }


        return listClass;
    }
}
