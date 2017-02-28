package com.example.ciheul.baros.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ciheul.baros.ArchiveList;
import com.example.ciheul.baros.FilterCase;
import com.example.ciheul.baros.R;

/**
 * Created by ciheul on 14/02/17.
 */

public class MoreFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

    CardView archiveSegment;
    CardView caseHistorySegment;
    CardView chatHistorySegment;
    CardView changeProfileSegment;
    CardView changePasswordSegment;
    CardView logoutSegment;
    CardView releaseInfoSegment;

    public MoreFragment() {}

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
        initElement(rootView);

        archiveSegment.setOnClickListener(this);

        return rootView;
    }

    /*PRIVATE FUNCTION*/
    private void initElement(View rootView) {
        archiveSegment = (CardView) rootView.findViewById(R.id.archiveSegment);
        caseHistorySegment = (CardView) rootView.findViewById(R.id.caseHistorySegment);
        chatHistorySegment = (CardView) rootView.findViewById(R.id.chatHistorySegment);
        changeProfileSegment = (CardView) rootView.findViewById(R.id.changeProfileSegment);
        changePasswordSegment = (CardView) rootView.findViewById(R.id.changePasswordSegment);
        logoutSegment = (CardView) rootView.findViewById(R.id.logoutSegment);
        releaseInfoSegment = (CardView) rootView.findViewById(R.id.releaseInfoSegment);
    }

    @Override
    public void onClick(View v) {
        System.out.println("v.GetId"+v.getId());
        switch (v.getId()) {
            case R.id.archiveSegment:
                Intent intentFilter = new Intent(v.getContext(), ArchiveList.class);
                startActivity(intentFilter);
                break;
        }
    }
}