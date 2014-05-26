package com.cubbyhole.android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.cubbyhole.android.CubbyholeAndroidClientApp;
import com.cubbyhole.android.R;
import com.cubbyhole.android.fragment.FileListFragment;
import com.cubbyhole.android.fragment.FileListFragmentListener;
import com.cubbyhole.android.fragment.HomeFragment;
import com.cubbyhole.android.fragment.NavigationDrawerFragment;
import com.cubbyhole.android.parcelable.ParcelableFile;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((CubbyholeAndroidClientApp)getApplication()).getObjectGraph().inject(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private FileListFragmentListener mainFileListFragmentListener = new FileListFragmentListener() {
        @Override
        public boolean onOpen(ParcelableFile file) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("file", file);
            FileListFragment fileListFragment = new FileListFragment(mainFileListFragmentListener);
            fileListFragment.setArguments(bundle);
            currentFragment = fileListFragment;
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,fileListFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
    };

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                currentFragment = new HomeFragment();
                break;
            case 1:
                currentFragment = new FileListFragment(mainFileListFragmentListener);
                break;
            case 2:
                currentFragment = new Fragment();
                break;
        }
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.container, currentFragment)
            .commit();
    }

    public void onSectionAttached(int number) {
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
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


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
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
