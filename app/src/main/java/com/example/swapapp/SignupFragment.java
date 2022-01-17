package com.example.swapapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;


public class SignupFragment extends Fragment {
    public static FirebaseUser user;
    private FirebaseAuth mAuth;
    private Button signup;
    private EditText username;
    private EditText email;
    private EditText password;
    private TextView popup;
    private View rootView;
    private EditText repassword;
    private static final String TAG = "SignupFragment";
    private DatabaseHelper mDatabaseHelper;
    private FirebaseFirestore db;
    private User newUser;
    private FirebaseAuth mAuthListener;
    private boolean usernameExistCheck;
    Context context;


    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        context = this.getActivity();
        makeUser();
        // Inflate the layout for this fragment

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        makeUser();
    }

    public void makeUser () {
        signup = (Button) rootView.findViewById(R.id.btn_signup);
        username = (EditText) rootView.findViewById(R.id.name);
        email = (EditText) rootView.findViewById(R.id.email);
        password = (EditText) rootView.findViewById(R.id.password);
        repassword = (EditText) rootView.findViewById(R.id.repassword);
        popup = (TextView) rootView.findViewById(R.id.popup);
        //usernameExists = false;

        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!isEmpty(username) && !isEmpty(email) && !isEmpty(password) && !isEmpty(repassword)){
                    if(!(password.getText().toString()).equals(repassword.getText().toString())){
                        Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    }else{
                        popup.setText("Please go to your email to verify your account!");
                        db.collection("accounts").whereEqualTo("username", username.getText().toString())
                                .limit(1).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            usernameExistCheck = task.getResult().isEmpty();
                                            if (usernameExistCheck) {
                                                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Sign in success, update UI with the signed-in user's information
                                                                    Log.d(TAG, "createUserWithEmail:success");
                                                                    user = mAuth.getCurrentUser();
                                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                            .setDisplayName(username.getText().toString())
                                                                            .build();

                                                                    user.updateProfile(profileUpdates)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        //Log.d(TAG, "User profile updated.");

                                                                    /*if (email.getText().toString().equals("deyb@bxscience.edu")) {
                                                                        newUser = new User(username.getText().toString(), email.getText().toString(), password.getText().toString(), true);
                                                                    } else {*/
                                                                                        newUser = new User(username.getText().toString(), email.getText().toString(), password.getText().toString());
                                                                                        //}
                                                                                        user.sendEmailVerification()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            Log.d(TAG, "Email sent.");
                                                                                                            db.collection("accounts").add(newUser);
                                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                                            builder.setTitle("AlertDialog with No Buttons");

                                                                                                            builder.setMessage("Hide this message by just tapping anywhere outside the dialog box!");
                                                                                                            AlertDialog diag = builder.create();

                                                                                                            //Display the message!
                                                                                                            diag.show();
                                                                                                            Toast.makeText(getContext(), "Check email for verification", Toast.LENGTH_SHORT).show();


                                                                                                            Intent i = new Intent(getContext(), LoginSignupActivity.class);
                                                                                                            startActivity(i);

                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                    }
                                                                                }
                                                                            });

                                                                    //Toast.makeText(getContext(), "User was successfully created." + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                                    Toast.makeText(getContext(), "Error detected. " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                Toast.makeText(getContext(), "Error. Username taken. Please make a different username.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });

                       /* if (usernameExists) {
                            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "createUserWithEmail:success");
                                                user = mAuth.getCurrentUser();
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(username.getText().toString())
                                                        .build();

                                                user.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //Log.d(TAG, "User profile updated.");


                                                                        newUser = new User(username.getText().toString(), email.getText().toString(), password.getText().toString());
                                                                    //}
                                                                    user.sendEmailVerification()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Log.d(TAG, "Email sent.");
                                                                                        db.collection("accounts").add(newUser);
                                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                                        builder.setTitle("AlertDialog with No Buttons");

                                                                                        builder.setMessage("Hide this message by just tapping anywhere outside the dialog box!");
                                                                                        AlertDialog diag = builder.create();

                                                                                        //Display the message!
                                                                                        diag.show();

                                                                                        do {
                                                                                            user.reload();
                                                                                        }
                                                                                        while (!user.isEmailVerified());
                                                                                        Intent i = new Intent(getContext(), HomeActivity.class);
                                                                                        startActivity(i);

                                                                                    }
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });

                                                //Toast.makeText(getContext(), "User was successfully created." + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(getContext(), "Error detected. " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getContext(), "Error. Username taken. Please make a different username.", Toast.LENGTH_LONG).show();
                        }*/

                    }
                }
                else{
                    Toast.makeText(getActivity(), "A field was left empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

}