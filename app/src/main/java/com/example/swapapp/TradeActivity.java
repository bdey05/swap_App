package com.example.swapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeActivity extends AppCompatActivity {

    private ArrayList<String> itemInfo;
    private FirebaseFirestore db;
    private ListView simpleList;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private Button swap, back;
    private DatabaseHelper mDatabaseHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private HashMap<String, Object> itemsToTrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        itemInfo = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor data = mDatabaseHelper.getData();
        itemsToTrade = new HashMap<String, Object>();

        while (data.moveToNext()) {
            if (data.getString(6).equals("" + user.getDisplayName())) {
                names.add(data.getString(1));
                ids.add(data.getString(0));
            }
        }

        db = FirebaseFirestore.getInstance();
        simpleList = (ListView)findViewById(R.id.simpleListView);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list, R.id.textView, temp);
        TradeAdapter customAdapter = new TradeAdapter(getApplicationContext(), names, ids);
        simpleList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        simpleList.setAdapter(customAdapter);
        TextView tv = (TextView) findViewById(R.id.test);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItem = (String) parent.getItemAtPosition(position);
                String selectedItem = names.get(position);
                Log.d("TradeActivity", selectedItem);
                tv.setText(selectedItem);
            }
        });

        Log.d("TradeActivity", "" + simpleList.getAdapter().getCount());
        ArrayList<Integer> temp = new ArrayList<Integer>();

        back = (Button) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Listings.class);
                startActivity(i);
            }
        });
        swap = (Button) findViewById(R.id.tradeSubmit);
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*for(int i = 0; i < simpleList.getAdapter().getCount(); i++){
                    View v = simpleList.getChildAt(i);
                    CheckBox cb = (CheckBox) v.findViewById(R.id.cbBox);
                    TextView name = (TextView) v.findViewById(R.id.textViewName);
                    TextView id = (TextView) v.findViewById(R.id.textViewID);
                    if(cb.isChecked()){
                        temp.add(i);
                    }
                }*/

                for (int i = 0; i < simpleList.getChildCount(); i++) {
                    View v = simpleList.getChildAt(i);
                    CheckBox cb = (CheckBox) v.findViewById(R.id.cbBox);
                    TextView name = (TextView) v.findViewById(R.id.textViewName);
                    TextView id = (TextView) v.findViewById(R.id.textViewID);
                    if(cb.isChecked()){
                        temp.add(i);
                    }
                }
                Log.d("TradeActivity", temp.size() + "");
                ArrayList<String> ids = new ArrayList<String>();
                if(!temp.isEmpty()) {
                    int itemNum = 1;
                    for (int i = 0; i < temp.size(); i++) {
                        View v = simpleList.getChildAt(temp.get(i).intValue());
                        TextView name = (TextView) v.findViewById(R.id.textViewName);
                        TextView id = (TextView) v.findViewById(R.id.textViewID);
                        HashMap<String, Object> item = new HashMap<String, Object>();
                        item.put("name", name.getText().toString());
                        item.put("id", id.getText().toString());
                        ids.add(id.getText().toString());
                        String temp = "Item" + itemNum;
                        itemsToTrade.put(temp, item);
                        itemNum++;
                    }

                    HashMap<String, Object> tradeRequest = new HashMap<String, Object>();
                    //HashMap<String, Object> idStrings = new HashMap<String, Object>();
                    //idStrings.put("ids", ids);
                    ids.add(itemInfo.get(1));
                    tradeRequest.put("ids", ids);
                    ArrayList<String> users = new ArrayList<String>();
                    users.add(user.getDisplayName());
                    users.add(itemInfo.get(0));
                    tradeRequest.put("users", users);
                    tradeRequest.put("user1items", itemsToTrade);
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("name", itemInfo.get(4));
                    item.put("id", itemInfo.get(1));
                    tradeRequest.put("user2items", item);
                    tradeRequest.put("accepted", "false");
                    tradeRequest.put("pending", "true");
                    tradeRequest.put("sender", user.getDisplayName());
                    db.collection("trades").add(tradeRequest);
                    Toast.makeText(getApplicationContext(), "Trade request sent", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please choose an item to trade.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}