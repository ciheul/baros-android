package com.example.ciheul.baros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.R.layout.simple_spinner_item;

/**
 * Created by ciheul on 24/02/17.
 * example api req:
 * http://localhost:10000/api/case/list/?p=1&progress_id=1&start_time=2016-12-01&end_time=2016-12-31
 * http://localhost:10000/api/case/list/?p=1&progress_id=7
 */
public class FilterCase extends AppCompatActivity {

    private DatePicker datePickerMonth;
    private DatePicker datePickerRange;
    private TextView textViewStartTime;
    private TextView textViewEndTime;
    private Spinner personnelDropdown;
    private Spinner progressDropdown;

    private Integer radioStats = 0;
    private Integer personnelId = -1;
    private Integer progressId = -1;
    private String start_time = "";
    private String end_time = "";

    HashMap<Integer,String> personnelMap = new HashMap<Integer, String>();
    HashMap<Integer,String> progressMap = new HashMap<Integer, String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_case);

        datePickerMonth = (DatePicker) findViewById(R.id.datePickerMonthly);
        datePickerRange = (DatePicker) findViewById(R.id.datePickerRange);
        textViewStartTime = (TextView) findViewById(R.id.textViewStartDate);
        textViewEndTime = (TextView) findViewById(R.id.textViewEndTime);

        progressDropdown = (Spinner) findViewById(R.id.progressStatusDropDown);
        personnelDropdown = (Spinner) findViewById(R.id.personnelDropDown);

        // TODO improve filter with previous filter
        fetchDropdownData();

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_grouper);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                final String value = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

                // checkedId is the RadioButton selected
                if (value.toLowerCase().equals("semua")) {
                    datePickerMonth.setVisibility(View.GONE);
                    datePickerRange.setVisibility(View.GONE);
                    textViewStartTime.setVisibility(View.GONE);
                    textViewEndTime.setVisibility(View.GONE);
                    radioStats = 0;
                }

                if (value.toLowerCase().equals("bulan")) {
                    datePickerMonth.setVisibility(View.VISIBLE);
                    datePickerRange.setVisibility(View.GONE);
                    textViewStartTime.setText("Bulan");
                    textViewStartTime.setVisibility(View.VISIBLE);
                    textViewEndTime.setVisibility(View.GONE);
                    radioStats = 1;
                }

                if (value.toLowerCase().equals("rentang")) {
                    datePickerMonth.setVisibility(View.VISIBLE);
                    datePickerRange.setVisibility(View.VISIBLE);
                    textViewStartTime.setText("Dari tanggal");
                    textViewStartTime.setVisibility(View.VISIBLE);
                    textViewEndTime.setVisibility(View.VISIBLE);
                    radioStats = 2;
                }

                System.out.println("radio button changed "+value);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_case_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.filter_case) {
            // should take view value each value and pass the data to your activity

            setFormData();
            /*
            homeIntent.putExtra("progress_id",1);
            homeIntent.putExtra("personnel_id",1);
            homeIntent.putExtra("start_time","2016-12-01");
            homeIntent.putExtra("end_time","2016-12-31");
            */
            System.out.println("Bundlesugar");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*PRIVATE FUNCTION*/
    /******************/
    private void setFormData() {
        // get progress id
        String progress = progressDropdown.getSelectedItem().toString();
        if (progress.toLowerCase().equals("semua")) {
            progressId = -1;
        } else {
            for (Map.Entry entry:progressMap.entrySet()) {
                if (progress.equals(entry.getValue())) {
                    progressId = (Integer) entry.getKey();
                    break;
                }
            }

        }

        // get personnelId
        String personnel = personnelDropdown.getSelectedItem().toString();
        if (personnel.toLowerCase().equals("semua")) {
            personnelId = -1;
        } else {
            for (Map.Entry entry: personnelMap.entrySet()) {
                if (personnel.equals(entry.getValue())) {
                    personnelId = (Integer) entry.getKey();
                    break;
                }
            }
        }

        // get start_time and end_time
        datePickerMonth = (DatePicker) findViewById(R.id.datePickerMonthly);
        datePickerRange = (DatePicker) findViewById(R.id.datePickerRange);

        // TODO logic jika end_time < start_time
        String dateMonthly = DateToSolrFormatter(datePickerMonth);
        String dateEnd = DateToSolrFormatter(datePickerRange);

        if (radioStats == 0) {
            start_time = "";
            end_time = "";
        }

        if (radioStats == 1) {
            start_time = getEndDateMonthly(datePickerMonth);
            end_time = dateMonthly;
        }

        if (radioStats == 2) {
            start_time = dateMonthly;
            end_time = dateEnd;
        }


        Intent homeIntent = new Intent(this, Home.class);
        if (progressId != -1) {
            homeIntent.putExtra("progress_id",progressId);
        }

        if (personnelId != -1) {
            homeIntent.putExtra("personnel_id",personnelId);
        }

        if (!start_time.equals("") || !start_time.isEmpty()) {
            homeIntent.putExtra("start_time",start_time);
        }

        if (!end_time.equals("") || !end_time.isEmpty()) {
            homeIntent.putExtra("end_time",end_time);
        }

        startActivity(homeIntent);
    }


    public void fetchDropdownData() {
        // find id based on progress
        ArrayList<DropdownDataResult> hasils = new ArrayList<DropdownDataResult>();
        // populate progress spinner(dropdown)
        final ArrayList<String> progressName = new ArrayList<String>();
        // populate penyidik spinner(dropdown)
        final ArrayList<String> penyidikName = new ArrayList<String>();

        progressName.add("Semua");
        penyidikName.add("Semua");

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
                            DropdownDataResult dropdownDataResult = new DropdownDataResult();
                            dropdownDataResult.setProgressId((Integer) progress.getJSONArray(i).get(0));
                            dropdownDataResult.setProgressName((String) progress.getJSONArray(i).get(1));

                            progressMap.put(dropdownDataResult.getProgressId(), dropdownDataResult.getProgressName());
                            progressName.add(dropdownDataResult.getProgressName());
                        }
                    }

                    // get penyidik
                    for (int i = 0; i < penyidik.length(); i++) {
                        JSONObject object = penyidik.getJSONObject(i);
                        JSONObject fields = (JSONObject) object.get("fields");

                        DropdownDataResult dropdownDataResult = new DropdownDataResult();
                        dropdownDataResult.setPenyidikId((Integer) object.get("pk"));
                        dropdownDataResult.setPenyidikName((String) fields.get("name"));

                        personnelMap.put(dropdownDataResult.getPenyidikId(), dropdownDataResult.getPenyidikName());
                        penyidikName.add(dropdownDataResult.getPenyidikName());
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error Occured [Server's JSON response might be invalid]!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                // spinner progress
                progressDropdown = (Spinner) findViewById(R.id.progressStatusDropDown);
                progressDropdown.setAdapter(new ArrayAdapter<String>(FilterCase.this, simple_spinner_item, progressName));

                // spinner penyidik
                personnelDropdown = (Spinner) findViewById(R.id.personnelDropDown);
                personnelDropdown.setAdapter(new ArrayAdapter<String>(FilterCase.this, simple_spinner_item, penyidikName));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 404){
                    // When Http response code is '404'
                    Toast.makeText(getApplicationContext(),
                            "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                else if(statusCode == 500){
                    // When Http response code is '500'
                    Toast.makeText(getApplicationContext(),
                            "Error 500", Toast.LENGTH_LONG).show();
                }
                else{
                    // When Http response code other than 404, 500
                    Toast.makeText(getApplicationContext(),
                            "Unexpected Error occcured! [Most common Error: Device might not be " +
                                    "connected to Internet or remote server is not up and running]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class DropdownDataResult {
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

    private String DateToSolrFormatter(DatePicker datePicker) {
        DecimalFormat formatter = new DecimalFormat("00");
        String day = String.valueOf(datePicker.getDayOfMonth());
        String month = formatter.format(datePicker.getMonth() + 1);
        String year = String.valueOf(datePicker.getYear());
        return (String) String.valueOf(year + "-" + month + "-" + day);
    }

    private String getEndDateMonthly(DatePicker datePicker) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -30);
        int y = now.get(Calendar.YEAR);
        int m = now.get(Calendar.MONTH); // it's zero based
        int d = now.get(Calendar.DAY_OF_MONTH);

//        datePicker.updateDate(y, m, d);

        DecimalFormat formatter = new DecimalFormat("00");
        String day = String.valueOf(d);
        String month = formatter.format(m + 1);
        String year = String.valueOf(y);
        return (String) String.valueOf(year + "-" + month + "-" + day);
    }

}
