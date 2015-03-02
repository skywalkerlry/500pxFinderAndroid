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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.android.ui.IconGenerator;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.api.Api500px;
import com.ruoyan.map500px.bean.UserLocation;
import com.ruoyan.map500px.data.RequestManager;
import com.ruoyan.map500px.ui.ImageViewActivity;
import com.ruoyan.map500px.ui.MainActivity;
import com.ruoyan.map500px.ui.adapter.DrawerAdapter;
import com.ruoyan.map500px.ui.listener.RecyclerItemClickListener;
import com.ruoyan.map500px.utils.JsonUtils;
import com.ruoyan.map500px.utils.TaskUtils;

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
    private double searchRadius;
    private List<Map<String,Object>> photoInfoList;
    private IconGenerator mGenerator;

    public static DrawerFragment newInstance(UserLocation location) {
        DrawerFragment fragment = new DrawerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_LATITUDE,Double.toString(location.getUserLocation().get
                ("latitude")));
        bundle.putString(USER_LONGITUDE,Double.toString(location.getUserLocation().get
                ("longitude")));
        bundle.putString(RADIUS,Double.toString(location.getUserLocation().get
                ("radius")));

        fragment.setArguments(bundle);
        return fragment;
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        userLatitude = bundle.getString(USER_LATITUDE);
        userLongitude = bundle.getString(USER_LONGITUDE);
        searchRadius = Double.valueOf(bundle.getString(RADIUS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View contentView = inflater.inflate(R.layout.fragment_drawer, null);
        photoInfoList = new ArrayList<>();
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(photoInfoList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener
                        .OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String largeImageUrl = photoInfoList.get(position).get("full_size_image_url").toString();
                        Intent intent = new Intent(getActivity(), ImageViewActivity.class);
                        intent.putExtra(ImageViewActivity.IMAGE_URL, largeImageUrl);
//                        intent.putExtra(ImageViewActivity.IMAGE_ORDER, position);
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

        requestImageInfo(THUMBNAIL_SIZE); //request thumbnail images first

        return contentView;
    }

    private void requestImageInfo(int imageSize) {
        String requestImg = Api500px.HOST + Api500px.SORT_RATING + "&geo=" +
                userLatitude + "%2C" + userLongitude
                + "%2C" + Double.toString(searchRadius) + "mi" + "&image_size=" + Integer.toString
                (imageSize) +
                "&consumer_key=" + Api500px
                .getConsumerKey();

        StringRequest stringRequest = new StringRequest(requestImg,
                infoListener(imageSize),
                errorListener());

        RequestManager.getInstance().addToRequestQueue(stringRequest);
    }

    private Response.Listener<String> infoListener(final int imageSize) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... inputs) {
                        List<Object> photoList = parseJsonToPhotoList(response);
                        if (imageSize == THUMBNAIL_SIZE) {
                            photoInfoList.clear();
                            if (photoList != null) {
                                for (int i = 0; i < photoList.size(); i++) {
                                    Map<String, Object> params = getMappedParams(photoList, i);
                                    if (params != null) {
                                        photoInfoList.add(params);
                                    }
                                }
                                requestImageInfo(FULL_IMAGE_SIZE);
                            }
                        }
                        else if (imageSize == FULL_IMAGE_SIZE) {
                            if (photoList != null) {
                                for (int i = 0; i < photoList.size(); i++) {
                                    String fullSizeImageUrl = (String)((LinkedTreeMap)photoList.get
                                            (i)).get
                                            ("image_url");
                                    photoInfoList.get(i).put("full_size_image_url",
                                            fullSizeImageUrl);
                                }

                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if (imageSize == THUMBNAIL_SIZE) {
                            mProgressBar.setVisibility(View.GONE);
                            iButton.setClickable(true);
                            mActivity.unlockDrawer();
                            addMarkerOnMap();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        };
    }

    private void addMarkerOnMap() {
        mGenerator = new IconGenerator(getActivity());
        mGenerator.setColor(getResources().getColor(R.color.deep_purple));
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
        double latitude = (double)((LinkedTreeMap)list.get(i)).get("latitude");
        double longitude = (double)((LinkedTreeMap)list.get(i)).get("longitude");
        String imageUrl = (String)((LinkedTreeMap)list.get(i)).get("image_url");

        paramMap.put("latitude",latitude);
        paramMap.put("longitude", longitude);
        paramMap.put("image_url", imageUrl);
        return paramMap;
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ToastUtils.showLong(error.getMessage());
            }
        };
    }

}
