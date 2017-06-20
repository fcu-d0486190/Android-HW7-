package com.bobo.hw7;

import android.graphics.Bitmap;

/**
 * Created by user on 2017/6/13.
 */

public class hotel {
    private String hotelname;
    private String hoteladdress;
    private Bitmap impicture;
    public String getHoteladdress() {
        return hoteladdress;
    }

    public void setHoteladdress(String hoteladdress) {
        this.hoteladdress = hoteladdress;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
    }

    public Bitmap getImpicture() {
        return impicture;
    }

    public void setImpicture(Bitmap impicture) {
        this.impicture = impicture;
    }
}
