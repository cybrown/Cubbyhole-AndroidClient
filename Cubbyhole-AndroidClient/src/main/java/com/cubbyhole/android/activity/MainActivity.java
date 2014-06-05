package com.cubbyhole.android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.cubbyhole.client.http.BasicAuthInterceptor;
import com.cubbyhole.client.http.ConnectionInfo;
import com.cubbyhole.client.model.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import retrofit.RequestInterceptor;

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

    @Inject @Named("RootFile") java.io.File rootFile;
    @Inject DownloadManager downloadManager;
    @Inject ConnectionInfo connectionInfo;
    @Inject @Named("baseUrl") Provider<String> baseUrl;

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
        public boolean onOpenFolder(ParcelableFile file) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("file", file);
            FileListFragment fileListFragment = new FileListFragment(mainFileListFragmentListener);
            fileListFragment.setArguments(bundle);
            currentFragment = fileListFragment;
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fileListFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        @Override
        public boolean onOpenFile(ParcelableFile file) {
            Uri uri = Uri.parse(baseUrl.get() + "files/" + file.getId() + "/raw");
            final DownloadManager.Request request = new DownloadManager.Request(uri);
            new BasicAuthInterceptor(connectionInfo).intercept(new RequestInterceptor.RequestFacade() {
                @Override
                public void addHeader(String s, String s2) {
                    request.addRequestHeader(s, s2);
                }

                @Override
                public void addPathParam(String s, String s2) {

                }

                @Override
                public void addEncodedPathParam(String s, String s2) {

                }

                @Override
                public void addQueryParam(String s, String s2) {

                }

                @Override
                public void addEncodedQueryParam(String s, String s2) {

                }
            });
            request.setTitle(file.getName());
            request.setDescription("File download");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType(file.getMimetype());
            request.allowScanningByMediaScanner();
            java.io.File destination = new java.io.File(rootFile.getPath() + "/" + file.getName());
            request.setDestinationUri(Uri.parse("file://" + destination.getPath()));
            final java.io.File cubbyholeDir = new java.io.File(Environment.getExternalStorageDirectory().getPath() + "/cubbyhole");
            if (!cubbyholeDir.exists()) {
                cubbyholeDir.mkdirs();
            }
            downloadManager.enqueue(request);
            return true;
        }

        @Override
        public void onSelect(FileListFragment fileListFragment, File currentFile) {

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
