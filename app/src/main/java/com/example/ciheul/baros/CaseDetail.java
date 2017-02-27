package com.example.ciheul.baros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

/**
 * Created by ciheul on 24/02/17.
 */
public class CaseDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);

        String sProgress = "";
        String sNumber = "";
        String sType ="";
        String sDesc = "";
        String sDimulai = "";
        String sPelapor = "";
        String sTerlapor = "";
        String sPersonnel = "";

        // get case detail information
        Intent intent = getIntent();
        if (null != intent) {
            sProgress = intent.getStringExtra("progress");
            sNumber = intent.getStringExtra("number");
            sType = intent.getStringExtra("type");
            sDesc = intent.getStringExtra("description");
            sDimulai = intent.getStringExtra("lp_date");
            sPelapor = intent.getStringExtra("reported_by");
            sTerlapor= intent.getStringExtra("reported");
            sPersonnel = intent.getStringExtra("personnel_name");
        }

        TextView tProgress = (TextView)findViewById(R.id.case_detail_progress);
        tProgress.setText(sProgress);

        TextView tNumber = (TextView)findViewById(R.id.case_detail_number);
        tNumber.setText(sNumber);

        TextView tType = (TextView)findViewById(R.id.case_detail_type);
        tType.setText(sType);

        TextView tDesc = (TextView)findViewById(R.id.case_detail_desc);
        tDesc.setText(sDesc);

        TextView tDimulai = (TextView)findViewById(R.id.case_detail_dimulai);
        tDimulai.setText(sDimulai);

        TextView tPelapor = (TextView)findViewById(R.id.case_detail_pelapor);
        tPelapor.setText(sPelapor);

        TextView tTerlapor = (TextView)findViewById(R.id.case_detail_terlapor);
        tTerlapor.setText(sTerlapor);

        TextView tPersonnel = (TextView)findViewById(R.id.case_detail_penyidik);
        tPersonnel.setText(sPersonnel);

        Bundle extras = getIntent().getExtras();
        int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));

/*
        // NEXT: show upload attachment + personnel img
        RequestParams params = new RequestParams();
        params.put("pk", pk);
*/


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
            // do archive
        }

        if (id == R.id.edit_case) {
            // do edit case
        }

        if (id == R.id.upload_document) {
            // do upload document
        }

        if (id == R.id.upload_tersangka) {
            // upload suspect
        }
        return super.onOptionsItemSelected(item);
    }

}
