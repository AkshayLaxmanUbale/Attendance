package com.example.android.attendance;

/**
 * Created by User on 6/28/2017.
 */

public class ClassInfo {
    private String cname;
    private String csub;
    private Integer ccount;
    private String ctable;

    public ClassInfo(String cn,String cs,Integer cc,String ct){
        cname = cn;
        csub = cs;
        ccount = cc;
        ctable = ct;
    }
    public String getCname(){
        return cname;
    }
    public String getCsub(){
        return csub;
    }
    public Integer getCcount(){
        return ccount;
    }
    public String getCtable(){
        return ctable;
    }
}
