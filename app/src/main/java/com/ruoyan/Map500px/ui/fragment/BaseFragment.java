package com.ruoyan.map500px.ui.fragment;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * Created by ruoyan on 2/19/15.
 */
public class BaseFragment extends Fragment implements OnMapReadyCallback {

    public static final String USER_LATITUDE = "latitude";
    public static final String USER_LONGITUDE = "longitude";
    public static final int INIT_SEARCH_RADIUS = 5;
    public static final int THUMBNAIL_SIZE = 3;
    public static final float INITIAL_DISPLAY_RANGE = 12;

    public static GoogleMap map = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
