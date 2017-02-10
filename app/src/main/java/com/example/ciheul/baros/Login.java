package com.example.ciheul.baros;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public class Login extends AppCompatActivity {
    // Process Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    // Email edit view object
    EditText emailET;
    // Password edit view object
    EditText pwdET;
    // Image view
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        img = (ImageView)findViewById(R.id.logo);
        // Find error msg text view control by ID
        errorMsg = (TextView)findViewById(R.id.loginError);
        // Find email edit view control by ID
        emailET = (EditText)findViewById(R.id.loginEmail);
        // Find password edit view control by ID
        pwdET = (EditText)findViewById(R.id.loginPassword);
        // Instantiate progress dialog object
        prgDialog = new ProgressDialog(this);
        // Set progress dialog text
        prgDialog.setMessage("Please wait ...");
        // Set cancelable as false
        prgDialog.setCancelable(false);
    }

    public void btnLogin(View v) {
        // Get email value
        String email = emailET.getText().toString();
        // Get password value
        String password = pwdET.getText().toString();
        // Instantiate http request param object
        RequestParams params = new RequestParams();

        // When email and password is not null
        if (Utility.isNotNull(email) && Utility.isNotNull(password)) {
            // Email is valid
            if (Utility.validate(email)) {
                // Put http params email
                params.put("email", email);
                // Put http params password
                params.put("password", password);
                // Invoke RESTful
                invokeWS(params);
            }
            // Email is invalid
            else {
                Toast.makeText(getApplicationContext(), "Format email salah.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Isian tidak lengkap.", Toast.LENGTH_LONG).show();
        }
    }


    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        RestClient.post("login/",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                System.out.println("eh!");
                String s = new String(responseBody);
                System.out.println(s);

                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    String response = obj.getString("success");
                    homeActivity();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Kombinasi email dan password tidak cocok.", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void homeActivity() {
        Intent homeIntent = new Intent(this, Home.class);
        startActivity(homeIntent);
    }

    public void btnLinkToRegister(View v) {
        // Open registration activity
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void btnGuest(View v) {
        // Open home activity
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}
