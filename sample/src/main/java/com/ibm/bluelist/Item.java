/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.bluelist;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("Item")
public class Item extends IBMDataObject {
    public static final String CLASS_NAME = "Item";
    private static final String NAME = "TaiKhoan";
    private static final String PASS = "MatKhau";
    private static final String IMAGE = "HinhAnhCMND";
    private static final String IMAGE2 = "HinhAnhBangLai";
    private static final String IMAGE3 = "AnhDaiDien";
    private static final String PHANQUYEN ="PhanQuyen";
    private static final String MESSAGE = "ThongBao";
    private static final String ADDRESS = "DiaChi";
    private static final String LAT   = "ViDo";
    private static final String LOGT   = "KinhDo";
    private static final String MAIL   = "Email";
    private static final String CMND   = "SoCMND";
    private static final String MONEY   = "Tien";
    private static final String MONEY2   = "Tam";
    private static final String START   = "DiemDon";
    private static final String DESTINATION   = "DiemDen";
    private static final String PHONE   = "Phone";
    private static final String NAMECUSTMER   = "TenHanhKhach";
    private static final String PRICE   = "Gia";
    private static final String DONE   = "done";
    private static final String BLOCK_USER   = "Block";
    private static final String KM   = "soKm";
    private static final String NAMEUSER   = "Ten";




    /**
     * Gets the name of the Item.
     *
     * @return String itemName
     */
    public String getNameuser() {

        return (String) getObject(NAMEUSER);
    }
    public String getName() {

        return (String) getObject(NAME);
    }
    public String getPass() {

        return (String) getObject(PASS);
    }
    public String getImage() {

        return (String) getObject(IMAGE);
    }
    public String getQuyen() {

        return (String) getObject(PHANQUYEN);
    }
    public String getMessage() {

        return (String) getObject(MESSAGE);
    }
    public String getAddress() {

        return (String) getObject(ADDRESS);
    }
    public String getVido() {

        return (String) getObject(LAT);
    }
    public String getKinhDo() {

        return (String) getObject(LOGT);
    }
    public String getMail() {

        return (String) getObject(MAIL);
    }
    public String getCmnd() {

        return (String) getObject(CMND);
    }
    public String getImage2() {

        return (String) getObject(IMAGE2);
    }
    public String getImage3() {

        return (String) getObject(IMAGE3);
    }
    public String getMoney() {

        return (String) getObject(MONEY);
    }
    public String getMoney2() {

        return (String) getObject(MONEY2);
    }
    public String getStart() {

        return (String) getObject(START);
    }
    public String getDestination() {

        return (String) getObject(DESTINATION);
    }
    public String getPhone() {

        return (String) getObject(PHONE);
    }
    public String getNamecustmer() {

        return (String) getObject(NAMECUSTMER);
    }

    public String getPRICE() {
        return (String) getObject(PRICE);
    }
    public  String getKm() {
        return (String) getObject(KM);
    }




    public  String getDONE() {
        return (String) getObject(DONE);
    }

    public String getBlockUser() {
        return (String) getObject(BLOCK_USER);
    }

    /**
     * Sets the name of a list item, as well as calls setCreationTime().
     *
     * @param //String itemName
     */
    public void setName(String itemName) {

        setObject(NAME, (itemName != null) ? itemName : "");
    }
    public void setPass(String itemPass) {

        setObject(PASS, (itemPass != null) ? itemPass : "");
    }
    public void setImage(String itemImage) {

        setObject(IMAGE, (itemImage != null) ? itemImage : "");
    }
    public void setQuyen(String itemPhanQuyen) {

        setObject(PHANQUYEN, (itemPhanQuyen != null) ? itemPhanQuyen : "");
    }
    public void setMessage(String itemMessage) {

        setObject(MESSAGE, (itemMessage != null) ? itemMessage : "");
    }
    public void setAddress(String itemAddress) {

        setObject(ADDRESS, (itemAddress != null) ? itemAddress : "");
    }
    public void setVido(String itemLa) {

        setObject(LAT, (itemLa != null) ? itemLa : "");
    }
    public void setKinhDo(String itemLo) {

        setObject(LOGT, (itemLo != null) ? itemLo : "");
    }
    public void setMail(String itemMail) {

        setObject(MAIL, (itemMail != null) ? itemMail : "");
    }
    public void setImage2(String itemImage2) {

        setObject(IMAGE2, (itemImage2 != null) ? itemImage2 : "");
    }
    public void setImage3(String itemImage3) {

        setObject(IMAGE3, (itemImage3 != null) ? itemImage3 : "");
    }
    public void setCmnd(String itemCmnd) {

        setObject(CMND, (itemCmnd != null) ? itemCmnd : "");
    }
    public void setMoney(String itemMoney) {

        setObject(MONEY, (itemMoney != null) ? itemMoney : "");
    }
    public void setMoney2(String itemMoney2) {

        setObject(MONEY2, (itemMoney2 != null) ? itemMoney2 : "");
    }
    public void setStart(String itemStart) {

        setObject(START, (itemStart != null) ? itemStart : "");
    }
    public void setDestination(String itemDes) {

        setObject(DESTINATION, (itemDes != null) ? itemDes : "");
    }
    public void setPhone(String itemDes) {

        setObject(PHONE, (itemDes != null) ? itemDes : "");
    }
    public void setNamecustmer(String itemDes) {

        setObject(NAMECUSTMER, (itemDes != null) ? itemDes : "");
    }

    public void setPRICE(String itemDes) {
        setObject(PRICE, (itemDes != null) ? itemDes : "");
    }
    public void setDone(String itemDes) {
        setObject(DONE, (itemDes != null) ? itemDes : "");
    }

    public void setBlockUser(String itemDes) {
        setObject(BLOCK_USER, (itemDes != null) ? itemDes : "");
    }
    public void setKm(String itemName) {
        setObject(KM, (itemName != null) ? itemName : "");

    }
    public void setNameuser(String itemName) {
        setObject(NAMEUSER, (itemName != null) ? itemName : "");

    }
    /**
     * When calling toString() for an item, we'd really only want the name.
     *
     * @return String theItemName
     */
    public String toString() {
        String theItemName = "";
        theItemName = getName();
        return theItemName;
    }
}
