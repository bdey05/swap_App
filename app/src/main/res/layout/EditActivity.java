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

public class EditActivity extends AppCompatActivity {

    private DatabaseHelper mDatabaseHelper;
    private TextView mTitleField;
    private Button mDateButton;
    private CheckBox mSwappedCheckbox;
    private TextView mDesField;
    private Button mCancel;
    private Button mSave;
    private Button mCamera;
    private ImageView mPicture;
    private int position;
    private byte[] Image, newImage;
    private Bitmap finalPhoto;
    private Boolean checked;
    private String title, des, mid;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean NewChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_swap);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            position = Integer.valueOf(value);
        }
        addListenOnButton();
    }

    private void addListenOnButton() {
        mid = SwapListFragment.mid;
        mDatabaseHelper = new DatabaseHelper(this);
        mTitleField = (TextView) findViewById(R.id.swap_title2);
        mTitleField.setText(SwapListFragment.mtitle);
        mCancel = (Button) findViewById(R.id.cancelID);
        mPicture = (ImageView) findViewById(R.id.image2);
        Image = SwapListFragment.mimage;
        mDesField = (TextView) findViewById(R.id.swap_des2);
        mDesField.setText(SwapListFragment.mdetail);
        Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
        mPicture.setImageBitmap(bitmap);
        des = SwapListFragment.mdetail;
        title = SwapListFragment.mtitle;
        checked = false;
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SwapListActivity.class);
                startActivity(i);
            }
        });
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDesField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                des = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) findViewById(R.id.swap_date);
        mDateButton.setText(SwapListFragment.mdate);
        mDateButton.setEnabled(false);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    finalPhoto = (Bitmap) bundle.get("data");
                    mPicture.setImageBitmap(finalPhoto);
                }
                try{
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    finalPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    newImage = byteArray;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        mCamera = (Button) findViewById(R.id.pic2);
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mSave = (Button) findViewById(R.id.backId);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.equals("")){
                    toastMessage("Enter a title for your item");
                }
                else if (des.equals("")){
                    toastMessage("Enter a description for your item");
                }
                else {
                    mDatabaseHelper.deleteData(mid);
                    Swap swap = new Swap();
                    swap.setTitle(title);
                    swap.setDes(des);
                    if (newImage != null) {
                        swap.setImage(newImage);
                    } else {
                        swap.setImage(Image);
                    }
                    mDatabaseHelper.addData(swap);
                    Intent i = new Intent(getApplicationContext(), SwapListActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}

