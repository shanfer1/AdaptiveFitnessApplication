package com.fitness.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {


    private EditText usernameText;
    private EditText passwordText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialise();

        setContentView(R.layout.activity_login);
        Button createAccount=findViewById(R.id.createaccount_button);
        Button loginButton=findViewById(R.id.loginbutton);
        usernameText=findViewById(R.id.usernameText);
        passwordText=findViewById(R.id.passwordText);

        loginButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view){

                attemptLogin();
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View view){
                navigateToCreateAccount();
            }
        });

    }
    private void attemptLogin(){
        Log.d("Login Class","the class used for");

        String username=usernameText.getText().toString();
        String password=passwordText.getText().toString();
        if(username.isEmpty()){
            usernameText.setError("Username should not be empty");
        }
        if(password.isEmpty()){
            passwordText.setError("Password should not be empty");
        }
        else{
            boolean isValid=checkCredentials(username,password);
            Log.d("Login Class","the class used for"+isValid);
            if(!isValid){
                usernameText.setError("Invalid Credentials");
                passwordText.setError("Invalid Credentials");
            }else{
                logCurrentUser(username);
                Intent intent=new Intent(Login.this,Home.class);
                startActivity(intent);
            }
        }

    }
    private void logCurrentUser(String emailID){
        SharedPreferences sharedPreferences=getSharedPreferences("MySharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("LoggedInUser",emailID);
        editor.apply();
    }


    private boolean checkCredentials(String username,String password){
        DatabaseHelper dbHelper=DatabaseHelperFactory.getInstance(Login.this);
        return dbHelper.getCredentials(username,password);

    }


    private void navigateToCreateAccount() {
        Intent intent = new Intent(Login.this, Create_Account.class);
        startActivity(intent);
    }

    private void initialise() {
        AppInitializer.initializeApp(this);
    }
}