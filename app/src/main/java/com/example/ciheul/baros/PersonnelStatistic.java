package com.example.ciheul.baros;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
    LinearLayout loadingHolderCase;
    LinearLayout emptyHolderCase;

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

        /* GET DATA BAR CHART JUMLAH KASUS DITERIMA DAN SELESAI */
        loadingHolderCase = (LinearLayout) findViewById(R.id.loadingHolderBarCase);
        emptyHolderCase = (LinearLayout) findViewById(R.id.emptySetsHolderBarCase);
        caseChart = (BarChart) findViewById(R.id.barchartCase);
        getBarChartCaseData(pk, params);

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

                    // data is empty
                    if (total == 0) {
                        loadingHolder.setVisibility(View.GONE);
                        emptyHolder.setVisibility(View.VISIBLE);
                        mChart.setVisibility(View.GONE);
                        return;
                    }

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
//        mChart.setUsePercentValues(true);
        mChart.setData(pieData);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        mChart.setDescription("");
        mChart.setDrawHoleEnabled(true);
        mChart.setTransparentCircleRadius(30f);
        mChart.setHoleRadius(30f);

        pieData.setValueFormatter((ValueFormatter) new MyValueFormatter());
//        pieData.setValueFormatter(new PercentFormatter());
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

                    // data is empty
                    if (dataPieChartStats.length() == 0) {
                        loadingHolderStats.setVisibility(View.GONE);
                        emptyHolderStats.setVisibility(View.VISIBLE);
                        statChart.setVisibility(View.GONE);
                        return;
                    }

                    // insert data into chart
                    getPieChartStats(yPieChartStats, xPieChartStats);


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
//        statChart.setUsePercentValues(true);
        statChart.setData(pieDataStats);
        statChart.setRotationAngle(0);
        statChart.setRotationEnabled(true);

        statChart.setDescription("");
        statChart.setDrawHoleEnabled(true);
        statChart.setTransparentCircleRadius(30f);
        statChart.setHoleRadius(30f);

        pieDataStats.setValueFormatter((ValueFormatter) new MyValueFormatter());
//        pieDataStats.setValueFormatter(new PercentFormatter());
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
        // loading container
        loadingHolderMonthly.setVisibility(View.VISIBLE);
        emptyHolderMonthly.setVisibility(View.GONE);
        monthlyChart.setVisibility(View.GONE);
        RestClient.get("stat/personnel/" + pk + "/monthly/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String dataMonthly = new String(responseBody);
                loadingHolderMonthly.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(dataMonthly);
                    Integer has_data = (Integer) obj.get("has_data");

                    if (has_data == 0) {
                        loadingHolderMonthly.setVisibility(View.GONE);
                        emptyHolderMonthly.setVisibility(View.VISIBLE);
                        monthlyChart.setVisibility(View.GONE);
                        return;
                    }

                    monthlyChart.setVisibility(View.VISIBLE);

                    JSONObject dataArr = (JSONObject) obj.getJSONObject("data");
                    JSONArray categories = dataArr.getJSONArray("categories");
                    JSONArray series = dataArr.getJSONArray("series");

                    ArrayList<BarEntry> yValue0 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue1 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue2 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue3 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue4 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue5 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue6 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue7 = new ArrayList<BarEntry>();

                    ArrayList<String> labels = new ArrayList<String>();
                    JSONObject dataSeries, dataCategories = null;

                    for (int i = 0; i <series.length(); i++) {
                        dataSeries = (JSONObject) series.getJSONObject(i);

                        String namaSeries = dataSeries.getString("name");
;
                        switch (i) {
                            case 0:
                                JSONArray datas0 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas0.length(); j++) {
                                    Integer yData0 = datas0.getInt(j);
                                    yValue0.add(new BarEntry(yData0, j));
                                }
                                break;

                            case 1:
                                JSONArray datas1 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas1.length(); j++) {
                                    Integer yData1 = datas1.getInt(j);
                                    yValue1.add(new BarEntry(yData1, j));
                                }
                                break;

                            case 2:
                                JSONArray datas2 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas2.length() ; j++) {
                                    Integer yData2 = datas2.getInt(j);
                                    yValue2.add(new BarEntry(yData2, j));
                                }
                                break;

                            case 3:
                                JSONArray datas3 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas3.length() ; j++) {
                                    Integer yData3 = datas3.getInt(j);
                                    yValue3.add(new BarEntry(yData3, j));
                                }
                                break;

                            case 4:
                                JSONArray datas4 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas4.length() ; j++) {
                                    Integer yData4 = datas4.getInt(j);
                                    yValue4.add(new BarEntry(yData4, j));
                                }
                                break;

                            case 5:
                                JSONArray datas5 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas5.length() ; j++) {
                                    Integer yData5 = datas5.getInt(j);
                                    yValue5.add(new BarEntry(yData5, j));
                                }
                                break;

                            case 6:
                                JSONArray datas6 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas6.length() ; j++) {
                                    Integer yData6 = datas6.getInt(j);
                                    yValue6.add(new BarEntry(yData6, j));
                                }
                                break;

                            case 7:
                                JSONArray datas7 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas7.length() ; j++) {
                                    Integer yData7 = datas7.getInt(j);
                                    yValue7.add(new BarEntry(yData7, j));
                                }
                                break;
                        }
                    }

                    for (int i = 0; i < categories.length(); i++) {
                        String cat = (String) categories.get(i);
                        labels.add(cat);
                    }

                    BarDataSet set1, set2, set3, set4, set5, set6, set7, set8;

                    // create 2 datasets with different types
                    set1 = new BarDataSet(yValue0, "Klarifikasi");
                    set2 = new BarDataSet(yValue1, "Gelar Perkara");
                    set3 = new BarDataSet(yValue2, "Pemanggilan");
                    set4 = new BarDataSet(yValue3, "SP2HP");
                    set5 = new BarDataSet(yValue4, "SP2HP A2");
                    set6 = new BarDataSet(yValue5, "Pelimpahan");
                    set7 = new BarDataSet(yValue6, "P21");
                    set8 = new BarDataSet(yValue7, "SP3");

                    set1.setColor(Color.rgb(75, 188, 244));
                    set2.setColor(Color.rgb(108, 191, 132));
                    set3.setColor(Color.rgb(63, 66, 52));
                    set4.setColor(Color.rgb(179, 124, 87));
                    set5.setColor(Color.rgb(97, 58, 67));
                    set6.setColor(Color.rgb(132, 153, 116));
                    set7.setColor(Color.rgb(164, 228, 255));
                    set8.setColor(Color.rgb(197, 145, 157));

                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(set1);
                    dataSets.add(set2);
                    dataSets.add(set3);
                    dataSets.add(set4);
                    dataSets.add(set5);
                    dataSets.add(set6);
                    dataSets.add(set7);
                    dataSets.add(set8);

                    BarData data = new BarData(labels,dataSets);
                    data.setValueFormatter((ValueFormatter) new MyValueFormatter());
                    monthlyChart.setData(data);

                    // Legends to show on bottom of the graph
                    Legend l = monthlyChart.getLegend();
                    l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                    l.setXEntrySpace(7f);
                    l.setYEntrySpace(0f);
                    l.setYOffset(0f);
                    l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
                    l.setWordWrapEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getBarChartCaseData(int pk, RequestParams params) {
        // loading container
        loadingHolderCase.setVisibility(View.VISIBLE);
        emptyHolderCase.setVisibility(View.GONE);
        caseChart.setVisibility(View.GONE);
        RestClient.get("stat/personnel/"+pk+"/case/list/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String caseData = new String (responseBody);

                loadingHolderCase.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(caseData);
                    Integer has_data = (Integer) obj.get("has_data");

                    if (has_data == 0) {
                        loadingHolderCase.setVisibility(View.GONE);
                        emptyHolderCase.setVisibility(View.VISIBLE);
                        caseChart.setVisibility(View.GONE);
                        return;
                    }

                    caseChart.setVisibility(View.VISIBLE);

                    JSONObject dataArr = (JSONObject) obj.getJSONObject("data");
                    JSONArray categories = dataArr.getJSONArray("categories");
                    JSONArray series = dataArr.getJSONArray("series");

                    ArrayList<BarEntry> yValue1 = new ArrayList<BarEntry>();
                    ArrayList<BarEntry> yValue2 = new ArrayList<BarEntry>();
                    ArrayList<String> labels = new ArrayList<String>();

                    JSONObject dataSeries, dataCategories = null;

                    for (int i = 0; i <series.length(); i++) {
                        dataSeries = (JSONObject) series.getJSONObject(i);

                        String namaSeries = dataSeries.getString("name");

                        switch (i) {
                            case 0:
                                JSONArray datas1 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas1.length(); j++) {
                                    Integer yData1 = datas1.getInt(j);
                                    yValue1.add(new BarEntry(yData1, j));
                                }
                                break;

                            case 1:
                                JSONArray datas2 = dataSeries.getJSONArray("data");
                                for (int j = 0; j < datas2.length() ; j++) {
                                    Integer yData2 = datas2.getInt(j);
                                    yValue2.add(new BarEntry(yData2, j));
                                }
                                break;
                        }
                    }

                    for (int i = 0; i < categories.length(); i++) {
                        String cat = (String) categories.get(i);
                        labels.add(cat);
                    }

                    BarDataSet set1, set2;

                    // create 2 datasets with different types
                    set1 = new BarDataSet(yValue1, "Kasus Diterima");
                    set1.setColor(Color.rgb(75, 188, 244));
                    set2 = new BarDataSet(yValue2, "Kasus Selesai");
                    set2.setColor(Color.rgb(108, 191, 132));

                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(set1);
                    dataSets.add(set2);

                    BarData data = new BarData(labels,dataSets);
                    data.setValueFormatter((ValueFormatter) new MyValueFormatter());

                    caseChart.setData(data);
                    caseChart.invalidate();

                    // chart customize
                    XAxis xAxis = caseChart.getXAxis();

                    // Legends to show on bottom of the graph
                    Legend l = caseChart.getLegend();
                    l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                    l.setXEntrySpace(7f);
                    l.setYEntrySpace(0f);
                    l.setYOffset(0f);
                    l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
                    l.setWordWrapEnabled(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value > 0) {
                return mFormat.format(value);
            } else {
                return "";
            }
        }

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
