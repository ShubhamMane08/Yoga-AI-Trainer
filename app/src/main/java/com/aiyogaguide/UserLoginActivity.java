package com.aiyogaguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserLoginActivity extends AppCompatActivity {

    public static String user = "";
    EditText username, password;

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected boolean isValid() {
        if(!username.getText().toString().equals(""))
            if(!password.getText().toString().equals(""))
                return true;
            else
                showToast("Empty Password!");
        else
            showToast("Invalid Credentials!");

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //INIT
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // Start
        // startActivity(new Intent(this, MainActivity.class));

        findViewById(R.id.register).setOnClickListener(view -> startActivity(new Intent(this, UserRegisterActivity.class)));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        findViewById(R.id.btn).setOnClickListener(view -> {

            if (isValid()) {

                String SHAUser = SHA256Generator.generateSHA256(username.getText().toString());
                String SHAPass = SHA256Generator.generateSHA256(password.getText().toString());

                assert SHAUser != null;
                mDatabase.child("Users").child(SHAUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String uname = Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString();
                            String upass = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();

                            if (uname.equals(username.getText().toString()) && upass.equals(SHAPass)) {
                                showToast("LOGGED IN!");
                                startActivity(new Intent(UserLoginActivity.this, HomePageActivity.class));
                                finish();

                                user = username.getText().toString();
                            } else {
                                password.setText("");
                                showToast("Invalid Credentials!");
                            }
                        } else
                            showToast("User not found!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast("Error!");
                    }
                });
            }
        });
    }
}