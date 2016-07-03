package com.example.ronylevari.callrecorder;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ronylevari.callrecorder.constants.AppConstants;
import com.example.ronylevari.callrecorder.fragments.FragmentNoContent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "Main Activity";
    private MenuItem mActiveMenuItem;
    private int mActiveMenuItemId;
    private boolean mFirstUpload;
    private boolean mIsContentAvailable;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // set up UI toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // initialize fab onclick listener
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        // load saved navigation state if present
        if (savedInstanceState != null) {
            mActiveMenuItemId = savedInstanceState.getInt(AppConstants.SAVE_NAV_MENU_OPTION);
        } else {
            mActiveMenuItemId = R.id.nav_recordings;
        }
        mNavigationView.getMenu().findItem(mActiveMenuItemId).setChecked(true);

        // load content according to navigation state
        displayMainContent(mActiveMenuItemId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save navigation menu id to initialize the right content
        outState.putInt(AppConstants.SAVE_NAV_MENU_OPTION, mActiveMenuItemId);
        Log.d(TAG, "active navigation item saved");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_ascending) {
            return true;
        } else if (id == R.id.action_sort_descending) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Update selected/deselected MenuItems
        if (mActiveMenuItem != null)
            mActiveMenuItem.setChecked(false);
        mActiveMenuItem = item;
        item.setChecked(true);

        // Handle navigation view item clicks here.
        mActiveMenuItemId = item.getItemId();

        displayMainContent(mActiveMenuItemId);

//       if (mActiveMenuItemId == R.id.nav_recordings) {
//            Toast toast = Toast.makeText(this, "messages", Toast.LENGTH_SHORT);
//            toast.show();
//        } else if (mActiveMenuItemId == R.id.nav_messages) {
//            Toast toast = Toast.makeText(this, "archive", Toast.LENGTH_SHORT);
//            toast.show();
//        } else if (mActiveMenuItemId == R.id.nav_archive) {
//            Toast toast = Toast.makeText(this, "trash", Toast.LENGTH_SHORT);
//            toast.show();
//        } else if (mActiveMenuItemId == R.id.nav_trash) {
//           Toast toast = Toast.makeText(this, "help", Toast.LENGTH_SHORT);
//           toast.show();
//       } else if (mActiveMenuItemId == R.id.nav_help) {
//           Toast toast = Toast.makeText(this, "help", Toast.LENGTH_SHORT);
//           toast.show();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayMainContent(int menuItemId) {

        Fragment fragment = null;
        String title = "";
        Bundle bundle = new Bundle();
        String message = "";

        switch (menuItemId) {
            case R.id.nav_recordings:
                message = "You have no new recordings";
                bundle.putString(AppConstants.NO_CONTENT_TITLE, "You have no new recordings");
                title  = "Recordings";
                break;
            case R.id.nav_messages:
                message = "You have no new recordings";
                bundle.putString(AppConstants.NO_CONTENT_TITLE, "You have no new recordings");
                title = "Default Return message";
                break;
            case R.id.nav_archive:
                message = "You have no archived items";
                bundle.putString(AppConstants.NO_CONTENT_TITLE, "You have no archived items");
                title  = "Archive";
                break;
            case R.id.nav_trash:
                message = "Trash is empty";
                bundle.putString(AppConstants.NO_CONTENT_TITLE, "Trash is empty");
                title = "Trash";
                break;
            default:
                message = "You have no new recordings";
                bundle.putString(AppConstants.NO_CONTENT_TITLE, "You have no new recordings");
                title = "Recordings";
                break;
        }

        fragment = FragmentNoContent.newInstance(message);

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}
