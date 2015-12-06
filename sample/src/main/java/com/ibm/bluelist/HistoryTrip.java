package com.ibm.bluelist;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Created by RON on 11/8/2015.
 */
@IBMDataObjectSpecialization("HistoryTrip")
public class HistoryTrip extends IBMDataObject {
    public static final String CLASS_NAME = "HistoryTrip";
    private static final String NAME = "TaiKhoan";
    private static final String START   = "DiemDon";
    private static final String DESTINATION   = "DiemDen";
    private static final String MONEY   = "Tien";
    private static final String KM   = "soKm";


    public String getNAME(){
        return (String) getObject(NAME);
    }
    public  String getSTART() {
        return (String) getObject(START);
    }

    public  String getDESTINATION() {
        return (String) getObject(DESTINATION);
    }

    public  String getMONEY() {
        return (String) getObject(MONEY);
    }
    public  String getKm() {
        return (String) getObject(KM);
    }






    public void setNAME(String itemName) {

        setObject(NAME, (itemName != null) ? itemName : "");
    }

    public void setSTART(String itemName) {

        setObject(START, (itemName != null) ? itemName : "");
    }

    public  void setDESTINATION(String itemName) {
        setObject(DESTINATION, (itemName != null) ? itemName : "");

    }

    public void setMONEY(String itemName) {
        setObject(MONEY, (itemName != null) ? itemName : "");

    }
    public void setKm(String itemName) {
        setObject(KM, (itemName != null) ? itemName : "");

    }

    public String toString() {
        String theItemName = "";
        theItemName = getNAME();
        return theItemName;
    }
}

