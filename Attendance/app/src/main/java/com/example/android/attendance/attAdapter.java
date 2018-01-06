package com.example.android.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 7/16/2017.
 */

public class attAdapter extends ArrayAdapter<attClass> {

    public attAdapter(Context context,ArrayList<attClass> list){
        super(context,0,list);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View fv = convertView;
        if(fv==null){
            fv= LayoutInflater.from(getContext()).inflate(R.layout.att_list,parent,false);
        }

        attClass att = getItem(position);

        TextView r = (TextView)fv.findViewById(R.id.roll_no);

        r.setText(String.valueOf(att.getRollno()));
        r.setBackgroundColor(att.getColor());

        return fv;
    }
}
