package com.example.swapapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewTradesAdapter extends BaseAdapter {
    Context context;
    ArrayList <HashMap<String, Object>> trades;
    LayoutInflater inflter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    ArrayList<String> docIDS;
    private FirebaseFirestore db;
    private DatabaseHelper mDatabaseHelper;
    private String ids = "";

    public ViewTradesAdapter(Context applicationContext, ArrayList<HashMap<String, Object>> trades, ArrayList<String>docIDS) {
        this.context = applicationContext;
        this.trades = trades;
        this.docIDS = docIDS;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return trades.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.viewtrades, null);
        TextView name = (TextView) view.findViewById(R.id.tVName);
        TextView itemsReceiving = (TextView) view.findViewById(R.id.tVItemsReceiving);
        TextView itemsSending = (TextView) view.findViewById(R.id.tVItemsSending);
        TextView itemIDS = (TextView) view.findViewById(R.id.tVItemIDS);
        TextView status = (TextView) view.findViewById(R.id.tVStatus);
        TextView pending = (TextView) view.findViewById(R.id.tVPending);
        ArrayList<String> tradeParticipants = new ArrayList<String>();
        tradeParticipants = (ArrayList<String>) trades.get(i).get("users");
        db = FirebaseFirestore.getInstance();
        mDatabaseHelper = new DatabaseHelper(context);
        HashMap<String, Object> iReceive = new HashMap<String, Object>();
        HashMap<String, Object> iSend = new HashMap<String, Object>();
        String nm = "";
        if(tradeParticipants.contains(user.getDisplayName())) {
            //name.setText("Trade with: " + (String) trades.get(i).get("sender"));
            Button accept = (Button) view.findViewById(R.id.accept);
            Button decline = (Button) view.findViewById(R.id.decline);
            String sender = (String) trades.get(i).get("sender");
            if (sender.equals(user.getDisplayName()) || trades.get(i).get("pending").equals("false")) {
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
            }
            for(String d : tradeParticipants){
                if(!d.equals(user.getDisplayName())){
                    nm = d;
                }
            }
            name.setText("Trade with: " + nm);

            status.setText("Trade Accepted: " + (String) trades.get(i).get("accepted"));
            iReceive = (HashMap<String, Object>) trades.get(i).get("user1items");
            //String ids = "";
            String receiving = "";
            for (Map.Entry<String, Object> entry : iReceive.entrySet()) {
                String key = entry.getKey();
                HashMap<String, Object> value = (HashMap<String, Object>) entry.getValue();
                ids += value.get("id") + ",";
                receiving += value.get("name") + ",";
            }
            String sending = "";
            iSend = (HashMap<String, Object>) trades.get(i).get("user2items");
            for (Map.Entry<String, Object> entry : iSend.entrySet()) {
                String key = entry.getKey();
                //HashMap<String, Object> value = (HashMap<String, Object>) entry.getValue();
                String value = (String)entry.getValue();
                Log.d("ViewTradesAdapter", entry.getKey());
                Log.d("ViewTradesAdapter", value);
                //sending += value.get("name") + ",";*/
                //ids += key + ",";
                sending += value + ",";
            }
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(sending.split(",")));
            String id1 = strings.get(1);
            sending = strings.get(0);
            ids += id1;
            if (sending.contains("'")) {
                sending = sending.substring(0, sending.lastIndexOf(","));
            }
            if(receiving.contains(",")) {
                receiving = receiving.substring(0, receiving.lastIndexOf(","));
            }
            itemIDS.setText(ids);
            if(sender.equals(user.getDisplayName())) {
                itemsSending.setText("Sending: " + receiving);
                itemsReceiving.setText("Receiving: " + sending);
            }
            else{
                itemsSending.setText("Sending: " + sending);
                itemsReceiving.setText("Receiving: " + receiving);
            }
            if(trades.get(i).get("pending").equals("true")){
                pending.setText("Trade is pending");
            }
            else{
                pending.setText("Trade request has been decided");
            }

            accept.setTag(i);
            decline.setTag(i);
            /*accept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = (Integer)view.getTag();

                }
            });*/

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = (Integer) view.getTag();
                    //View v = tradeView.getChildAt(position);
                    String docID = docIDS.get(position);
                    HashMap<String, Object> trade = trades.get(position);
                    trade.put("accepted", "true");
                    trade.put("pending", "false");
                    //Log.d(TAG, docID);
                    //db.collection("trades").document(docID).set(trade);
                    db.collection("trades").document(docID).update("accepted", "true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ViewTradesAdapter", "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ViewTradesAdapter", "Error updating document", e);
                                }
                            });
                    db.collection("trades").document(docID).update("pending", "false").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ViewTradesAdapter", "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ViewTradesAdapter", "Error updating document", e);
                                }
                            });
                    ArrayList<String> idStrings = new ArrayList<>(Arrays.asList(ids.split(",")));
                    Cursor data = mDatabaseHelper.getData();
                    for(String i : idStrings){
                        mDatabaseHelper.deleteData(i);
                    }
                    Intent i = new Intent(context, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            });


            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    //View v = tradeView.getChildAt(position);
                    String docID = docIDS.get(position);
                    HashMap<String, Object> trade = trades.get(position);
                    trade.put("accepted", "true");
                    trade.put("pending", "false");
                    //Log.d(TAG, docID);
                    //db.collection("trades").document(docID).set(trade);
                    db.collection("trades").document(docID).update("accepted", "false").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ViewTradesAdapter", "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ViewTradesAdapter", "Error updating document", e);
                                }
                            });
                    db.collection("trades").document(docID).update("pending", "false").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ViewTradesAdapter", "DocumentSnapshot successfully updated!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ViewTradesAdapter", "Error updating document", e);
                                }
                            });
                    Intent i = new Intent(context, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            });



            itemIDS.setVisibility(View.GONE);

        }

        return view;
    }
}

