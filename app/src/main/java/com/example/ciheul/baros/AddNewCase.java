package com.example.ciheul.baros;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

/**
 * Created by ciheul on 20/02/17.
 */

public class AddNewCase extends AppCompatActivity {
    // Process Dialog Object
    ProgressDialog prgDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_case);

        // Instantiate progress dialog object
        prgDialog = new ProgressDialog(this);
        // Set progress dialog text
        prgDialog.setMessage("Please wait ...");
        // Set cancelable as false
        prgDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.save_new_case) {
            // do save new case
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
