package com.example.android.attendance;

/**
 * Created by User on 7/16/2017.
 */

public class attClass {
    private int rollno;
    private int color;

    public attClass(int r,int color){
        rollno=r;
        this.color=color;
    }

    public int getRollno() {
        return rollno;
    }

    public void setRollno(int rollno) {
        this.rollno = rollno;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
