package com.ruoyan.map500px.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.android.ui.IconGenerator;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.bean.PhotoInfo;
import com.ruoyan.map500px.bean.UserLocation;
import com.ruoyan.map500px.data.LocalDataSource;
import com.ruoyan.map500px.data.RequestManager;
import com.ruoyan.map500px.data.RequestUrl;
import com.ruoyan.map500px.ui.ImageViewActivity;
import com.ruoyan.map500px.ui.MainActivity;
import com.ruoyan.map500px.ui.adapter.DrawerAdapter;
import com.ruoyan.map500px.ui.listener.RecyclerItemClickListener;
import com.ruoyan.map500px.utils.JsonUtils;
import com.ruoyan.map500px.utils.TaskUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruoyan on 2/17/15.
 */
public class DrawerFragment extends BaseFragment{

    private RecyclerView mRecyclerView;
    private DrawerAdapter mAdapter;
    private MainActivity mActivity;
    private String userLatitude;
    private String userLongitude;
    private boolean fromMenu;
    private double searchRadius;
    private List<Map<String,Object>> photoInfoList;
    private List<String> thumbnailUrlList;
    private List<String> favoriteIdList;
    private IconGenerator mGenerator;
    private LocalDataSource dataSource;

    public static DrawerFragment newInstance(UserLocation location, boolean exploreMode,
                                             boolean fromMenu) {
        DrawerFragment fragment = new DrawerFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXPLORE_MODE, exploreMode);
        bundle.putBoolean(FROM_MENU, fromMenu);
        if (location != null) {
            bundle.putString(USER_LATITUDE, Double.toString(location.getUserLocation().get
                    ("latitude")));
            bundle.putString(USER_LONGITUDE, Double.toString(location.getUserLocation().get
                    ("longitude")));
            bundle.putString(RADIUS, Double.toString(location.getUserLocation().get
                    ("radius")));
        }
        else {
            bundle.putString(USER_LATITUDE, "0");
            bundle.putString(USER_LONGITUDE, "0");
            bundle.putString(RADIUS, Double.toString(EQUATOR_LENGTH / 10));
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        isExplore = bundle.getBoolean(EXPLORE_MODE);
        fromMenu = bundle.getBoolean(FROM_MENU);
        userLatitude = bundle.getString(USER_LATITUDE);
        userLongitude = bundle.getString(USER_LONGITUDE);
        searchRadius = Double.valueOf(bundle.getString(RADIUS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        dataSource = new LocalDataSource(mActivity);
        View contentView = inflater.inflate(R.layout.fragment_drawer, null);
        photoInfoList = new ArrayList<>();
        thumbnailUrlList = new ArrayList<>();
        favoriteIdList = new ArrayList<>();
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(thumbnailUrlList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener
                        .OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                        if (isExplore) {
                            double imageId = (double) photoInfoList.get(position).get("id");
                            BigDecimal imageIdDecimal = new BigDecimal(imageId);
                            intent.putExtra(ImageViewActivity.IMAGE_ID, imageIdDecimal.toString());
                        }
                        else {
                            intent.putExtra(ImageViewActivity.IMAGE_ID,
                                    favoriteIdList.get(position));
                        }
                        intent.putExtra(ImageViewActivity.THUMBNAIL_URL,
                                thumbnailUrlList.get(position));
                        startActivity(intent);
                    }
                })
        );

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        parseArgument();
        if (searchRadius == 0)
            searchRadius = INIT_SEARCH_RADIUS;
        
        if (isExplore) {
            try {
                map.setOnCameraChangeListener(null);
            }catch (Exception e) {
            }

            if (!fromMenu)
                requestImageInfo(THUMBNAIL_SIZE); //request thumbnail images first
            else
                map.clear();
            iButton.setVisibility(View.VISIBLE);
        }
        else {
            refreshCamera();
            showFavorites();
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    refreshCamera();
                    showFavorites();
                }
            });
        }
        
        return contentView;
    }

    private void showFavorites() {
        iButton.setVisibility(View.GONE);
        map.clear();
        thumbnailUrlList.clear();
        favoriteIdList.clear();
        List<PhotoInfo> favoritePhotoList = dataSource.findAllFavorites();
        int order = 0;
        for (PhotoInfo photo : favoritePhotoList) {
            if (isPhotoInView(photo)) {
                addMakerOfThisPhoto(photo,order);
                order++;
                thumbnailUrlList.add(photo.getThumbnailUrl());
                favoriteIdList.add(photo.getId());
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void addMakerOfThisPhoto(PhotoInfo photo, int order) {
        mGenerator = new IconGenerator(mActivity);
        mGenerator.setColor(mActivity.getResources().getColor(R.color.trans_pink));
        mGenerator.setTextAppearance(R.style.WihteText);
        mGenerator.setContentPadding(5,0,5,0);
        Bitmap iconBitmap = mGenerator.makeIcon(Integer.toString(order + 1));
        map.addMarker(new MarkerOptions().position(new LatLng(photo.getLatitude(),
                        photo.getLongitude())).icon(BitmapDescriptorFactory
                        .fromBitmap(iconBitmap)));
    }

    private boolean isPhotoInView(PhotoInfo photo) {
        double radius = zoomToRadius(currentLatitude,currentZoom);
        double latitudeOffset = radius/KILOMETER_TO_LATITUDE_DEGREE*screenHeight/screenWidth;
        if (photo.getLatitude()<currentLatitude+latitudeOffset && photo.getLatitude()
                >currentLatitude-latitudeOffset) {
            double longitudeOffset = distanceToLongitudeOffset(radius, currentLatitude);
            if (currentLongitude+longitudeOffset>180) {
                if ((photo.getLongitude()<currentLongitude+longitudeOffset-360 && photo
                        .getLongitude()>-180) || (photo.getLongitude()
                        >currentLongitude-longitudeOffset && photo.getLongitude()<180))
                    return true;
                else
                    return false;
            }
            else if (currentLongitude-longitudeOffset<-180) {
                if ((photo.getLongitude()>currentLongitude-longitudeOffset+360 && photo
                        .getLongitude()<180) || (photo.getLongitude()
                        <currentLongitude+longitudeOffset && photo.getLongitude()>-180))
                    return true;
                else
                    return false;
            }
            else {
                if (photo.getLongitude()<currentLongitude+longitudeOffset && photo.getLongitude()
                        >currentLongitude-longitudeOffset)
                    return true;
                else
                    return false;
            }

        }
        else
            return false;
    }

    private void requestImageInfo(int imageSize) {
        RequestUrl requestUrl = new RequestUrl();
        String requestImg = requestUrl.getUrlOfCurrentSettings(mActivity,
                userLatitude,userLongitude,
                searchRadius,
                imageSize);
        //Log.i("request", requestImg);
        StringRequest stringRequest = new StringRequest(requestImg,
                infoListener(),
                errorListener());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT,1,1.0f));

        RequestManager.getInstance().addToRequestQueue(stringRequest);
    }

    private Response.Listener<String> infoListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... inputs) {
                        List<Object> photoList = parseJsonToPhotoList(response);
                        photoInfoList.clear();
                        thumbnailUrlList.clear();
                        if (photoList != null) {
                            for (int i = 0; i < photoList.size(); i++) {
                                Map<String, Object> params = getMappedParams(photoList, i);
                                if (params != null) {
                                    photoInfoList.add(params);
                                    thumbnailUrlList.add(params.get("image_url").toString());
                                }
                            }
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        postProgressAction();
                    }
                });
            }
        };
    }

    private void postProgressAction() {
        mProgressBar.setVisibility(View.GONE);
        iButton.setClickable(true);
        mActivity.unlockDrawer();
        mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getActionBar().setHomeButtonEnabled(true);
        addMarkerOnMap();
        mAdapter.notifyDataSetChanged();
    }

    private void addMarkerOnMap() {
        mGenerator = new IconGenerator(mActivity);
        mGenerator.setColor(mActivity.getResources().getColor(R.color.deep_purple));
        mGenerator.setTextAppearance(R.style.WihteText);
        mGenerator.setContentPadding(5,0,5,0);
        if (photoInfoList.size()>0) {
            for (int i=0; i<photoInfoList.size(); i++) {
                Map<String,Object> params = photoInfoList.get(i);
                Bitmap iconBitmap = mGenerator.makeIcon(Integer.toString(i + 1));
                map.addMarker(new MarkerOptions().position(new LatLng((double) params.get("latitude"),
                        (double) params.get("longitude"))).icon(BitmapDescriptorFactory
                        .fromBitmap(iconBitmap)));
            }
        }
    }

    private List<Object> parseJsonToPhotoList(String feed) {
        Map<String, Object> feedMap = JsonUtils.getMappedData(feed);
        List<Object> photoList = null;
        if (feedMap != null) {
            if (feedMap.containsKey("photos")) {
                photoList = (ArrayList)feedMap.get
                        ("photos");
            }
        }
        return photoList;
    }

    private Map<String,Object> getMappedParams(List<Object> list, int i) {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        if (((LinkedTreeMap)list.get(i)).get("latitude")==null)
            return null;
        double id = (double)((LinkedTreeMap)list.get(i)).get("id");
        double latitude = (double)((LinkedTreeMap)list.get(i)).get("latitude");
        double longitude = (double)((LinkedTreeMap)list.get(i)).get("longitude");
        String imageUrl = (String)((LinkedTreeMap)list.get(i)).get("image_url");

        paramMap.put("id", id);
        paramMap.put("latitude",latitude);
        paramMap.put("longitude", longitude);
        paramMap.put("image_url", imageUrl);
        return paramMap;
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getActivity(),
                                "Oops. Server not responding, please try again later",
                                Toast.LENGTH_LONG).show();
                        postProgressAction();
                    }
                }
            }
        };
    }

}
