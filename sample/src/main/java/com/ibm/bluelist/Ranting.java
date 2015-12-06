package com.ibm.bluelist;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * Created by RON on 11/9/2015.
 */
@IBMDataObjectSpecialization("Ranting")
public class Ranting extends IBMDataObject {
    public static final String CLASS_NAME = "Ranting";
    private static final String NAME = "TaiKhoan";
    private static final String USER = "NguoiDanhGia";
    private static final String TITLE = "ChuDe";
    private static final String CONTENT = "Noidung";
    private static final String RANTINGNUMBER = "SoDiem";


    public String getNAME(){
        return (String) getObject(NAME);
    }

    public  String getUSER() {
        return (String) getObject(USER);
    }

    public  String getTITLE() {
        return (String) getObject(TITLE);
    }

    public  String getCONTENT() {
        return (String) getObject(CONTENT);
    }

    public  String getRANTINGNUMBER() {
        return (String) getObject(RANTINGNUMBER);
    }




    public void setNAME(String itemName) {

        setObject(NAME, (itemName != null) ? itemName : "");
    }
    public void setUSER(String itemName) {

        setObject(USER, (itemName != null) ? itemName : "");
    }
    public void setTITLE(String itemName) {

        setObject(TITLE, (itemName != null) ? itemName : "");
    }
    public void setCONTENT(String itemName) {

        setObject(CONTENT, (itemName != null) ? itemName : "");
    }
    public void setRANTINGNUMBER(String itemName) {

        setObject(RANTINGNUMBER, (itemName != null) ? itemName : "");
    }

}
