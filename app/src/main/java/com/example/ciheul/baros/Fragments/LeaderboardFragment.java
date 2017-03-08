package com.example.ciheul.baros.Fragments;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ciheul.baros.Adapters.LeaderboardAdapter;
import com.example.ciheul.baros.R;
import com.example.ciheul.baros.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 14/02/17.
 */

public class LeaderboardFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

    LinearLayout loadingHolder;
    LinearLayout emptyHolder;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private static ExpandableListView expandableListView;
    private static ExpandableListAdapter listAdapter;

    public LeaderboardFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LeaderboardFragment newInstance(int sectionNumber, String texta) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CONTENT_TEXT, texta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.headerLeaderboard);
        textView.setMovementMethod(new ScrollingMovementMethod());

//        textView.setText(getString(R.string.section_format_str, getArguments().getString(ARG_CONTENT_TEXT)));
        // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));


        loadingHolder = (LinearLayout) rootView.findViewById(R.id.loadingHolder);
        emptyHolder = (LinearLayout) rootView.findViewById(R.id.emptySetsHolder);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_leaderboard);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        firstLoader();

        return rootView;
    }

    private void firstLoader() {
        RestClient.get("leaderboard/", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                try {
                    JSONObject obj = new JSONObject(result);
                    adapter = new LeaderboardAdapter(obj);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    leaderboardCollection = obj;
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

    JSONObject leaderboardCollection = new JSONObject();
    private JSONObject getLeaderboardCollection() { return leaderboardCollection; }

}
