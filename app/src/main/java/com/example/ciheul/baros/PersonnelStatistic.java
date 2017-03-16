package com.example.ciheul.baros;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

    /* Pie Chart Kinerja Keseluruhan */
    PieChart mChart;
    private PieData pieData;
    private PieDataSet pieDataSet;
    LinearLayout loadingHolder;
    LinearLayout emptyHolder;

    /* Pie Chart Statistik Kinerja*/
    PieChart statChart;
    private PieData pieDataStats;
    private PieDataSet pieDataSetStats;
    LinearLayout loadingHolderStats;
    LinearLayout emptyHolderStats;

    /* Bar Char Kinerja Bulanan*/
    BarChart monthlyChart;
    private BarData barDataMonthly;
    private BarDataSet barDataSetMontly;
    LinearLayout loadingHolderMonthly;
    LinearLayout emptyHolderMonthly;

    /* Bar Chart Jumlah Kasus Diterima dan Selesai*/
    BarChart caseChart;

    /* Intent */
    String sName = "";
    String sNrpRank = "";
    String sPosisi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_statistic);

        // get PK
        Bundle extras = getIntent().getExtras();
        int pk = Integer.parseInt(String.valueOf(extras.getInt("pk")));

        RequestParams params = new RequestParams();
        params.put("personnel_id", pk);

        /*GET DATA PIE CHART KINERJA KESELURUHAN*/
        loadingHolder = (LinearLayout) findViewById(R.id.loadingHolder);
        emptyHolder = (LinearLayout) findViewById(R.id.emptySetsHolder);
        mChart = (PieChart)findViewById(R.id.piechart);
        getPieChartData(pk, params);

        /*GET DATA PIE CHART STATISTIK KINERJA*/
        loadingHolderStats = (LinearLayout) findViewById(R.id.loadingHolderStats);
        emptyHolderStats = (LinearLayout) findViewById(R.id.emptySetsHolderStats);
        statChart = (PieChart) findViewById(R.id.piechartStats);
        getPieChartStatData(pk, params);

        /*GET DATA BAR CHART KINERJA BULANAN*/
        loadingHolderMonthly = (LinearLayout) findViewById(R.id.loadingHolderBarMonthly);
        emptyHolderMonthly = (LinearLayout) findViewById(R.id.emptySetsHolderBarMonthly);
        monthlyChart = (BarChart) findViewById(R.id.barchartMonthly);
        getBarChartMonthlyData(pk, params);

        /*APPEND DATA FROM INTENT*/
        getIntentData();

    }

    public void getPieChartData(int pk, RequestParams params) {
        // loading container
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
                    }

                    // insert data into pie chart
                    pieChart(yPieChart, xPieChart);

                    // data is empty
                    if (total == 0) {
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

        // color customize
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

    public void getPieChartStatData(int pk, RequestParams params) {
        // loading container
        loadingHolderStats.setVisibility(View.VISIBLE);
        emptyHolderStats.setVisibility(View.GONE);
        statChart.setVisibility(View.GONE);

        RestClient.get("stat/personnel/"+pk+"/case/category/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String sStats = new String(responseBody);
                loadingHolderStats.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(sStats);

                    ArrayList<Entry> yPieChartStats = new ArrayList<Entry>();
                    ArrayList<String> xPieChartStats = new ArrayList<String>();

                    JSONArray dataPieChartStats = obj.getJSONArray("data");
                    for (int i = 0; i < dataPieChartStats.length() ; i++) {
                        JSONObject dataStats = dataPieChartStats.getJSONObject(i);
                        String namaStats = (String) dataStats.get("name");
                        int yDataStats = (Integer) dataStats.get("y");

                        yPieChartStats.add(new Entry((float) yDataStats, i));
                        xPieChartStats.add(namaStats);
                    }

                    getPieChartStats(yPieChartStats, xPieChartStats);

                    if (dataPieChartStats.length() == 0) {
                        loadingHolderStats.setVisibility(View.GONE);
                        emptyHolderStats.setVisibility(View.VISIBLE);
                        statChart.setVisibility(View.GONE);                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getPieChartStats(ArrayList<Entry> yPieChartStats, ArrayList<String> xPieChartStats) {
        pieDataSetStats = new PieDataSet(yPieChartStats,"");
        pieDataStats = new PieData(xPieChartStats, pieDataSetStats);

        statChart.setVisibility(View.VISIBLE);

        // chart configuration
        statChart.setUsePercentValues(true);
        statChart.setData(pieDataStats);
        statChart.setRotationAngle(0);
        statChart.setRotationEnabled(true);

        statChart.setDescription("");
        statChart.setDrawHoleEnabled(true);
        statChart.setTransparentCircleRadius(30f);
        statChart.setHoleRadius(30f);

        pieDataStats.setValueFormatter(new PercentFormatter());
        pieDataStats.setValueTextSize(13f);
        pieDataStats.setValueTextColor(Color.DKGRAY);
        statChart.animateXY(1400, 1400);

        // color customize
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        pieDataSetStats.setColors(colors);

        // Legends to show on bottom of the graph
        Legend l = statChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setWordWrapEnabled(true);
    }

    public void getBarChartMonthlyData(int pk, RequestParams params) {
        RestClient.get("stat/personnel/" + pk + "/monthly/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String dataMonthly = new String(responseBody);

                try {
                    JSONObject obj = new JSONObject(dataMonthly);
                    Integer has_data = (Integer) obj.get("has_data");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
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
