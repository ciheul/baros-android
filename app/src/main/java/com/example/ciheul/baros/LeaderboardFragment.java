package com.example.ciheul.baros;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ciheul on 14/02/17.
 */

public class LeaderboardFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "CASES FRAGMENT PLATTER";
    private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

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

        textView.setText(getString(R.string.section_format_str, getArguments().getString(ARG_CONTENT_TEXT)));
        // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
