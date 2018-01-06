package com.example.android.attendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by User on 7/9/2017.
 */

public class DialogAlert extends DialogFragment {
    int code=0;

    static DialogAlert newInstance(String title,int code){
        DialogAlert fragment = new DialogAlert();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putInt("code",code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        code = getArguments().getInt("code");
        return new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(code == 0) {
                            ((MainActivity) getActivity()).deleteclass();
                        }
                        else if(code == 1){
                            ((MainActivity)getActivity()).dropall();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText((getActivity()),"Nothing Changed!!",Toast.LENGTH_SHORT).show();
                    }
                }).create();
    }
}
