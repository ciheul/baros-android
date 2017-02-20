package com.example.ciheul.baros.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ciheul.baros.R;

/**
 * Created by ciheul on 14/02/17.
 */

public class MoreFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

    public MoreFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MoreFragment newInstance(int sectionNumber, String texta) {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CONTENT_TEXT, texta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_more, container, false);
        return rootView;
    }
}