package com.ruoyan.map500px.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.bean.UserLocation;
import com.ruoyan.map500px.ui.MainActivity;

/**
 * Created by ruoyan on 2/18/15.
 */
public class MyMapFragment extends BaseFragment implements View.OnClickListener{

    public double userLatitude;
    public double userLongitude;
    public double radius;

    private MainActivity mainActivity;
    private View contentView;
    private SupportMapFragment fragment;

    public static MyMapFragment newInstance(UserLocation location) {
        MyMapFragment fragment = new MyMapFragment();
        Bundle bundle = new Bundle();
        if (location != null) {
            bundle.putDouble(USER_LATITUDE, location.getUserLocation().get("latitude"));
            bundle.putDouble(USER_LONGITUDE, location.getUserLocation().get
                    ("longitude"));
            bundle.putDouble(RADIUS, location.getUserLocation().get("radius"));
        }
        else {
            bundle.putDouble(USER_LATITUDE, 0);
            bundle.putDouble(USER_LONGITUDE, 0);
            bundle.putDouble(RADIUS, 0);
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        userLatitude = bundle.getDouble(USER_LATITUDE);
        userLongitude = bundle.getDouble(USER_LONGITUDE);
        radius = bundle.getDouble(RADIUS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        contentView = inflater.inflate(R.layout.fragment_map,container,false);
        parseArgument();

        iButton = (ImageButton)contentView.findViewById(R.id.refresh_button);
        iButton.setOnClickListener(this);
        iButton.setClickable(false);
        mProgressBar = (ProgressBar)contentView.findViewById(R.id.google_progress);
        mProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(getActivity()).build());
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fragment.getMapAsync(this);
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            map = fragment.getMap();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        super.onMapReady(googleMap);
        map = googleMap;
        initMapParam();

    }

    private void initMapParam() {
        map.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(userLatitude,
                userLongitude);
        if(radius == 0)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                INITIAL_DISPLAY_RANGE));
        else
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    (float)radius));
    }

    @Override
    public void onClick(View v) {
        map.clear();
        iButton.setClickable(false);
        mainActivity.lockDrawer();
        mainActivity.getActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getActionBar().setHomeButtonEnabled(false);
        if (!mProgressBar.isShown())
            mProgressBar.setVisibility(View.VISIBLE);
        double currentLatitude = map.getCameraPosition().target.latitude;
        double currentLongitude = map.getCameraPosition().target.longitude;
        double currentZoom = map.getCameraPosition().zoom;
        UserLocation userLocation = new UserLocation(currentLatitude,
                currentLongitude,zoomToRadius(currentLatitude,currentZoom));
        mainActivity.initDrawer(userLocation);
    }

}
