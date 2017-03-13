package com.example.ciheul.baros;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ciheul on 13/03/17.
 */

public class PersonnelStatistic extends AppCompatActivity {

    String sName = "";
    String sNrpRank = "";
    String sPosisi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_statistic);

        getIntentData();

        TextView name = (TextView)findViewById(R.id.personnel_name);
        name.setText(sName);
        TextView nrpRank = (TextView)findViewById(R.id.personnel_rank_nrp);
        nrpRank.setText(sNrpRank);
        TextView posisi = (TextView)findViewById(R.id.personnel_position);
        posisi.setText(sPosisi);

    }

    public void getIntentData() {
        Bundle extras = getIntent().getExtras();
        int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));

        Intent getIntent = getIntent();
        if (null != getIntent) {
            sName = getIntent.getStringExtra("nama");
            sNrpRank = getIntent.getStringExtra("nrpRank");
            sPosisi = getIntent.getStringExtra("posisi");
        }

    }
}
