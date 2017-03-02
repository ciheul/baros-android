package com.example.ciheul.baros;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static android.R.layout.simple_spinner_item;

/**
 * Created by ciheul on 28/02/17.
 */
public class EditCase extends AppCompatActivity{
    HashMap<Integer,String> penyidikMap = new HashMap<Integer, String>();
    HashMap<Integer,String> progressMap = new HashMap<Integer, String>();

    // initialize data from intent
    String progress = "";
    String number = "";
    String type ="";
    String desc = "";
    String dimulai = "";
    String pelapor = "";
    String terlapor = "";
    String personnel = "";
    String spdp = "";
    String hambatan = "";
    String ket = "";

    // Edit view object
    EditText lpET;
    EditText perkaraET;
    EditText uraianET;
    EditText pelaporET;
    EditText terlaporET;
    EditText spdpET;
    EditText hambatanET;
    EditText keteranganET;

    // Process Dialog Object
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_case);

        // get all information from case detail
        Intent intent = getIntent();
        if (null != intent) {
            progress = intent.getStringExtra("progress");
            number = intent.getStringExtra("number");
            type = intent.getStringExtra("type");
            desc = intent.getStringExtra("desc");
            dimulai = intent.getStringExtra("dimulai");
            pelapor = intent.getStringExtra("pelapor");
            terlapor= intent.getStringExtra("terlapor");
            personnel = intent.getStringExtra("personnel");
            spdp = intent.getStringExtra("spdp");
            hambatan = intent.getStringExtra("hambatan");
            ket = intent.getStringExtra("ket");
        }

        Bundle extras = getIntent().getExtras();
        int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));

        // set data
        TextView tNumber = (TextView)findViewById(R.id.edit_case_lp);
        tNumber.setText(number);

        TextView tType = (TextView)findViewById(R.id.edit_case_perkara);
        tType.setText(type);

        TextView tDesc = (TextView)findViewById(R.id.edit_case_uraian);
        tDesc.setText(desc);

        TextView tPelapor = (TextView)findViewById(R.id.edit_case_pelapor);
        tPelapor.setText(pelapor);

        TextView tTerlapor = (TextView)findViewById(R.id.edit_case_terlapor);
        tTerlapor.setText(terlapor);

        TextView tSpdp = (TextView)findViewById(R.id.edit_case_spdp);
        tSpdp.setText(spdp);

        TextView tHambatan = (TextView)findViewById(R.id.edit_case_hambatan);
        tHambatan.setText(hambatan);

        TextView tKet = (TextView)findViewById(R.id.edit_case_keterangan);
        tKet.setText(ket);

        // set datepicker
        SimpleDateFormat dayformat = new SimpleDateFormat("dd");
        Integer day = Integer.valueOf(dayformat.format(Date.parse(dimulai)));

        SimpleDateFormat monthformat = new SimpleDateFormat("MM");
        Integer month = Integer.valueOf(monthformat.format(Date.parse(dimulai)));

        SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        Integer year = Integer.valueOf(yearformat.format(Date.parse(dimulai)));

        DatePicker datePicker = (DatePicker)findViewById(R.id.edit_date_picker);
        datePicker.updateDate(year, month-1, day);


        // get data penyidik + progress from db
        // NOTE: set current position still doesn't work
        getDataDropdown(personnel, progress);


    }

    public void getDataDropdown(String personnel, final String progress) {
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

                            progressMap.put(hasil.getProgressId(), hasil.getProgressName());
                            progressName.add(hasil.getProgressName());
                        }
                    }

                    // get penyidik
                    for (int i = 0; i < penyidik.length(); i++) {
                        JSONObject object = penyidik.getJSONObject(i);
                        JSONObject fields = (JSONObject) object.get("fields");

                        Hasil hasil = new Hasil();
                        hasil.setPenyidikId((Integer) object.get("pk"));
                        hasil.setPenyidikName((String) fields.get("name"));

                        penyidikMap.put(hasil.getPenyidikId(), hasil.getPenyidikName());
                        penyidikName.add(hasil.getPenyidikName());
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(),
                            "Error Occured [Server's JSON response might be invalid]!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                // spinner progress
                Spinner progressSpinner = (Spinner) findViewById(R.id.dropdown_edit_progress);
                progressSpinner.setAdapter(new ArrayAdapter<String>
                        (EditCase.this, simple_spinner_item, progressName));

                // spinner penyidik
                Spinner penyidikSpinner = (Spinner) findViewById(R.id.dropdown_edit_penyidik);
                penyidikSpinner.setAdapter(new ArrayAdapter<String>
                        (EditCase.this, simple_spinner_item, penyidikName));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! " +
                            "[Most common Error: Device might not be connected to Internet or " +
                            "remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_case, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.back_to_case_detail) {
            finish();
        }

        if (id == R.id.submit_edit_case) {
            // Instantiate progress dialog object
            prgDialog = new ProgressDialog(this);
            // Set progress dialog text
            prgDialog.setMessage("Please wait ...");
            // Set cancelable as false
            prgDialog.setCancelable(false);

            // Set edit text
            lpET = (EditText)findViewById(R.id.edit_case_lp);
            perkaraET = (EditText)findViewById(R.id.edit_case_perkara);
            uraianET = (EditText)findViewById(R.id.edit_case_uraian);
            pelaporET = (EditText)findViewById(R.id.edit_case_pelapor);
            terlaporET = (EditText)findViewById(R.id.edit_case_terlapor);
            spdpET = (EditText)findViewById(R.id.edit_case_spdp);
            hambatanET = (EditText)findViewById(R.id.edit_case_hambatan);
            keteranganET = (EditText)findViewById(R.id.edit_case_keterangan);

            // get all data
            String lp = lpET.getText().toString();
            String perkara = perkaraET.getText().toString();
            String uraian = uraianET.getText().toString();
            String pelapor = pelaporET.getText().toString();
            String terlapor = terlaporET.getText().toString();
            String spdp = spdpET.getText().toString();
            String hambatan = hambatanET.getText().toString();
            String keterangan = keteranganET.getText().toString();

            Spinner proSpinner = (Spinner) findViewById(R.id.dropdown_edit_progress);
            String proText = proSpinner.getSelectedItem().toString();
            Integer proId = null;

            // get progress id
            for (Map.Entry entry:progressMap.entrySet()) {
                if (proText.equals(entry.getValue())) {
                    proId = (Integer) entry.getKey();
                    break;
                }
            }

            Spinner penSpinner = (Spinner) findViewById(R.id.dropdown_edit_penyidik);
            String penText = penSpinner.getSelectedItem().toString();
            Integer penId = null;

            // get penyidik id
            for (Map.Entry entry:penyidikMap.entrySet()) {
                if (penText.equals(entry.getValue())) {
                    penId = (Integer) entry.getKey();
                    break;
                }
            }

            // check required params
            // lp, perkara, uraian
            if(TextUtils.isEmpty(lp)) {
                lpET.setError("Wajib diisi");
            }

            if(TextUtils.isEmpty(perkara)) {
                perkaraET.setError("Wajib diisi");
            }

            if(TextUtils.isEmpty(uraian)) {
                uraianET.setError("Wajib diisi");
            }

            DatePicker datePicker = (DatePicker)findViewById(R.id.edit_date_picker);
            // change format month (ex: 2 -> 02)
            DecimalFormat formatter = new DecimalFormat("00");

            String day = String.valueOf(datePicker.getDayOfMonth());
            String month = formatter.format(datePicker.getMonth() + 1);
            String year = String.valueOf(datePicker.getYear());

            String date = String.valueOf(year + "-" + month + "-" + day);

            RequestParams params = new RequestParams();
            params.put("lp_number", lp);
            params.put("lp_date", date);
            params.put("lp_type", perkara);
            params.put("reported_by", pelapor);
            params.put("reported", terlapor);
            params.put("note", keterangan);
            params.put("description", uraian);
            params.put("spdp", spdp);
            params.put("obstacle", hambatan);
            params.put("progress", proId);
            params.put("personnel", penId);

            System.out.println(date);

            sendAPI(params);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendAPI(RequestParams params) {
        // Show progress dialog
        prgDialog.show();

        Bundle extras = getIntent().getExtras();
        final int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));
        params.put("case_id", pk);

        RestClient.post("case/update/"+pk+"/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                String s = new String(responseBody);
                System.out.println(s);

                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    String response = obj.getString("success");
                    System.out.println("ini sebelum"+pk);
                    caseDetail(pk);
                    Toast.makeText(getApplicationContext(),"Kasus berhasil di perbarui.", Toast.LENGTH_LONG).show();

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

    public void caseDetail(Integer pk) {
        Intent cd = new Intent(getApplicationContext(), CaseDetail.class);
        System.out.println("ini loh"+pk);
        cd.putExtra("pk", pk);
        startActivity(cd);
    }
}
