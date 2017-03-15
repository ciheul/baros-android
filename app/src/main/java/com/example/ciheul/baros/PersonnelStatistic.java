package com.example.ciheul.baros;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 13/03/17.
 */

public class PersonnelStatistic extends AppCompatActivity {

    private PieData pieData;
    private PieDataSet pieDataSet;
    PieChart mChart;

    String sName = "";
    String sNrpRank = "";
    String sPosisi = "";

    LinearLayout loadingHolder;
    LinearLayout emptyHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_statistic);

        loadingHolder = (LinearLayout) findViewById(R.id.loadingHolder);
        emptyHolder = (LinearLayout) findViewById(R.id.emptySetsHolder);
        mChart = (PieChart)findViewById(R.id.piechart);

        /*GET DATA PIE CHART*/
        getPieChartData();

        /*APPEND DATA TO FRONTEND*/
        getIntentData();

    }

    public void getPieChartData() {
        RequestParams params = new RequestParams();

        // get PK
        Bundle extras = getIntent().getExtras();
        int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));

        params.put("personnel_id", pk);

        loadingHolder.setVisibility(View.VISIBLE);
        emptyHolder.setVisibility(View.GONE);
        mChart.setVisibility(View.GONE);

        //request API
        RestClient.get("stat/personnel/"+pk+"/all/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                loadingHolder.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(s);

                    // get case info
                    JSONObject cases = (JSONObject) obj.get("case");
                    Integer open = cases.getInt("open");
                    Integer closed = cases.getInt("closed");
                    Integer total = open+closed;

                    TextView tOpen = (TextView)findViewById(R.id.personnel_kasus_berjalan);
                    tOpen.setText(String.valueOf(open));
                    TextView tClose = (TextView)findViewById(R.id.personnel_kasus_selesai);
                    tClose.setText(String.valueOf(closed));
                    TextView tTotal = (TextView)findViewById(R.id.personnel_total_kasus);
                    tTotal.setText(String.valueOf(total));

                    ArrayList<Entry> yPieChart = new ArrayList<Entry>();
                    ArrayList<String> xPieChart = new ArrayList<String>();

                    // get pie chart info
                    JSONArray dataPieChart = obj.getJSONArray("data");
                    for (int i = 0; i < dataPieChart.length() ; i++) {
                        JSONObject datas = dataPieChart.getJSONObject(i);
                        int yData = (Integer) datas.get("y");
                        String nama = (String) datas.get("name");

                        yPieChart.add(new Entry((float) yData, i));
                        xPieChart.add(nama);
                        System.out.println(nama);
                    }

                    // insert data into pie chart
                    pieChart(yPieChart, xPieChart);

                    // data is empty
                    if (total == 0) {
                        System.out.println("ini kosong");
                        loadingHolder.setVisibility(View.GONE);
                        emptyHolder.setVisibility(View.VISIBLE);
                        mChart.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void pieChart(ArrayList<Entry> yPieChart, ArrayList<String> xPieChart) {
        pieDataSet = new PieDataSet(yPieChart,"");
        pieData = new PieData(xPieChart, pieDataSet);

        mChart.setVisibility(View.VISIBLE);

        // chart configuration
        mChart.setUsePercentValues(true);
        mChart.setData(pieData);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        mChart.setDescription("");
        mChart.setDrawHoleEnabled(true);
        mChart.setTransparentCircleRadius(30f);
        mChart.setHoleRadius(30f);

        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(13f);
        pieData.setValueTextColor(Color.DKGRAY);
        mChart.animateXY(1400, 1400);

//        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        pieDataSet.setColors(colors);

        // Legends to show on bottom of the graph
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setWordWrapEnabled(true);
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

        TextView name = (TextView)findViewById(R.id.personnel_name);
        name.setText(sName);
        TextView nrpRank = (TextView)findViewById(R.id.personnel_rank_nrp);
        nrpRank.setText(sNrpRank);
        TextView posisi = (TextView)findViewById(R.id.personnel_position);
        posisi.setText(sPosisi);
    }

}
