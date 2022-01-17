package com.example.swapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ArrayList<Chat> contacts;
    private Button send;
    private EditText message;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ChatAdapter adapter;
    private RecyclerView rvContacts;
    private String receiver;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        send = (Button)findViewById(R.id.send);
        message = (EditText)findViewById(R.id.messageInput);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        receiver = getIntent().getStringExtra("receiver");
        currentUsername = user.getDisplayName();
        rvContacts = (RecyclerView) findViewById(R.id.messages);
        contacts = new ArrayList<Chat>();
        displayNewMessage();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageToSend = message.getText().toString();

                if(!messageToSend.isEmpty()){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                    String currentDateandTime = sdf.format(new Date());
                    Chat chat = new Chat(messageToSend, currentUsername, currentDateandTime);
                    db.collection("chats").document().collection("messageHistory")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("ChatActivity", document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.d("ChatActivity", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                    db.collection("chats").document().collection("messageHistory").add(chat);
                    message.setText("");
                    contacts.add(contacts.size(), chat);
                    adapter.notifyItemInserted(contacts.size()-1);
                }
            }
        });

        /*contacts = Chat.createContactsList(20);
        adapter = new ChatAdapter(contacts);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));*/

    }
    public void displayNewMessage(){
        db.collection("chats").document("conversationBetween" + currentUsername + "and" + receiver).collection("messageHistory").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<Chat> types = documentSnapshots.toObjects(Chat.class);
                        // Add all to your list

                        contacts.addAll(types);
                        //contacts = Chat.createContactsList(20);
                        adapter = new ChatAdapter(contacts);
                        rvContacts.setAdapter(adapter);
                        rvContacts.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        //Log.d("ChatActivity", contacts.get(0).getMessage());
                    }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }


}