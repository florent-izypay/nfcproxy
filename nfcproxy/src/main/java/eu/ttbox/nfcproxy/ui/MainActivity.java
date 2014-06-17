package eu.ttbox.nfcproxy.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import eu.ttbox.nfcproxy.R;
import eu.ttbox.nfcproxy.ui.proxy.NfcProxyCardFragment;
import eu.ttbox.nfcproxy.ui.proxy.NfcProxyFragment;
import eu.ttbox.nfcproxy.ui.proxy.NfcProxyReaderFragment;
import eu.ttbox.nfcproxy.ui.readernfc.NfcReaderFragment;
import eu.ttbox.nfcproxy.ui.connect.bluetooth.BluetoothScanFragment;
import eu.ttbox.nfcproxy.ui.nav.NavigationDrawerFragment;
import eu.ttbox.nfcproxy.ui.prefs.SettingsActivity;
import eu.ttbox.nfcproxy.ui.readernfc.NfcReplayFragment;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "MainActivity";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }


    // ===========================================================
    // Navigation
    // ===========================================================

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // Select matching Fragment
        Log.d(TAG, "onNavigationDrawerItemSelected : position = " + position);
        Fragment toReplaceFragment = null;
        switch (position) {
            case 0:
                toReplaceFragment =  NfcReaderFragment.newInstance(position + 1);
                break;
            case 1:
                toReplaceFragment =   NfcReplayFragment.newInstance(position + 1);
                break;
            case 2:
                toReplaceFragment= NfcProxyFragment.newInstance(position + 1);
                break;
            case 3:
                toReplaceFragment= NfcProxyCardFragment.newInstance(position + 1);
                break;
            default:
                toReplaceFragment= PlaceholderFragment.newInstance(position + 1);
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, toReplaceFragment);
        fragmentTransaction.commit();
    }

    public void onSectionAttached(int number) {
        Log.d(TAG, "onSectionAttached : number = " + number);
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    // ===========================================================
    // Menu
    // ===========================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ===========================================================
    // Mock Fragment
    // ===========================================================

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    // ===========================================================
    // Other
    // ===========================================================


}
