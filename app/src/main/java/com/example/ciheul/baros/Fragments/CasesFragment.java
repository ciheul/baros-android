package com.example.ciheul.baros.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ciheul.baros.Adapters.CasesAdapter;
import com.example.ciheul.baros.EndlessScrollListener;
import com.example.ciheul.baros.RestClient;

import com.example.ciheul.baros.AddNewCase;
import com.example.ciheul.baros.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 13/02/17.
 * A placeholder fragment containing a simple view.
 */
public class CasesFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private EndlessScrollListener scrollListener;
    TextView caseCounter;


    // initiate the fragment
    public CasesFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CasesFragment newInstance(int sectionNumber, String texta) {
        CasesFragment fragment = new CasesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CONTENT_TEXT, texta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format_str, getArguments().getString(ARG_CONTENT_TEXT)));
        // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        textView.setMovementMethod(new ScrollingMovementMethod());*/

        Button button = (Button) rootView.findViewById(R.id.new_case_btn);
        button.setOnClickListener(this);


        /**/

        caseCounter = (TextView) rootView.findViewById(R.id.caseCounter);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        firstDataLoader();

//        recyclerView.setAdapter(adapter);

        // TODO jangan lupa di tambahin elemen progress dialognya (kalau bisa bentuknya spt spin.js

        scrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                //dummy test
                /*if (page == 3) {
                 loadNextDataFromApi(79, view);return;
                 }
                System.out.println(recyclerView.getScrollState()+"cimol");*/
                loadNextDataFromApi(page, view);

                //System.out.println("lasted::"+this.getLastVisibleItem(recyclerView.getDrawableState()));
            }

        };
        recyclerView.addOnScrollListener(scrollListener);

        /**/

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_case_btn:
                Intent intent = new Intent(v.getContext(), AddNewCase.class);
                startActivity(intent);
                break;

        }
    }

    /************/
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_demo, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Append the next page of data into the adapter
     * This method probably sends out a network request and appends new data items to your adapter.
     * Send an API request to retrieve appropriate paginated data
     * --> Send the request including an offset value (i.e `page`) as a query parameter.
     * --> Deserialize and construct new model objects from the API response
     * --> Append the new data objects to the existing set of items inside the array of items
     * --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
     **/
    public void loadNextDataFromApi(int offset, final RecyclerView rView) {
        System.out.println("loadNextDataFromAPI, "+offset);

        ViewGroup.LayoutParams para = recyclerView.getLayoutParams();
        if (getDeviceResoluition().heightPixels > getDeviceResoluition().widthPixels) {
            // 0.30 is good 0.45 is better
            para.height = (int) (getDeviceResoluition().heightPixels - (getDeviceResoluition().heightPixels *0.35));
        } else {
            para.height = (int) (getDeviceResoluition().heightPixels - (getDeviceResoluition().heightPixels *0.65));
        }
        recyclerView.setLayoutParams(para);
        System.out.println("hahau"+para.height+"|"+getDeviceResoluition().heightPixels+"|"+getDeviceResoluition().widthPixels);

        RequestParams params = new RequestParams();
        params.put("p",offset+1);

        RestClient.get("case/list/", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("yay!");
                String s = new String(responseBody);
                System.out.println(s);

                try {
                    // JSON Object data sets to append and notify
                    JSONObject obj = new JSONObject(s);
                    System.out.println(obj);

                    merge(obj);

                    /*adapter = new CasesAdapter(obj);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollToPosition(scrollTo);
                    adapter.notifyDataSetChanged();*/
                    //scrollListener.resetState();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = -2;
                recyclerView.setLayoutParams(params);

                System.out.println("ErrorCode:" + statusCode);

                if(statusCode == 404) {
                    /*Toast.makeText(getActivity(),
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();*/
                } else if(statusCode == 500) {
                    /*// When Http response code is '500'
                    Toast.makeText(getActivity(),"Error 500.",Toast.LENGTH_LONG).show();*/
                } else{
                    // When Http response code other than 404, 500
                    /*Toast.makeText(getActivity(),
                            "Unexpected Error occcured! [Most common Error: Device might not be " +
                                    "connected to Internet or remote server is not up and running]",
                            Toast.LENGTH_LONG).show();*/
                }
            }
        });
    }

    /**
     * load all data
     * in future development must use sql lite and sync data to server
     * */
    private void firstDataLoader() {
        RequestParams params = new RequestParams();
        params.put("p",1);

        RestClient.get("case/list/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);

                try {
                    // store data collection
                    JSONObject obj = new JSONObject(s);
                    adapter = new CasesAdapter(obj);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    caseCollection = obj;
                    caseCounter.setText(""+obj.get("total_cases")+" Kasus ditampilkan");
                    //merge(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 404) {
                    /*Toast.makeText(getActivity(),
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();*/
                } else if(statusCode == 500) {
                    // When Http response code is '500'
                    /*Toast.makeText(getActivity(),"Error 500.",Toast.LENGTH_LONG).show();*/
                } else{
                    /*// When Http response code other than 404, 500
                    Toast.makeText(getActivity(),
                            "Unexpected Error occcured! [Most common Error: Device might not be " +
                                    "connected to Internet or remote server is not up and running]",
                            Toast.LENGTH_LONG).show();*/
                }
            }
        });
    }

    JSONObject caseCollection = new JSONObject();
    private JSONObject getCaseCollection() {
        return caseCollection;
    }

    /**
     * appending source data to destination(caseCollection)*/
    private JSONObject merge(JSONObject... jsonObjects) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        int sizeOfCols = 0;

        try {
            sizeOfCols = caseCollection.getJSONArray("cases").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(JSONObject temp : jsonObjects){
            Iterator<String> keys = temp.keys();
            while(keys.hasNext()){
                String key = keys.next();
                jsonObject.put(key, temp.get(key));

                // get cases only
                if (key.equals("cases") && temp.get("cases") instanceof JSONArray) {
                    int x = 0;
                    for (int i = sizeOfCols; x < ((JSONArray) temp.get("cases")).length(); i++) {
                        JSONObject obs = (JSONObject) ((JSONArray) temp.get("cases")).get(x);
                        caseCollection.getJSONArray("cases").put(i,obs);
                        x++;
                    }
                }

                // get total_case only | switch the newest
                if (key.equals("total_cases") && temp.get("total_cases") instanceof Integer) {
                    caseCollection.put("total_cases",temp.get("total_cases"));
                    caseCounter.setText(""+temp.get("total_cases")+" Kasus ditampilkan");
                }

                int scrollTo = adapter.getItemCount() - 13;
                if (scrollTo < 0) scrollTo = 0;
                adapter = new CasesAdapter(caseCollection);
                recyclerView.setAdapter(adapter);

                //append new view
                if (key.equals("success")) {
                    if (-1 == (Integer) temp.get("success")) {
                        System.out.println("sucss-1"+temp.get("success").toString());

                        Snackbar.make(getView(), "Tidak ada kasus, kembali ke atas",
                                Snackbar.LENGTH_LONG)
//                                .setActionTextColor(Integer.parseInt("#FFFFFF"))
                                .setAction(R.string.snack_bar_action, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        recyclerView.smoothScrollToPosition(0);
                                        adapter.notifyDataSetChanged();
                                    }
                                }).show();
                    }
                }

                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = -2;
                recyclerView.setLayoutParams(params);
                System.out.println(params.height+"haha");

                recyclerView.smoothScrollToPosition(scrollTo);
                recyclerView.scrollToPosition(scrollTo);
                adapter.notifyDataSetChanged();

            }
        }

        return jsonObject;
    }


    /**
     * Getting current device resolution
     * */
    private DisplayMetrics getDeviceResoluition() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println("resolution hg:"+height+"-wd:"+width);

        return displayMetrics;
    }


}
