package com.aiyogaguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText name, age, username, password, password2, height, weight;
    RadioGroup genderRadioGroup;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // INIT
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
//        height = findViewById(R.id.height);
//        weight = findViewById(R.id.weight);
        CheckBox showPasswordCheckbox = findViewById(R.id.show_password_checkbox);
        genderRadioGroup = findViewById(R.id.gender_radio_group);

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        findViewById(R.id.registerButton).setOnClickListener(v -> {

            if(isValid()) {

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                String SHAUser = SHA256Generator.generateSHA256(username.getText().toString());
                assert SHAUser != null;
                mDatabase.child("Users").child(SHAUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            showToast("User is already exists!");

                        else {

                            String SHAUser = SHA256Generator.generateSHA256(username.getText().toString());
                            String SHAPass = SHA256Generator.generateSHA256(password.getText().toString());

                            assert SHAUser != null;
                            mDatabase.child("Users").child(SHAUser).child("username").setValue(username.getText().toString());
                            mDatabase.child("Users").child(SHAUser).child("password").setValue(SHAPass);

                            // Personal Details
                            mDatabase.child("Users").child(SHAUser).child("name").setValue(name.getText().toString());
                            mDatabase.child("Users").child(SHAUser).child("age").setValue(Integer.parseInt(age.getText().toString()));
                           // mDatabase.child("Users").child(SHAUser).child("height").setValue(Double.parseDouble((height.getText().toString())));
                         //   mDatabase.child("Users").child(SHAUser).child("weight").setValue(Double.parseDouble(weight.getText().toString()));

                            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                            RadioButton selectedRadioButton = findViewById(selectedId);

                            mDatabase.child("Users").child(SHAUser).child("gender").setValue(selectedRadioButton.getText().toString());

                            //showToast(SHA256Generator.generateSHA256(username.getText().toString()));

                            showToast("User registered successfully!");
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            showToast("Kindly LOGIN");
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast("Error!");
                    }
                });
            }
        });
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected boolean isValid() {
        if (!name.getText().toString().equals("")) {
            if (age.getText().toString().length() <= 2 && !age.getText().toString().equals("") && Integer.parseInt(age.getText().toString()) > 0) {
                if (!username.getText().toString().equals("")) {
                    if (username.getText().toString().contains("@") && username.getText().toString().contains(".com")) {
                        if (!password.getText().toString().equals("")) {
                            if (password.getText().toString().length() > 4) {
                                if (password.getText().toString().equals(password2.getText().toString())) {
                                   // if (!height.getText().toString().equals("") && Float.parseFloat(height.getText().toString()) > 0) {
                                      //  if (!weight.getText().toString().equals("") && Float.parseFloat(weight.getText().toString()) > 0) {
                                            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                                            if (selectedGenderId != -1) {
                                                return true;
                                            } else {
                                                showToast("Please select your gender!");
                                            }
                                        }
//                                else {
//                                            showToast("Please Enter the Weight!");
//                                        }
//                                    }
//                            else {
//                                        showToast("Please Enter the Height!");
//                                    }
                               // }
                            else {
                                    showToast("Password doesn't match!");
                                }
                            } else {
                                showToast("Weak Password!");
                            }
                        } else {
                            showToast("Empty Password!");
                        }
                    } else {
                        showToast("Invalid Email!");
                    }
                } else {
                    showToast("Please Enter the Email!");
                }
            } else {
                showToast("Please Enter the Age!");
            }
        } else {
            showToast("Please Enter the Name!");
        }

        return false;
    }

}