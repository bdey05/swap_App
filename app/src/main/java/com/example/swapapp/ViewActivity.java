package com.example.swapapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private TextView mTitleField;
    private Button mDateButton;
    private CheckBox mSwappedCheckbox;
    private TextView mDesField;
    private Button mBack;
    private ImageView mPicture;
    private int position;
    private byte[] Image = Listings.mimage;
    private Bitmap finalPhoto;
    private Boolean checked;
    private String title, des;
    private Button mChat, mTrade;
    private TextView username;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_view);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            position = Integer.valueOf(value);
        }
        username = (TextView) findViewById(R.id.username);
        username.setText(Listings.muser);
        mTitleField = (TextView) findViewById(R.id.swap_title3);
        mTitleField.setText(Listings.mtitle);
        mDateButton = (Button) findViewById(R.id.swap_date);
        mDateButton.setText(Listings.mdate);
        mDateButton.setEnabled(false);
        mPicture = (ImageView) findViewById(R.id.image2);
        mDesField = (TextView) findViewById(R.id.swap_des3);
        Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
        mPicture.setImageBitmap(bitmap);
        mDesField.setText(Listings.mdetail);
        mBack = (Button) findViewById(R.id.backId);
        mTrade = (Button) findViewById(R.id.trade);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Listings.class);
                startActivity(i);
            }
        });
        /*mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("receiver", Listings.muser);
                startActivity(i);
            }
        });*/
        mTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TradeActivity.class);
                //i.putExtra("receiver", Listings.muser);
                ArrayList<String> itemInfo = new ArrayList<String>();
                itemInfo.add(Listings.muser);
                itemInfo.add(Listings.mid);
                itemInfo.add(Listings.mdate);
                itemInfo.add(Listings.mswapped);
                itemInfo.add(Listings.mtitle);
                itemInfo.add(Listings.mdetail);
                //i.putExtra("itemInfo", itemInfo);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)itemInfo);
                i.putExtra("BUNDLE",args);
                startActivity(i);
            }
        });

    }

}