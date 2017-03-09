package com.example.ciheul.baros.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ciheul.baros.Adapters.CasesAdapter;
import com.example.ciheul.baros.Adapters.PersonnelsAdapter;
import com.example.ciheul.baros.EndlessScrollListener;
import com.example.ciheul.baros.R;
import com.example.ciheul.baros.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ciheul on 14/02/17.
 */
public class PersonnelFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    LinearLayout loadingHolder;
    LinearLayout emptyHolder;

    public PersonnelFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PersonnelFragment newInstance(int sectionNumber, String texta) {
        PersonnelFragment fragment = new PersonnelFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CONTENT_TEXT, texta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_personnel, container, false);

        loadingHolder = (LinearLayout) rootView.findViewById(R.id.loadingHolder);
        emptyHolder = (LinearLayout) rootView.findViewById(R.id.emptySetsHolder);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_personnel);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        firstDataLoader();

        return rootView;
    }

    private void firstDataLoader() {
        RestClient.get("group/", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);

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
}