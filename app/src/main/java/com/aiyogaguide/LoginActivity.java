package com.aiyogaguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static String user = "";
    private EditText username, password;
    private static final String SHARED_PREFS_NAME = "user_session";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USERNAME_KEY = "username";

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected boolean isValid() {
        if (!username.getText().toString().equals(""))
            if (!password.getText().toString().equals(""))
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
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // Redirect to Tips activity
            startActivity(new Intent(LoginActivity.this, Tips.class));
            finish();
            return;  // No need to execute the rest of the code
        }


        // Initialize views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        findViewById(R.id.register).setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        findViewById(R.id.btn).setOnClickListener(view -> {
            if (isValid()) {
                loginUser(mDatabase);
            }
        });
    }

    private void loginUser(DatabaseReference mDatabase) {
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

                        // Save login state in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(IS_LOGGED_IN, true);
                        editor.putString(USERNAME_KEY, username.getText().toString());
                        editor.apply();

                        // Redirect to Tips activity
                        startActivity(new Intent(LoginActivity.this, Tips.class));
                        finish();

                        user = username.getText().toString();
                    } else {
                        password.setText("");
                        showToast("Invalid Credentials!");
                    }
                } else {
                    showToast("User not found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Error!");
            }
        });
    }
}
