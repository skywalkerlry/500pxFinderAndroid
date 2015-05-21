package com.ruoyan.map500px.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.ruoyan.map500px.R;
import com.ruoyan.map500px.bean.UserLocation;
import com.ruoyan.map500px.ui.fragment.DrawerFragment;
import com.ruoyan.map500px.ui.fragment.MyMapFragment;

import java.lang.reflect.Field;


public class MainActivity extends BaseActivity{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean firstStart;
    private Menu mMenu;
    private boolean exploreMode;

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
        lockDrawer();
        setTitle("Swipe");
        firstStart = true;
        exploreMode = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.favorite_mode).setIcon(R.drawable.ic_action_action_favorite);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Field field = menu.getClass().
                            getDeclaredField("mOptionalIconsVisible");
                    field.setAccessible(true);
                    field.setBoolean(menu, true);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.favorite_mode:
                if (exploreMode) {
                    exploreMode = false;
                    mMenu.findItem(R.id.favorite_mode).setIcon(R.drawable
                            .ic_action_action_explore).setTitle("Go exploring");
                    initDrawer(null,exploreMode,true);
                }
                else {
                    exploreMode = true;
                    mMenu.findItem(R.id.favorite_mode).setIcon(R.drawable
                            .ic_action_action_favorite).setTitle("My favorites");
                    initDrawer(userLocation,exploreMode,true);
                }
        }

        return super.onOptionsItemSelected(item);
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
        if (firstStart) {
            initMap(userLocation);
            initDrawer(userLocation,exploreMode,false);
            firstStart = false;
        }
    }
    
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
    }

    public void initMap(UserLocation userLocation) {
        replaceFragment(R.id.content_frame, MyMapFragment.newInstance(userLocation));
    }

    public void initDrawer(UserLocation userLocation,boolean isExplore,boolean fromMenu) {
        replaceFragment(R.id.left_drawer, DrawerFragment.newInstance(userLocation,isExplore,fromMenu));
    }

    public void unlockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void lockDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

}

