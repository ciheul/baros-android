package com.example.ciheul.baros;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.R.layout.simple_spinner_item;

/**
 * Created by ciheul on 20/02/17.
 */

public class AddNewCase extends AppCompatActivity {
    // Process Dialog Object
    ProgressDialog prgDialog;

    // Error Msg TextView Object
    TextView errorMsg;

    // Dropdown object
    Spinner progressSP;
    Spinner penyidikSP;

    // Edit view object
    EditText lpET;
    EditText perkaraET;
    EditText uraianET;
    EditText pelaporET;
    EditText terlaporET;
    EditText spdpET;
    EditText hambatanET;
    EditText keteranganET;

    TextView dateText;

    // Date picker
    DatePickerDialog datePickerDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_case);

        // initiate the date picker and button
        final Button dateET = (Button) findViewById(R.id.new_case_kasus_dimulai);

        // perform click event date picker
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // NOTE: January = 0;
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int mYear = calendar.get(java.util.Calendar.YEAR);
                int mMonth = calendar.get(java.util.Calendar.MONTH);
                int mDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(AddNewCase.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                dateText = (TextView)findViewById(R.id.result_date);
                                dateText.setText("Tanggal: " + dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // Get progress and penyidik from api
        getDataDropdown();

         // Instantiate progress dialog object
        prgDialog = new ProgressDialog(this);
        // Set progress dialog text
        prgDialog.setMessage("Please wait ...");
        // Set cancelable as false
        prgDialog.setCancelable(false);
    }

    public void getDataDropdown() {
        // find id based on progress
        ArrayList<Hasil> hasils = new ArrayList<Hasil>();
        // populate progress spinner(dropdown)
        final ArrayList<String> progressName = new ArrayList<String>();
        // populate penyidik spinner(dropdown)
        final ArrayList<String> penyidikName = new ArrayList<String>();

        RestClient.get("personnel/", null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);

                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    JSONArray progress = obj.getJSONArray("progress_statuses");

                    String sidik = (String) obj.get("personnels");
                    JSONArray penyidik = new JSONArray(sidik);

                    // get progress
                    for (int i = 0; i < progress.length(); i++) {
                        int proId = (int) progress.getJSONArray(i).get(0);

                        // hide progressId = 0 (tanpa status)
                        if (proId != 0) {
                            Hasil hasil = new Hasil();
                            hasil.setProgressId((Integer) progress.getJSONArray(i).get(0));
                            hasil.setProgressName((String) progress.getJSONArray(i).get(1));

                            progressName.add((String) progress.getJSONArray(i).get(1));
                        }
                    }

                    // get penyidik
                    for (int i = 0; i < penyidik.length(); i++) {
                        JSONObject object = penyidik.getJSONObject(i);
                        JSONObject fields = (JSONObject) object.get("fields");

                        Hasil hasil = new Hasil();
                        hasil.setPenyidikId((Integer) object.get("pk"));
                        hasil.setPenyidikName((String) fields.get("name"));

                        penyidikName.add((String) fields.get("name"));
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                // spinner progress
                Spinner progressSpinner = (Spinner) findViewById(R.id.dropdown_progress);
                progressSpinner.setAdapter(new ArrayAdapter<String>(AddNewCase.this, simple_spinner_item, progressName));

                // spinner penyidik
                Spinner penyidikSpinner = (Spinner) findViewById(R.id.dropdown_penyidik);
                penyidikSpinner.setAdapter(new ArrayAdapter<String>(AddNewCase.this, simple_spinner_item, penyidikName));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
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

        if (id == R.id.dropdown_progress) {
            System.out.println("kyaa");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // temp spinner information
    public class Hasil {
        private int progressId;
        private String progressName;
        private int penyidikId;
        private String penyidikName;

        public int getProgressId() {
            return progressId;
        }

        public void setProgressId(int progressId) {
            this.progressId = progressId;
        }

        public String getProgressName() {
            return progressName;
        }

        public void setProgressName(String progressName) {
            this.progressName = progressName;
        }

        public int getPenyidikId() {
            return penyidikId;
        }

        public void setPenyidikId(int penyidikId) {
            this.penyidikId = penyidikId;
        }

        public String getPenyidikName() {
            return penyidikName;
        }

        public void setPenyidikName(String penyidikName) {
            this.penyidikName = penyidikName;
        }

    }

}
