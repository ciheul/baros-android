package com.example.ciheul.baros;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ciheul.baros.Fragments.CasesFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 24/02/17.
 */
public class CaseDetail extends AppCompatActivity {

    // Process Dialog Object
    ProgressDialog prgDialog;

    // Textview object
    TextView tProgress;
    TextView tNumber;
    TextView tType;
    TextView tDesc;
    TextView tDimulai;
    TextView tPelapor;
    TextView tTerlapor;
    TextView tSpdp;
    TextView tHambatan;
    TextView tKet;
    TextView tPersonnel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);

        tProgress = (TextView)findViewById(R.id.case_detail_progress);
        tNumber = (TextView)findViewById(R.id.case_detail_number);
        tType = (TextView)findViewById(R.id.case_detail_type);
        tDesc = (TextView)findViewById(R.id.case_detail_desc);
        tDimulai = (TextView)findViewById(R.id.case_detail_dimulai);
        tPelapor = (TextView)findViewById(R.id.case_detail_pelapor);
        tTerlapor = (TextView)findViewById(R.id.case_detail_terlapor);
        tSpdp = (TextView)findViewById(R.id.case_detail_spdp);
        tHambatan = (TextView)findViewById(R.id.case_detail_hambatan);
        tKet = (TextView)findViewById(R.id.case_detail_ket);
        tPersonnel = (TextView)findViewById(R.id.case_detail_penyidik);

        // Instantiate progress dialog object
        prgDialog = new ProgressDialog(this);
        // Set progress dialog text
        prgDialog.setMessage("Please wait ...");
        // Set cancelable as false
        prgDialog.setCancelable(false);

        getCaseDetail();
    }

    private void getCaseDetail() {
        // Show progress dialog
        prgDialog.show();

        RequestParams params = new RequestParams();

        Bundle extras = getIntent().getExtras();

        int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));
        params.put("case_id", pk);

        RestClient.post("case/"+pk+"/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                String s = new String(responseBody);
                System.out.println(s);
                // Hide Progress Dialog
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    String cases = (String) obj.get("case");
                    String progress = (String) obj.get("progress");
                    String penyidik = (String) obj.get("personnel");
                    String dimulai = (String) obj.get("lp_date");
                    JSONArray detail = new JSONArray(cases);

                    JSONObject object = detail.getJSONObject(0);
                    JSONObject fields = (JSONObject) object.get("fields");

                    System.out.println(progress);

                    // insert data to textview
                    tProgress.setText(progress);
                    tNumber.setText(fields.getString("lp_number"));
                    tType.setText(fields.getString("lp_type"));
                    tDesc.setText(fields.getString("description"));
                    tPelapor.setText(fields.getString("reported_by"));
                    tTerlapor.setText(fields.getString("reported"));
                    tSpdp.setText(fields.getString("spdp"));
                    tHambatan.setText(fields.getString("obstacle"));
                    tKet.setText(fields.getString("note"));
                    tPersonnel.setText(penyidik);
                    tDimulai.setText(dimulai);

                    /**
                    ** NEXT: show upload attachment + personnel img
                    **/

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

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_case_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.case_archive) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("Apakah Anda yakin untuk mengarsipkan kasus ini?")
                    .setTitle("Arsipkan kasus");

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("klik ok");

                    // Instantiate progress dialog object
                    prgDialog = new ProgressDialog(CaseDetail.this);
                    // Set progress dialog text
                    prgDialog.setMessage("Please wait ...");
                    // Set cancelable as false
                    prgDialog.setCancelable(false);

                    sendArchiveApi();

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.show();
        }

        if (id == R.id.edit_case) {
            String lp_number = tNumber.getText().toString();
            String lp_progress = tProgress.getText().toString();
            String lp_type = tType.getText().toString();
            String lp_desc = tDesc.getText().toString();
            String lp_date = tDimulai.getText().toString();
            String lp_reported_by = tPelapor.getText().toString();
            String lp_reported = tPelapor.getText().toString();
            String lp_ket = tKet.getText().toString();
            String lp_personnel = tPersonnel.getText().toString();
            String lp_spdp = tSpdp.getText().toString();
            String lp_hambatan = tHambatan.getText().toString();
            Bundle extras = getIntent().getExtras();
            int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));

            Intent intent = new Intent(this, EditCase.class);
            intent.putExtra("progress", lp_progress);
            intent.putExtra("number", lp_number);
            intent.putExtra("type", lp_type);
            intent.putExtra("desc", lp_desc);
            intent.putExtra("dimulai", lp_date);
            intent.putExtra("pelapor", lp_reported_by);
            intent.putExtra("terlapor", lp_reported);
            intent.putExtra("personnel", lp_personnel);
            intent.putExtra("spdp", lp_spdp);
            intent.putExtra("hambatan", lp_hambatan);
            intent.putExtra("ket", lp_ket);
            intent.putExtra("pk", pk);

            startActivity(intent);
        }

        if (id == R.id.upload_document) {
            // do upload document
        }

        if (id == R.id.upload_tersangka) {
            // upload suspect
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendArchiveApi() {
        // Show progress dialog
        prgDialog.show();

        RequestParams params = new RequestParams();

        Bundle extras = getIntent().getExtras();
        final int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));
        params.put("pk", pk);

        RestClient.post("case/delete/case/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                String s = new String(responseBody);
                System.out.println(s);

                archiveList();
                Toast.makeText(getApplicationContext(),"Kasus berhasil diarsipkan.", Toast.LENGTH_LONG).show();

/*                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);

                    Toast.makeText(getApplicationContext(),"Kasus berhasil diarsipkan.", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured " +
                            "[Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }*/

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
                    Toast.makeText(getApplicationContext(), "Internal server error. Please try again.", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void archiveList() {
        Intent intent = new Intent(CaseDetail.this, Home.class);
        startActivity(intent);
    }
}
