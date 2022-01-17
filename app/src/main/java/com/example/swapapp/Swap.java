package com.example.swapapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Swap{
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mDes;
    private boolean mSwapped;
    private byte[] mImage;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Swap() {
        mId = UUID.randomUUID();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        mDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        //mDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public byte[] getImage(){
        return mImage;
    }

    public void setImage(byte[] bytes){
        mImage = bytes;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDes() {
        return mDes;
    }

    public void setDes(String des) {
        mDes = des;
    }

    public boolean isSwapped() {
        return mSwapped;
    }

    public void setSwapped(boolean swapped) {
        mSwapped = swapped;
    }


}
