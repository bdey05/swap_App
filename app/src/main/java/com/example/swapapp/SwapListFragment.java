package com.example.swapapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class SwapListFragment extends Fragment {
    private static final String TAG = "SwapListFragment";
    private RecyclerView mSwapRecyclerView;
    private SwapAdapter mAdapter;
    private Button mBack;
    private FirebaseUser user1;
    private String username;
    DatabaseHelper mDatabaseHelper;
    private FirebaseAuth mAuth;
    //private SwapLab lab;
    public static String mtitle, mdate, mswapped, muser, mdetail;
    public static byte[] mimage;
    public static boolean mcheck;
    public static String mid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_swap_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        user1 = mAuth.getCurrentUser();
        if(user1 != null) {
            username = user1.getDisplayName();
        }
        mSwapRecyclerView = (RecyclerView) v.findViewById(R.id.swap_recycler_view);
        mSwapRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabaseHelper = new DatabaseHelper(getContext());
        mBack = (Button) v.findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
            }
        });
        updateUI();
        return v;
    }

    public void updateUI() {
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
            if (data.getString(6).equals("" + username)) {
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
        mAdapter = new SwapAdapter(Data);
        mSwapRecyclerView.setAdapter(mAdapter);
    }

    public static class SwapHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public TextView mDateTextView;
        public TextView mSwappedTextView;
        public TextView mUserTextView;
        public Button mEdit;
        public Button mDelete;

        public SwapHolder(View itemView) {
            super(itemView);
            mUserTextView = (TextView) itemView.findViewById(R.id.User);
            mTitleTextView = (TextView) itemView.findViewById(R.id.Title);
            mDateTextView = (TextView) itemView.findViewById(R.id.Date);
            mSwappedTextView = (TextView) itemView.findViewById(R.id.Swap);
            mEdit = (Button) itemView.findViewById(R.id.Edit);
            mDelete = (Button) itemView.findViewById(R.id.Delete);
        }
    }

    public class SwapAdapter extends RecyclerView.Adapter<SwapHolder> {
        private List<List> mSwap;

        public SwapAdapter(List<List> swap) {
            mSwap = swap;
        }

        @Override
        public SwapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.single_view, parent, false);
            return new SwapHolder(view);
        }


        @Override
        public void onBindViewHolder(SwapHolder holder, @SuppressLint("RecyclerView") int position) {
            String user = (String) mSwap.get(4).get(position);
            holder.mUserTextView.setText(user);
            String title = (String) mSwap.get(1).get(position);
            holder.mTitleTextView.setText(title);
            String date = (String) mSwap.get(2).get(position);
            holder.mDateTextView.setText(date);
            String swap = (String) mSwap.get(3).get(position);
            if (swap.equals("false")) {
                holder.mSwappedTextView.setText("Swapped: False");
            } else {
                holder.mSwappedTextView.setText("Swapped: True");
            }
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabaseHelper.deleteData((String) mSwap.get(0).get(position));
                    updateUI();
                }
            });
            holder.mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mtitle = title;
                    mdate = date;
                    mswapped = swap;
                    muser = user;
                    mdetail = (String) mSwap.get(5).get(position);
                    Log.d("SwapListFragment", "" + mSwap.get(6).get(position));
                    mimage = (byte[]) mSwap.get(6).get(position);
                    mcheck = Boolean.parseBoolean((String) mSwap.get(3).get(position));
                    mid = (String) mSwap.get(0).get(position);
                    Intent i = new Intent(getActivity(), EditActivity.class);
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