package com.example.ciheul.baros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ciheul on 06/03/17.
 */

public class ArchiveDetail extends AppCompatActivity {

    String sProgress = "";
    String sNumber = "";
    String sType = "";
    String sDesc = "";
    String sStart = "";
    String sReported = "";
    String sReportedBy = "";
    String sSpdp = "";
    String sObstacle = "";
    String sNote = "";
    String sPersonnel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_detail);

        // get archive detail
        getArchiveDetail();

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

        System.out.println(pk);

    }
}
