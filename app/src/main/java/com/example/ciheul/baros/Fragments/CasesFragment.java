package com.example.ciheul.baros.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ciheul.baros.AddNewCase;
import com.example.ciheul.baros.R;

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

    public CasesFragment() {
    }

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

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setMovementMethod(new ScrollingMovementMethod());

        textView.setText(getString(R.string.section_format_str, getArguments().getString(ARG_CONTENT_TEXT)));
        // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        Button button = (Button) rootView.findViewById(R.id.new_case_btn);
        button.setOnClickListener(this);

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
}