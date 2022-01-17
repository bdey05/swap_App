package com.example.swapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import android.os.MessageQueue;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewTradesActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private final String TAG = "ViewTradesActivity";
    private HashMap<String, Object> trade;
    private ArrayList<HashMap<String, Object>> trades;
    private ArrayList <String> docIDS;
    private Button accept, decline;
    private Button back;

    //private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trades);
        LinearLayout ll = (LinearLayout) findViewById(R.id.tradecontainer);
        ll.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        trade = new HashMap<String, Object>();
        trades = new ArrayList<HashMap<String, Object>>();
        docIDS = new ArrayList<String>();
        db.collection("trades")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                trade = (HashMap<String, Object>) document.getData();
                                ArrayList<String> usersInTrade = (ArrayList<String>) trade.get("users");
                                if(usersInTrade.contains(user.getDisplayName())) {
                                    trades.add(trade);
                                    docIDS.add(document.getId());
                                }
                                //Log.d(TAG, "" + trades.get(0));
                                //Log.d(TAG, "" + trade.get("sender"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*Log.d(TAG, "" + trades.size());
        if(trades != null && trades.size() != 0){
            Log.d(TAG, trades.get(0) + "");
        }*/
        ListView tradeView = (ListView) findViewById(R.id.tradeView);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewTradesAdapter vta = new ViewTradesAdapter(getApplicationContext(), trades, docIDS);
                tradeView.setAdapter(vta);
                ll.setVisibility(View.VISIBLE);

            }
        }, 1000);
        back = (Button) findViewById(R.id.backBtn2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });

        /*accept = (Button) findViewById(R.id.accept);
        decline = (Button) findViewById(R.id.decline);*/
        //Log.d(TAG, "nice");
        /*if(accept != null) {
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    View v = tradeView.getChildAt(position);
                    String docID = docIDS.get(position);
                    HashMap<String, Object> trade = trades.get(position);
                    trade.put("accepted", "true");
                    trade.put("pending", "false");
                    Log.d(TAG, docID);
                    //db.collection("trades").document(docID).set(trade);
                    db.collection("trades").document(docID).update("accepted", "true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            });
        }*/

    }


}