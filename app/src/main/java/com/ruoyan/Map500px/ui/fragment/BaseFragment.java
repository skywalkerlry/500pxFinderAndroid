package com.ruoyan.map500px.ui.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import at.markushi.ui.CircleButton;


/**
 * Created by ruoyan on 2/19/15.
 */
public class BaseFragment extends Fragment implements OnMapReadyCallback {

    public static final String USER_LATITUDE = "latitude";
    public static final String USER_LONGITUDE = "longitude";
    public static final String EXPLORE_MODE = "mode";
    public static final String FROM_MENU = "from_menu";
    public static final String RADIUS = "radius";
    public static final double INIT_SEARCH_RADIUS = 3.5;
    public static final int THUMBNAIL_SIZE = 3;
    public static final int REQUEST_TIMEOUT = 10000;
    public static final float INITIAL_DISPLAY_RANGE = 12;
    public static final double EQUATOR_LENGTH = 40075004;
    private static final double MILE_TO_METER = 1609.344;
    public static final double KILOMETER_TO_LATITUDE_DEGREE = 111.3;

    public static GoogleMap map = null;
    public static int screenWidth;
    public static int screenHeight;

    public static ProgressBar mProgressBar;
//    public static ImageButton iButton;
    public static CircleButton iButton;

    public static double currentLatitude;
    public static double currentLongitude;
    public static double currentZoom;

    public static boolean isExplore;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getScreenWidth();
        Thread timer = new Thread() {
            public void run () {
                for (;;) {
                    // do stuff in a separate thread
                    uiCallback.sendEmptyMessage(0);
                    try {
                        Thread.sleep(3000);    // sleep for 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
 //       timer.start();
    }

    private Handler uiCallback = new Handler () {
        public void handleMessage (Message msg) {
            Log.i("camera target", map.getCameraPosition().target.toString());
        }
    };

    public double zoomToRadius(double latitude, double zoom) {
        double arg = Math.pow(Math.E,zoom*Math.log(2));
        double latitudinalAdjustment = Math.cos(Math.PI*latitude/180.0);
        double radius = EQUATOR_LENGTH*screenWidth*latitudinalAdjustment/(arg*256*MILE_TO_METER*4);
        return radius;
    }

    public double distanceToLongitudeOffset(double distance, double latitude) {
        return 360*distance*1000*1.5/(EQUATOR_LENGTH*Math.cos(Math.PI*latitude/180.0));
    }

    private void getScreenWidth() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

    public void refreshCamera() {
        currentLatitude = map.getCameraPosition().target.latitude;
        currentLongitude = map.getCameraPosition().target.longitude;
        currentZoom = map.getCameraPosition().zoom;
    }

}
