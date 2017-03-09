package com.example.ciheul.baros;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.text.method.ScrollingMovementMethod;


import android.widget.Button;
import android.widget.TextView;

import com.example.ciheul.baros.Fragments.CasesFragment;
import com.example.ciheul.baros.Fragments.LeaderboardFragment;
import com.example.ciheul.baros.Fragments.MoreFragment;
import com.example.ciheul.baros.Fragments.PersonnelFragment;
import com.example.ciheul.baros.Fragments.SearchFragment;
import com.loopj.android.http.RequestParams;

import java.util.List;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private RequestParams rParameters = new RequestParams();

    /**
     * TODO saat onCreate jangan lupa untuk menambahkan logic untuk melihat session authenticate user
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // check filter data
        Bundle b = getIntent().getExtras();
        if (b != null) {
            for (String key: b.keySet()) {
                System.out.println("Bundle"+key+""+b.get(key));
                rParameters.put(key,b.get(key));
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);
        setupTablayout();

//        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // jump to tab page more
                mViewPager.setCurrentItem(4);

            }
        });

        /*if (savedInstanceState == null) {
            CasesFragment fragmentDemo = (CasesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_platter);
        }*/

        /*// Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_platter, new CasesFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_CONTENT_TEXT = "TEXT CONTENT";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String texta) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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
            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * TODO tambahkan logic privilege user (superuser, personnel, people, guest
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // example Return a PlaceholderFragment (defined as a static inner class below).
            String content = "No Fragments or no menu";
            System.out.println("ini position "+ position);

            switch (position) {
                case 0:{
                    content = "Daftar Kasus";
                    return CasesFragment.newInstance(position, content, rParameters);
                }
                case 1:{
                    content = "Leaderboard";
                    return LeaderboardFragment.newInstance(position, content);
                }

                case 2:{
                    content = "Anggota";
                    return PersonnelFragment.newInstance(position, content, rParameters);
                }
                case 3:{
                    content = "Search";
                    return SearchFragment.newInstance(position, content);
                }
                case 4:{
                    // content = "Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: Population of the tabs to display is done through TabLayout.Tab instances. You create tabs via newTab(). From there you can change the tab's label or icon via setText(int) and setIcon(int) respectively. To display the tab, you need to add it to the layout via one of the addTab(Tab) methods. For example: ";
                    content = "More";
                    return MoreFragment.newInstance(position, content);
                }

            }

            return PlaceholderFragment.newInstance(position, content);

            // should return fragment
            //return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        /*
         * adding new fragment
         * */
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /*@Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }*/
    }

    private void setupTablayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSmoothScrollingEnabled(true);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_action_name);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_leaderboard);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_action_anggota);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_action_search);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_action_more);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Fragment(), "Kasus");
        adapter.addFrag(new Fragment(), "Leaderboard");
        adapter.addFrag(new Fragment(), "Anggota");
        adapter.addFrag(new Fragment(), "Cari");
        adapter.addFrag(new Fragment(), "More");
        viewPager.setAdapter(adapter);
    }

/*    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registration, container, false);
        Button button = (Button) view.findViewById(R.id.btnLinkToRegister);
        button.setOnClickListener(new onClickListener() {
           @Override
            public void onClick(View v) {

           }
        });
        return view;
    }*/
}
