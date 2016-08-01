package com.example.ronylevari.callrecorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ronylevari.callrecorder.adapter.AdapterListener;
import com.example.ronylevari.callrecorder.adapter.RecordingDataAdapter;
import com.example.ronylevari.callrecorder.adapter.SimpleTouchCallback;
import com.example.ronylevari.callrecorder.bl.DatabaseObject;
import com.example.ronylevari.callrecorder.bl.ParentRecordingItem;
import com.example.ronylevari.callrecorder.constants.AppConstants;
import com.example.ronylevari.callrecorder.database.DatabaseAdapter;
import com.example.ronylevari.callrecorder.receivers.FileCreatedReceiver;
import com.example.ronylevari.callrecorder.service.CallListenerService;
import com.example.ronylevari.callrecorder.service.CommaSeparatedValuesService;
import com.example.ronylevari.callrecorder.widgets.CallRecorderRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // constants
    private static final String TAG = "MainActivity";
    private static final String SAVE_NAV_MENU_OPTION = "save navigation menu item";
    private static final String SAVE_OPTION_MENU_ITEM = "save option menu item";
    private static final String SAVE_ACTION_MODE = "save action mode";
    private static final String SAVE_WATCHED_PARENT_ID = "watched parent";
    private static final String SAVE_SELECTION_MODE_ITEMS = "selected items";

    // UI components
    private MenuItem mActiveMenuItem;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private CallRecorderRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private View mEmptyView;
    private TextView mEmptyViewText;

    // data and data adapters components
    private DatabaseAdapter mDBAdapter;
    private RecordingDataAdapter mAdapter;
    private ArrayList<? extends DatabaseObject> mData = new ArrayList<>();

    // receivers
    FileCreatedReceiver mNewsFileReadyReceiver = new FileCreatedReceiver();

    // App state variables
    private boolean mIsRecording;

    // activity state variables
    private int mActiveNavMenuItemId;
    private int mOptionMenuTemId;
    private boolean mIsOnActionMode;
    private long mWatchedParentId;
    private ArrayList<ParentRecordingItem> mSelected = new ArrayList<>();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean mFileAccessPermission;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mFileAccessPermission = false;
                }else {
                    mFileAccessPermission = true;
                }
                    updateSharedRecordingFilePermission(mFileAccessPermission);
                return;
            }
        }
    }

    // Action mode, callbacks and listeners
    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.paren_recording_data_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
            switch (menuItem.getItemId()) {
                case R.id.menu_item_delete:
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        mAdapter.deleteItem(selectedItemPositions.get(i));
                    }
                    actionMode.finish();
                    return true;
                case R.id.menu_item_merge:
                    actionMode.finish();
                    return true;
                case R.id.menu_item_share:
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        ArrayList<ParentRecordingItem> toExport = extractParentsFromSelectedSparseArray();
                        Intent intent = new Intent(MainActivity.this, CommaSeparatedValuesService.class);
                        intent.setAction(CommaSeparatedValuesService.ACTION_WRITE_TO_FILE);
                        intent.putParcelableArrayListExtra(CommaSeparatedValuesService.EXTRA_PARENT, toExport);
                        intent.putExtra(CommaSeparatedValuesService.EXTRA_TRASHED, mActiveNavMenuItemId == R.id.nav_trash);
                        startService(intent);
                    }
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mIsOnActionMode = false;
            mAdapter.clearSelection();
            mActionMode = null;
        }
    };

    private AdapterListener mAdapterListener = new AdapterListener() {

        @Override
        public void onContextMenuState() {
            openActionMode();
        }

        @Override
        public void onDeselectingAllItems() {
            closeActionMode();
        }

        @Override
        public void onChildrenRequest(ParentRecordingItem parent, boolean trashedChildren) {
            setEmptyViewTitle("No more recordings here");
            mWatchedParentId = parent.getID();
            mAdapter.clearSelection();
            mData = mDBAdapter.getChildrenOf(mWatchedParentId, trashedChildren);
            mAdapter.update(mData);
            mNavigationView.getMenu().findItem(R.id.nav_recordings).setChecked(false);
            invalidateOptionsMenu();
        }
    };

    private void openActionMode() {
        mIsOnActionMode = true;
        if (mActionMode != null) {
            return;
        }
        mActionMode = startSupportActionMode(mActionModeCallback);
    }

    public void closeActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDBAdapter = new DatabaseAdapter(this);

        // set up UI toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // initialize fab according to application recording state
        mIsRecording = loadRecordingStateFromSharedPref();
        mFileAccessPermission = loadFilePermissionFromSharedPref();
        int fabImageResource = mIsRecording ? R.drawable.ic_stop_white_24dp : R.drawable.ic_menu_listen_white;
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setImageResource(fabImageResource);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRecording(view);
            }
        });

        // set up the hamburger icon to open and close the drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // listen for navigation events
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // initialise the empty view components
        mEmptyView = findViewById(R.id.empty_screen);
        mEmptyViewText = (TextView) findViewById(R.id.no_content_text);

        // load saved navigation state if present
        if (savedInstanceState != null) {
            mActiveNavMenuItemId = savedInstanceState.getInt(SAVE_NAV_MENU_OPTION);
            mIsOnActionMode = savedInstanceState.getBoolean(SAVE_ACTION_MODE);
            mWatchedParentId = savedInstanceState.getLong(SAVE_WATCHED_PARENT_ID);
            mOptionMenuTemId = savedInstanceState.getInt(SAVE_OPTION_MENU_ITEM);
            if (mIsOnActionMode) {
                mSelected = savedInstanceState.getParcelableArrayList(SAVE_SELECTION_MODE_ITEMS);
            }
        } else {
            mActiveNavMenuItemId = R.id.nav_recordings;
            mIsOnActionMode = false;
            mWatchedParentId = 0;
            mOptionMenuTemId = R.id.action_parent_sort_descending_date;
        }
        mNavigationView.getMenu().findItem(mActiveNavMenuItemId).setChecked(true);

        if (mWatchedParentId != 0) {
            // TODO this function
            setEmptyViewTitle("No more recordings here");
            //mData = loadParentChildren(mWatchedParentId);
        } else {
            mData = loadMainContent(mActiveNavMenuItemId);
        }

        // set up the recycler view
        mRecyclerView = (CallRecorderRecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordingDataAdapter(this, mData, mDBAdapter);
        mAdapter.setAdapterListener(mAdapterListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.hideIfEmpty(mRecyclerView);
        mRecyclerView.showIfEmpty(mEmptyView);

        // enable swipe touch
        SimpleTouchCallback touchCallback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mIsOnActionMode) {
            loadSelectedItems();
            openActionMode();
        }
        verifyStoragePermissions(MainActivity.this);
    }

    // TODO this function
    private void loadSelectedItems() {
        for(ParentRecordingItem p : mSelected) {
            int pos = mData.indexOf(p);
            if (mAdapter != null) {
                mAdapter.toggleSelection(pos);
            }
        }
    }

    private boolean loadRecordingStateFromSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getBoolean(AppConstants.SHARED_PREFS_IS_RECORDING_KEY, false);
    }

    private void updateSharedRecordingFilePermission(boolean isPermitted) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AppConstants.SHARED_PREFS_FILE_PERMISSION, isPermitted);
        editor.commit();
    }

    private boolean loadFilePermissionFromSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(AppConstants.SHARED_PREFS_FILE_PERMISSION, false);
    }

    private void toggleRecording(View view) {
        if (mIsRecording) {
            Intent intent = new Intent();
            intent.setAction(CallListenerService.STOP_SERVICE_ACTION);
            intent.putExtra(CallListenerService.STOP_SERVICE_BROADCAST_KEY,
                    CallListenerService.STOP_SERVICE_REQUEST);
            // Broadcast the given intent to all interested BroadcastReceivers
            sendBroadcast(intent);
            mFab.setImageResource(R.drawable.ic_menu_listen_white);
            Snackbar.make(view, "Recording has stopped", Snackbar.LENGTH_LONG).show();
        } else {
            //Intent intent = new Intent(this, CallListenerService.class);
            startService(new Intent(this, CallListenerService.class));
            //mRecordingService.startService(intent);
            mFab.setImageResource(R.drawable.ic_stop_white_24dp);
            Snackbar.make(view, "Recording has started", Snackbar.LENGTH_LONG).show();
        }
        mIsRecording = !mIsRecording;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNewsFileReadyReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsRecording = loadRecordingStateFromSharedPref();
        registerReceiver(mNewsFileReadyReceiver, new IntentFilter(FileCreatedReceiver.ACTION_EXPORT_TO_MAIL));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.unregisterAdapterListener(mAdapterListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_NAV_MENU_OPTION, mActiveNavMenuItemId);
        outState.putBoolean(SAVE_ACTION_MODE, mIsOnActionMode);
        outState.putLong(SAVE_WATCHED_PARENT_ID, mWatchedParentId);
        outState.putInt(SAVE_OPTION_MENU_ITEM, mOptionMenuTemId);
        ArrayList<ParentRecordingItem> toSave = extractParentsFromSelectedSparseArray();
        outState.putParcelableArrayList(SAVE_SELECTION_MODE_ITEMS, toSave);
        Log.d(TAG, "active navigation item saved");
    }

    private ArrayList<ParentRecordingItem> extractParentsFromSelectedSparseArray() {
        ArrayList<ParentRecordingItem> toSave = new ArrayList<>();
        if(mAdapter != null) {
            if (mIsOnActionMode) {
                List<Integer> sa = mAdapter.getSelectedItems();
                for (int i = 0; i < sa.size(); i++) {
                    toSave.add((ParentRecordingItem) mData.get(sa.get(i)));
                }
            }
        }
        return toSave;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (mWatchedParentId != 0) {
            // hide parent's items and expose children's
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);
            menu.getItem(5).setVisible(true);
        } else {
            // hide children's items and expose parent's
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO this sorting functions
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_parent_sort_ascending_date) {
            return true;
        } else if (id == R.id.action_parent_sort_descending_date) {
            return true;
        } else if (id == R.id.action_child_sort_ascending_date) {
            return true;
        } else if (id == R.id.action_child_sort_descending_date) {
            return true;
        } else if (id == R.id.action_child_sort_ascending_name) {
            return true;
        } else if (id == R.id.action_child_sort_descending_name) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        ArrayList<DatabaseObject> data = new ArrayList<>();

        // Update selected/deselected MenuItems
        if (mActiveMenuItem != null)
            mActiveMenuItem.setChecked(false);
        mActiveMenuItem = item;
        item.setChecked(true);

        // Handle navigation view item clicks here.
        mActiveNavMenuItemId = item.getItemId();
        mData = loadMainContent(mActiveNavMenuItemId);
        if (mAdapter != null) {
            mAdapter.update(mData);
        }
        invalidateOptionsMenu();
        return true;
    }

    public ArrayList<? extends DatabaseObject> loadMainContent(int menuItemId) {

        String actionBarTitle = "";
        String emptyStateMessage = "";
        ArrayList<? extends DatabaseObject> data = new ArrayList<>();

        switch (menuItemId) {
            case R.id.nav_recordings:
                data = mDBAdapter.getParentRecords(false);
                emptyStateMessage = "You have no new recordings";
                actionBarTitle  = "Recordings";
                break;
            case R.id.nav_messages:
                data = mDBAdapter.getParentRecords(false);
                break;
            case R.id.nav_trash:
                data = mDBAdapter.getParentRecords(true);
                emptyStateMessage = "Trash is empty";
                actionBarTitle = "Trash";
                break;
            default:
                break;
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
        }

        // set the watched parent to 0 (not watching children)
        mWatchedParentId = 0;

        // set empty view's title
        setEmptyViewTitle(emptyStateMessage);

        mDrawer.closeDrawer(GravityCompat.START);

        return data;
    }

    private void setEmptyViewTitle(String title) {
        mEmptyViewText.setText(title);
    }

}
