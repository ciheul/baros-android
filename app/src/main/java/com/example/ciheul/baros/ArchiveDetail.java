package com.example.ciheul.baros;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 06/03/17.
 */

public class ArchiveDetail extends AppCompatActivity {
    // Process Dialog Object
    ProgressDialog prgDialog;

    String sNote = "";
    String sProgress = "";
    String sNumber = "";
    String sType = "";
    String sDesc = "";
    String sStart = "";
    String sReported = "";
    String sReportedBy = "";
    String sSpdp = "";
    String sObstacle = "";
    String sPersonnel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_detail);

        // get archive detail
        getArchiveDetail();

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_archive_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.restore_archive) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("Apakah Anda yakin untuk memulihkan kasus ini?")
                    .setTitle("Pulihkan kasus");

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("klik ok");

                    // Instantiate progress dialog object
                    prgDialog = new ProgressDialog(ArchiveDetail.this);
                    // Set progress dialog text
                    prgDialog.setMessage("Please wait ...");
                    // Set cancelable as false
                    prgDialog.setCancelable(false);

                    sendRestoreApi();

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

        if (id == R.id.delete_permanent) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("Apakah Anda yakin untuk menghapus kasus ini secara permanen?")
                    .setTitle("Hapus Permanen");

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("klik ok");

                    // Instantiate progress dialog object
                    prgDialog = new ProgressDialog(ArchiveDetail.this);
                    // Set progress dialog text
                    prgDialog.setMessage("Please wait ...");
                    // Set cancelable as false
                    prgDialog.setCancelable(false);

                    sendDeleteApi();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println("klik cancel");
                    dialog.dismiss();
                }
            });

            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendDeleteApi() {
        // Show progress dialog
        prgDialog.show();

        RequestParams params = new RequestParams();

        Bundle extras = getIntent().getExtras();
        final int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));
        params.put("pk", pk);

        RestClient.post("archive/delete/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                String s = new String(responseBody);

                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    String response = obj.getString("success");
                    Toast.makeText(getApplicationContext(),"Kasus " +
                            "berhasil dihapus.", Toast.LENGTH_LONG).show();
                    archiveList();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured " +
                            "[Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Internal server error. Please try again.", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendRestoreApi() {
        // Show progress dialog
        prgDialog.show();

        RequestParams params = new RequestParams();

        Bundle extras = getIntent().getExtras();
        final int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));
        params.put("pk", pk);

        RestClient.post("archive/restore/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                String s = new String(responseBody);

                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(s);
                    String response = obj.getString("success");
                    Toast.makeText(getApplicationContext(),"Kasus berhasil dipulihkan." +
                            " Kasus dapat dilihat kembali pada laman utama.", Toast.LENGTH_LONG).show();
                    archiveList();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured " +
                            "[Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(getApplicationContext(), ArchiveList.class);
        startActivity(intent);
    }


    public void getArchiveDetail() {
        Intent intent = getIntent();
        if (null != intent) {
            sProgress = intent.getStringExtra("progress");
            sNumber = intent.getStringExtra("number");
            sType = intent.getStringExtra("type");
            sDesc = intent.getStringExtra("description");
            sStart = intent.getStringExtra("lp_date");
            sReported = intent.getStringExtra("reported");
            sReportedBy = intent.getStringExtra("reported_by");
            sSpdp = intent.getStringExtra("spdp");
            sObstacle = intent.getStringExtra("obstacle");
            sNote = intent.getStringExtra("note");
            sPersonnel = intent.getStringExtra("personnel_name");
        }

        TextView tProgress = (TextView)findViewById(R.id.archive_detail_progress);
        tProgress.setText(sProgress);

        TextView tNumber = (TextView)findViewById(R.id.archive_detail_number);
        tNumber.setText(sNumber);

        TextView tType = (TextView)findViewById(R.id.archive_detail_type);
        tType.setText(sType);

        TextView tDesc = (TextView)findViewById(R.id.archive_detail_desc);
        tDesc.setText(sDesc);

        TextView tStart = (TextView)findViewById(R.id.archive_detail_dimulai);
        tStart.setText(sStart);

        TextView tReported = (TextView)findViewById(R.id.archive_detail_terlapor);
        tReported.setText(sReported);

        TextView tReportedBy = (TextView)findViewById(R.id.archive_detail_pelapor);
        tReportedBy.setText(sReportedBy);

        TextView tSpdp = (TextView)findViewById(R.id.archive_detail_spdp);
        tSpdp.setText(sSpdp);

        TextView tObstacle = (TextView)findViewById(R.id.archive_detail_hambatan);
        tObstacle.setText(sObstacle);

        TextView tNote = (TextView)findViewById(R.id.archive_detail_ket);
        tNote.setText(sNote);

        TextView tPersonnel = (TextView)findViewById(R.id.archive_detail_penyidik);
        tPersonnel.setText(sPersonnel);

        Bundle extras = getIntent().getExtras();
        int pk = extras.getInt("pk");

    }


}
