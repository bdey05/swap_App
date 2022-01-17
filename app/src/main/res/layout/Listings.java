package com.example.swapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Listings extends AppCompatActivity {

    private RecyclerView mSwapRecyclerView;
    private DatabaseHelper mDatabaseHelper;
    private SwapAdapter mAdapter;
    private Button mBack;
    private String username;
    private FirebaseUser user1;
    private FirebaseAuth mAuth;
    public static String mtitle, mdate, mswapped, muser, mdetail, mid;
    public static byte[] mimage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);
        mAuth = FirebaseAuth.getInstance();
        user1 = mAuth.getCurrentUser();
        if(user1 != null) {
            username = user1.getDisplayName();
        }
        mSwapRecyclerView = (RecyclerView) findViewById(R.id.swap_recycler_view2);
        mSwapRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabaseHelper = new DatabaseHelper(this);
        mBack = (Button) findViewById(R.id.back1);
        mBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });
        updateUI();
    }

    private void updateUI() {
        Cursor data = mDatabaseHelper.getData();
        List<String> listId = new ArrayList<>();
        List<String> listTitle = new ArrayList<>();
        List<String> listDate = new ArrayList<>();
        List<String> listSwap = new ArrayList<>();
        List<byte[]> listImage = new ArrayList<>();
        List<String> listDes = new ArrayList<>();
        List<String> listUser = new ArrayList<>();
        List<List> Data = new ArrayList<>();
        while (data.moveToNext()) {
            if (!data.getString(6).equals("" + username) && data.getString(4).equals("false")) {
                listId.add(data.getString(0));
                listTitle.add(data.getString(1));
                listDate.add(data.getString(2));
                listSwap.add(data.getString(4));
                listUser.add(data.getString(6));
                listImage.add(data.getBlob(5));
                listDes.add(data.getString(3));
            }
        }
        Data.add(listId);
        Data.add(listTitle);
        Data.add(listDate);
        Data.add(listSwap);
        Data.add(listUser);
        Data.add(listDes);
        Data.add(listImage);
        mAdapter = new Listings.SwapAdapter(Data);
        mSwapRecyclerView.setAdapter(mAdapter);
    }
    public class SwapHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public TextView mDateTextView;
        public TextView mSwappedTextView;
        public TextView mUserTextView;
        public Button mView;
        public SwapHolder(View itemView) {
            super(itemView);
            mUserTextView = (TextView) itemView.findViewById(R.id.User);
            mTitleTextView = (TextView) itemView.findViewById(R.id.Title);
            mDateTextView = (TextView)  itemView.findViewById(R.id.Date);
            mSwappedTextView = (TextView) itemView.findViewById(R.id.Swap);
            mView = (Button) itemView.findViewById(R.id.View);
        }
    }

    public class SwapAdapter extends RecyclerView.Adapter<Listings.SwapHolder> {
        private List<List> mSwap;
        public SwapAdapter(List<List> swap) {
            mSwap = swap;
        }
        @Override
        public SwapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.single_list, parent, false);
            return new SwapHolder(view);
        }
        @Override
        public void onBindViewHolder(Listings.SwapHolder holder, @SuppressLint("RecyclerView") int position) {
            String user = (String) mSwap.get(4).get(position);
            holder.mUserTextView.setText(user);
            String title = (String) mSwap.get(1).get(position);
            holder.mTitleTextView.setText(title);
            String date = (String) mSwap.get(2).get(position);
            holder.mDateTextView.setText(date);
            String swap = (String) mSwap.get(3).get(position);
            String id = (String) mSwap.get(0).get(position);
            if (swap.equals("false")) {
                holder.mSwappedTextView.setText("Swapped: False");
            } else {
                holder.mSwappedTextView.setText("Swapped: True");
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mtitle = title;
                    mdate = date;
                    mswapped = swap;
                    muser = user;
                    mid = id;
                    mdetail = (String) mSwap.get(5).get(position);
                    Log.d("SwapListFragment", "" + mSwap.get(6).get(position));
                    mimage = (byte[]) mSwap.get(6).get(position);
                    Intent i = new Intent(getApplicationContext(), ViewActivity.class);
                    startActivity(i);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mSwap.get(0).size();
        }

    }
}