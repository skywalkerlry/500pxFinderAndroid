package com.ruoyan.map500px.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.bean.UserLocation;

/**
 * Created by ruoyan on 2/18/15.
 */
public class MyMapFragment extends BaseFragment {

    private double userLatitude;
    private double userLongitude;

    public static MyMapFragment newInstance(UserLocation location) {
        MyMapFragment fragment = new MyMapFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(USER_LATITUDE, location.getUserLocation().get("latitude"));
        bundle.putDouble(USER_LONGITUDE, location.getUserLocation().get
                ("longitude"));

        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        userLatitude = bundle.getDouble(USER_LATITUDE);
        userLongitude = bundle.getDouble(USER_LONGITUDE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_map, null);
        parseArgument();
        MapFragment mapFragment = (MapFragment)getActivity().getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return contentView;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        initMapParam();
    }

    private void initMapParam() {
        map.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(userLatitude,
                userLongitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                INITIAL_DISPLAY_RANGE));
    }
}
