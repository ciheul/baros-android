package com.example.ciheul.baros;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;


public class Registration extends AppCompatActivity {
    // Process Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    // Edit view
    EditText regFullnameET;
    EditText regEmailET;
    EditText regKtpET;
    EditText regPassET;
    EditText regConfPassET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find error msg text view control by ID
        errorMsg = (TextView)findViewById(R.id.loginError);
        // Get all information from text view
        regFullnameET = (EditText)findViewById(R.id.regFullname);
        regKtpET = (EditText)findViewById(R.id.regKtp);
        regEmailET = (EditText)findViewById(R.id.regEmail);
        regPassET = (EditText)findViewById(R.id.regPass);
        regConfPassET = (EditText)findViewById(R.id.regConfPass);
        // Instantiate progress dialog object
        prgDialog = new ProgressDialog(this);
        // Set progress dialog text
        prgDialog.setMessage("Please wait ...");
        // Set cancelable as false
        prgDialog.setCancelable(false);

    }

    public void btnRegistration(View v) {

    }


}
