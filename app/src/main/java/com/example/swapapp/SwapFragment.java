package com.example.swapapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class SwapFragment extends Fragment {

    private DatabaseHelper mDatabaseHelper;
    private Swap mItem;
    private TextView mTitleField, mDesField;
    private Button mDateButton;
    private CheckBox mSwappedCheckbox;
    private Button mCancel;
    private Button mEnter;
    private Button mCamera;
    private ImageView mPicture;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = new Swap();
        mItem.setTitle("");
        mItem.setDes("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_swap, container, false);
        mDatabaseHelper = new DatabaseHelper(this.getContext());
        mTitleField = (EditText) v.findViewById(R.id.swap_title);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDesField = (EditText) v.findViewById(R.id.swap_des);
        mDesField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mItem.setDes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.swap_date);
        mDateButton.setText(mItem.getDate().toString());
        mDateButton.setEnabled(false);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap finalPhoto = (Bitmap) bundle.get("data");
                    mPicture.setImageBitmap(finalPhoto);
                    try{
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        mItem.setImage(byteArray);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        mPicture = (ImageView) v.findViewById(R.id.image);
        mCamera = (Button) v.findViewById(R.id.pic);
        mCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    activityResultLauncher.launch(intent);
                }else{
                    Toast.makeText(getActivity(), "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancel = (Button) v.findViewById(R.id.cancelID);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
            }
        });

        mEnter = (Button) v.findViewById(R.id.enterID);
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItem.getTitle().equals("")){
                    toastMessage("Enter a title for your item");
                }
                else if (mItem.getDes().equals("")){
                    toastMessage("Enter a description for your item");
                }
                else if (mItem.getImage() == null){
                    toastMessage("Add an image of your item");
                }
                else {
                    AddData(mItem);
                    Intent i = new Intent(getActivity(), SwapListActivity.class);
                    startActivity(i);
                }
            }
        });
        return v;
    }


    public void AddData(Swap newEntry){
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if(insertData){
            toastMessage("Trade Request Inputted");
        }
        else{
            toastMessage("Error");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
