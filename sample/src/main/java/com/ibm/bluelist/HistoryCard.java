package com.ibm.bluelist;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Created by RON on 11/9/2015.
 */
@IBMDataObjectSpecialization("HistoryCard")
public class HistoryCard extends IBMDataObject {
    public static final String CLASS_NAME = "HistoryCard";
    private static final String NAME = "TaiKhoan";
    private static final String DATETIME = "Ngay";
    private static final String TYPE = "LoaiThe";
    private static final String SERI = "SoSeri";
    private static final String MENHGIA = "VND";
    private static final String MAGD = "ID";



    public String getName(){
        return (String) getObject(NAME);
    }
    public String getDatetime(){
        return (String) getObject(DATETIME);
    }
    public String getType(){
        return (String) getObject(TYPE);
    }
    public String getSeri(){
        return (String) getObject(SERI);
    }
    public String getMenhgia(){
        return (String) getObject(MENHGIA);
    }
    public String getMagd(){
        return (String) getObject(MAGD);
    }




    public void setName(String itemName) {

        setObject(NAME, (itemName != null) ? itemName : "");
    }
    public void setDatetime(String itemName) {

        setObject(DATETIME, (itemName != null) ? itemName : "");
    }
    public void setType(String itemName) {

        setObject(TYPE, (itemName != null) ? itemName : "");
    }
    public void setSeri(String itemName) {

        setObject(SERI, (itemName != null) ? itemName : "");
    }
    public void setMenhgia(String itemName) {

        setObject(MENHGIA, (itemName != null) ? itemName : "");
    }
    public void setMagd(String itemName) {

        setObject(MAGD, (itemName != null) ? itemName : "");
    }


}
