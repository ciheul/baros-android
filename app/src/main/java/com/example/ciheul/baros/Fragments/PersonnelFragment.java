package com.example.ciheul.baros.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ciheul.baros.Adapters.CasesAdapter;
import com.example.ciheul.baros.Adapters.PersonnelsAdapter;
import com.example.ciheul.baros.AddNewPersonnel;
import com.example.ciheul.baros.EndlessScrollListener;
import com.example.ciheul.baros.R;
import com.example.ciheul.baros.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 14/02/17.
 */
public class PersonnelFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    LinearLayout loadingHolder;
    LinearLayout emptyHolder;
    private EndlessScrollListener scrollListener;

    private static RequestParams rParameters = new RequestParams();

    public PersonnelFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PersonnelFragment newInstance(int sectionNumber, String texta, RequestParams params) {
        PersonnelFragment fragment = new PersonnelFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CONTENT_TEXT, texta);
        fragment.setArguments(args);
        rParameters = params;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_personnel, container, false);

        /* ADD PERSONNEL */
        Button addPersonnel = (Button) rootView.findViewById(R.id.addingPersonnelBtn);
        addPersonnel.setOnClickListener(this);

        loadingHolder = (LinearLayout) rootView.findViewById(R.id.loadingHolder);
        emptyHolder = (LinearLayout) rootView.findViewById(R.id.emptySetsHolder);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_personnel);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        firstDataLoader();

        scrollListener = new EndlessScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                RequestParams filterParams = new RequestParams();
                if (rParameters != null) {
                    filterParams = rParameters;
                }

                loadNextDataFromApi(page, view, filterParams);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        return rootView;
    }


    private DisplayMetrics getDeviceResoluition() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        return displayMetrics;
    }


    public void loadNextDataFromApi(int offset, final RecyclerView rView, RequestParams params) {
        System.out.println("loadNextDataFromAPI, "+offset);

        ViewGroup.LayoutParams para = recyclerView.getLayoutParams();
        if (getDeviceResoluition().heightPixels > getDeviceResoluition().widthPixels) {
            // 0.30 is good 0.45 is better
            para.height = (int) (getDeviceResoluition().heightPixels - (getDeviceResoluition().heightPixels *0.35));
        } else {
            para.height = (int) (getDeviceResoluition().heightPixels - (getDeviceResoluition().heightPixels *0.65));
        }
        recyclerView.setLayoutParams(para);

        params.put("p",offset+1);
//        loadingHolder.setVisibility(View.VISIBLE);
//        emptyHolder.setVisibility(View.GONE);

        RestClient.get("group/", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                System.out.println(s);
//                loadingHolder.setVisibility(View.GONE);

                try {
                    // JSON Object data sets to append and notify
                    JSONObject obj = new JSONObject(s);
                    merge(obj);
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
//                loadingHolder.setVisibility(View.GONE);
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


    private JSONObject merge(JSONObject... jsonObjects) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        int sizeOfCols = 0;

        try {
            sizeOfCols = personnelCollection.getJSONArray("data").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(JSONObject temp : jsonObjects){
            Iterator<String> keys = temp.keys();
            while(keys.hasNext()){
                String key = keys.next();
                jsonObject.put(key, temp.get(key));

                // get cases only
                if (key.equals("data") && temp.get("data") instanceof JSONArray) {
                    int x = 0;
                    for (int i = sizeOfCols; x < ((JSONArray) temp.get("data")).length(); i++) {
                        JSONObject obs = (JSONObject) ((JSONArray) temp.get("data")).get(x);
                        personnelCollection.getJSONArray("data").put(i,obs);
                        x++;
                    }
                }

                // get total_case only | switch the newest

/*                if (temp.get("personnels").toString().equals("0")) {
//                    emptyHolder.setVisibility(View.VISIBLE);
                }*/

                int scrollTo = adapter.getItemCount() - 13;
                if (scrollTo < 0) scrollTo = 0;
                adapter = new PersonnelsAdapter(personnelCollection);
                recyclerView.setAdapter(adapter);

                //append new view
                if (key.equals("success")) {
                    if (-1 == (Integer) temp.get("success")) {
                        System.out.println("sucss-1"+temp.get("success").toString());

                        Snackbar.make(getView(), "Akhir dari data anggota, kembali ke atas",
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

                recyclerView.smoothScrollToPosition(scrollTo);
                recyclerView.scrollToPosition(scrollTo);
                adapter.notifyDataSetChanged();
            }
        }
        return jsonObject;
    }


    private void firstDataLoader() {
        RequestParams params = new RequestParams();
        if (rParameters != null) {
            params = rParameters;
        }

        params.put("p",1);
//        loadingHolder.setVisibility(View.VISIBLE);
        RestClient.get("group/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
//                loadingHolder.setVisibility(View.GONE);
                try {
                    // store data collection

                    JSONObject obj = new JSONObject(s);
                    System.out.println(obj);
                    adapter = new PersonnelsAdapter(obj);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    personnelCollection = obj;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    JSONObject personnelCollection = new JSONObject();
    private JSONObject getPersonnelCollection() { return personnelCollection; }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addingPersonnelBtn:
                Intent intent = new Intent(v.getContext(), AddNewPersonnel.class);
                startActivity(intent);
                break;
        }
    }
}