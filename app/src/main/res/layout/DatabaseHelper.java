package com.example.swapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "DatabaseHelper";
    private static final String COL0 = "ID";
    private static final String COL1 = "title";
    private static final String COL2 = "date";
    private static final String COL3 = "detail";
    private static final String COL4 = "swapped";
    private static final String COL5 = "image";
    private static final String COL6 = "user";
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    public DatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COL0 + " TEXT," + COL1 + " TEXT," + COL2 + " TEXT," + COL3 + " TEXT," + COL4 + " TEXT," + COL5 + " BLOB," + COL6 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Swap item){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        contentValues.put(COL0, "" + item.getId());
        contentValues.put(COL1, "" + item.getTitle());
        contentValues.put(COL2, "" + item.getDate());
        contentValues.put(COL3, "" + item.getDes());
        contentValues.put(COL4, "" + item.isSwapped());
        contentValues.put(COL5, item.getImage());
        if (user != null) {
            contentValues.put(COL6, "" + user.getDisplayName());
        }

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public boolean deleteData(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL0 + "=?", new String[]{id}) > 0;
    }



}