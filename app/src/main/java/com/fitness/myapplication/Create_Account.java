package com.fitness.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Pattern;

public class Create_Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        EditText editTextFullname=findViewById(R.id.editTextText);
        EditText editTextEmail=findViewById(R.id.editTextTextEmailAddress);
        EditText editTextPassword=findViewById(R.id.editTextTextPassword);
        EditText editTextPassword2=findViewById(R.id.editTextTextPassword2);
        Button signUpButton = findViewById(R.id.button);
        Button backbutton=findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Create_Account.this,Login.class);
                startActivity(intent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(isValidForm(editTextFullname,editTextEmail,editTextPassword,editTextPassword2)){
                    Intent intent = new Intent(Create_Account.this, PersonalDetails.class);
                    intent.putExtra("fullname", editTextFullname.getText().toString());
                    intent.putExtra("email", editTextEmail.getText().toString());
                    intent.putExtra("password", editTextPassword.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }
    private boolean isValidForm(EditText fullName,EditText email, EditText password,EditText password2){

        boolean valid=true;
        if(fullName.getText().toString().trim().isEmpty() || fullName.getText().toString().trim().length()<6){
            fullName.setError("Full Name must be at least 6 characters");
            valid=false;
        }
        if(email.getText().toString().trim().isEmpty() || !isValidEmail(email.getText().toString().trim())){
            email.setError("Invalid Email Address");
            valid=false;
        }
        if(password.getText().toString().trim().isEmpty() || !isValidPassword(password.getText().toString().trim())){
            password.setError("Password must be at least 8 characters, include a number and a special character");
            valid=false;
        }
        if(!password.getText().toString().trim().equals(password2.getText().toString().trim())){
            password.setError("Password should match");
            password2.setError("Password should match");
            valid=false;
        }
        DatabaseHelper dbHelper=DatabaseHelperFactory.getInstance(this);
        if(dbHelper.isEmailExists(email.getText().toString().trim())){
            email.setError("Email already in use");
            valid=false;
        }
        return valid;
    }
    private boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPassword(String password){
        Pattern specialCharPattern = Pattern.compile("[^a-z0-9]",Pattern.CASE_INSENSITIVE);
        Pattern digitPattern=Pattern.compile("[0-9]");
        return password.length()>=8 && specialCharPattern.matcher(password).find() && digitPattern.matcher(password).find();
    }
}