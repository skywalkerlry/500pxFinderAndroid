package com.ruoyan.map500px.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import com.ruoyan.map500px.R;
import com.ruoyan.map500px.ui.fragment.DrawerFragment;
import com.ruoyan.map500px.ui.fragment.MyMapFragment;


public class MainActivity extends BaseActivity{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.argb(100, 0, 0, 0));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setTitle("Swipe");

    }

    protected void replaceFragment(int viewId, android.support.v4.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        initMap();
        initDrawer();
    }
    
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
    }

    private void initMap() {
        replaceFragment(R.id.content_frame, MyMapFragment.newInstance(userLocation));
    }

    private void initDrawer() {
        replaceFragment(R.id.left_drawer, DrawerFragment.newInstance(userLocation));
    }

}

